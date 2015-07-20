package au.org.ala.profile.hub

import grails.converters.JSON
import grails.transaction.NotTransactional
import net.sf.jasperreports.engine.data.JsonDataSource
import net.sf.jasperreports.engine.util.SimpleFileResolver
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.springframework.scheduling.annotation.Async
import org.springframework.web.context.request.RequestContextHolder

import java.util.concurrent.ConcurrentLinkedQueue

import static groovyx.gpars.GParsPool.withPool

class ExportService {

    private static final int THREAD_COUNT = 10
    def transactional = false

    ProfileService profileService
    BiocacheService biocacheService
    ImageService imageService
    WebService webService
    EmailService emailService
    NslService nslService
    JasperNonTransactionalService jasperNonTransactionalService
    def grailsApplication

    Map statusRegions = [
            ""                            : "IUCN",
            "IUCN"                        : "IUCN",
            "Australia"                   : "AU",
            "Australian Capital Territory": "ACT",
            "New South Wales"             : "NSW",
            "Northern Territory"          : "NT",
            "Queensland"                  : "QLD",
            "South Australia"             : "SA",
            "Tasmania"                    : "TAS",
            "Victoria"                    : "VIC",
            "Western Australia"           : "WA"
    ]


    @Async
    void createPdfAsych(Map params, boolean latest = false) {
        try {
            byte[] pdf = createPdf(params, latest)

            String filename = UUID.randomUUID()
            File f = new File("${grailsApplication.config.temp.file.location}/${filename}.pdf")
            f << pdf
            String url = "${grailsApplication.config.grails.serverURL}/opus/${params.opusId}/profile/${params.profileId}/file/${filename}.pdf"
            emailService.sendEmail(params.email, "${params.opusTitle}<no-reply@ala.org.au>", "Your file is ready to download",
                    """<html><body>
                        <p>The PDF you requested from ${params.opusTitle} can now be downloaded from <a href='${url}'>this url</a>.</p>
                        <p>Please note that this file will only remain on the server for a few days.</p>
                        <p>This is an automated email. Please do not reply.</p>
                        </body></html>""")
        } catch (Exception e) {
            log.error("Failed to generated PDF", e)
            emailService.sendEmail(params.email, "${params.opusTitle}<no-reply@ala.org.au>", "PDF generation failed", "We are sorry to inform you that an error occurred while generating the PDF you requested. Please try again.")
        }
    }

    @NotTransactional
    byte[] createPdf(Map params, boolean latest = false) {

        // Create curated report model
        Map curatedModel = getCurateReportModel(params, latest)

        // Transform curated model to JSON format input stream
        InputStream inputStream = IOUtils.toInputStream((curatedModel as JSON).toString())

        // Runtime classpath directory where the reports are available
        File reportsDir = new File(grailsApplication.mainContext.getResource('classpath:reports/profiles/PROFILES.jrxml').URL.file.replaceFirst("/[\\w_]+.jrxml\$", ""))

        // Generate report and return byte array
        JasperReportDef reportDef = new JasperReportDef(
                name: 'profiles/PROFILES.jrxml',
                fileFormat: JasperExportFormat.PDF_FORMAT,
                dataSource: new JsonDataSource(inputStream),
                parameters: [
                        'PROFILES_REPORT_OPTIONS': curatedModel.options,
                        'REPORT_FILE_RESOLVER': new SimpleFileResolver(reportsDir)
                ]
        )

        return jasperNonTransactionalService.generateReport(reportDef).toByteArray()
    }

    /**
     *
     * @param params
     * @param latest
     * @return
     */
    private Map getCurateReportModel(Map params, boolean latest = false) {
        def curatedModel = [
                options : params,
                profiles: [] as ConcurrentLinkedQueue
        ]

        def opus = webService.get("${grailsApplication.config.profile.service.url}/opus/${URLEncoder.encode(params.opusId, "UTF-8")}")?.resp
        curatedModel.profiles << loadProfileData(params.profileId as String, opus, params, latest)

        if (params.children) {
            def children = profileService.findByNameAndTaxonLevel(params.opusId, curatedModel.profiles[0].profile.rank, curatedModel.profiles[0].profile.scientificName, "99999", "0", false)?.resp

            // By default, the RequestAttributes thread local used by Grails/Spring is not inheritable, so new threads
            // will not have access to the request context when calling web services. This line works around this issue
            // by resetting the request attributes with the inheritable flag set to true, meaning spawned threads will
            // inherit the state.
            RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true)

            withPool(THREAD_COUNT) {
                children.eachParallel {
                    if (it.profileId != params.profileId && it.scientificName != params.profileId) {
                        curatedModel.profiles << loadProfileData(it.profileId, opus, params, latest)
                    }
                }
            }

            curatedModel.profiles = curatedModel.profiles.sort { it.profile.scientificName }
        }

        curatedModel.options << [
                allowFineGrainedAttribution: opus.allowFineGrainedAttribution,
                displayToc: curatedModel.profiles?.size() > 1
        ]

        curatedModel << [
                cover: [
                        title       : opus.title,
                        subtitle    : curatedModel.profiles[0]?.profile?.fullName,
                        logo        : opus.logoUrl,
                        banner      : opus.bannerUrl,
                        primaryImage: curatedModel.profiles[0]?.profile?.primaryImage ?: (curatedModel.profiles[0]?.profile?.images?.size() > 0 ? curatedModel.profiles[0]?.profile?.images[0].leftImage.largeImageUrl : '')
                ]
        ]

        return curatedModel
    }

    /**
     *
     * @param profileId
     * @param opus
     * @param params
     * @param latest
     * @return
     */
    private Map loadProfileData(String profileId, opus, Map params, boolean latest = false) {

        Map model = [:]
        model.profile = webService.get("${grailsApplication.config.profile.service.url}/opus/${opus.uuid}/profile/${URLEncoder.encode(profileId, "UTF-8")}?latest=${false}")?.resp

        if (params.taxonomy || params.conservation) {
            model.profile.speciesProfile = profileService.getSpeciesProfile(model.profile.guid)?.resp

            model.profile.speciesProfile?.conservationStatuses?.each {
                it.colour = getColourForStatus(it.status)
                it.regionAbbrev = statusRegions[it.region]
            }

            model.profile.classifications = profileService.getClassification(opus.uuid, params.profileId, model.profile.guid)?.resp
        }

        if (params.specimens) {
            model.profile.specimens = model.profile.specimenIds?.collect {
                def spec = biocacheService.lookupSpecimen(it)?.resp
                [
                        institutionName: spec.processed.attribution.institutionName,
                        collectionName : spec.processed.attribution.collectionName,
                        catalogNumber  : spec.raw.occurrence.catalogNumber,
                ]
            }
        }

        if (params.images) {
            String searchIdentifier = model.profile.guid ? "lsid:" + model.profile.guid : model.profile.scientificName
            model.profile.images = imageService.retrieveImages(opus.uuid, profileId, latest, opus.imageSources.join(","), searchIdentifier)?.resp

            List<Map> groupedImagesInPairs = []
            def images = model.profile.images
            images.eachWithIndex { image, i ->
                if (i % 2 == 0) {
                    image << [
                            imageNumber: i + 1,
                            scientificName: model.profile.scientificName,
                            imageDetailsUrl: "${grailsApplication.config.images.service.url}/image/details?imageId=${image.imageId}"
                    ]
                    Map nextImage = (i + 1 < images.size()) ? images[i + 1] : [:]
                    nextImage << [
                            imageNumber: i + 2,
                            scientificName: model.profile.scientificName,
                            imageDetailsUrl: "${grailsApplication.config.images.service.url}/image/details?imageId=${nextImage.imageId}"
                    ]
                    groupedImagesInPairs << ["leftImage": image, "rightImage": nextImage]
                }
            }

            model.profile.images = groupedImagesInPairs
        }

        // Retrieve occurrences-map image url
        String occurrenceQuery = createOccurrenceQuery(model.profile, opus)
        model.profile.mapImageUrl = createMapImageUrl(opus, occurrenceQuery)

        // Don't make them a String if you want the groovy truth to work (Check Bootstrap.groovy for null values workaround)
        def nslNameIdentifier = model.profile.nslNameIdentifier
        def nslNomenclatureIdentifier = model.profile.nslNomenclatureIdentifier
        // Format nomenclature references
        if (params.nomenclature && nslNameIdentifier && nslNomenclatureIdentifier) {
            model.profile.nomenclature = nslService.getConcept(nslNameIdentifier, nslNomenclatureIdentifier)
            model.profile.nomenclature.citations.each {citation ->
                citation.relationship = stripTextFromNonFormattingHtmlTags(citation.relationship)
            }
        }

        // Filter authors and contributors
        model.profile.acknowledgements = model.profile.authorship?.findAll { it.category != 'Author' }
        model.profile.authorship = model.profile.authorship?.findAll { it.category == 'Author' }.collect {it.text}.join(", ")

        // Format creators and editors
        model.profile.attributes.each {attribute ->
            attribute.creators = attribute?.creators && opus.allowFineGrainedAttribution ? attribute.creators.toArray().join(', ') : ''
            attribute.editors = attribute?.editors && opus.allowFineGrainedAttribution ? attribute.editors.toArray().join(', ') : ''
        }

        // Format conservation status
        if (params.conservation && model.profile.speciesProfile?.conservationStatuses) {
            model.profile.hasConservationStatus = true
            model.profile.speciesProfile.conservationStatuses = model.profile.speciesProfile?.conservationStatuses.sort {it.region}
        } else {
            model.profile.hasConservationStatus = false
        }

        return model
    }

    def createOccurrenceQuery = { profile, opus ->
        String occurrenceQuery;

        if (profile.guid && profile.guid != "null") {
            occurrenceQuery = "lsid:" + profile.guid;
        } else {
            occurrenceQuery = profile.scientificName;
        }

        if (opus.recordSources) {
            occurrenceQuery += " AND (data_resource_uid:" + opus.recordSources.join(" OR data_resource_uid:") + ")"
        }

        occurrenceQuery.encodeAsURL()
    }

    def createMapImageUrl = { opus, occurrenceQuery ->
        Map p = [
                extents      : "96.173828125,-47.11468820158343,169.826171875,-2.5694811631203973",
                outlineColour: 0x000000,
                dpi          : 300,
                scale        : "on",
                baselayer    : "world",
                fileName     : "occurrencemap.jpg",
                format       : "jpg",
                outline      : true,
                popacity     : 1,
                pradiuspx    : 5,
                pcolour      : "00ff85"
        ]

        String url = "${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.wms.image.path}${occurrenceQuery}&"
        p.each { k, v -> url += "${k}=${v}&" }
        if (url.charAt(url.length() - 1) == "&") {
            url = url.substring(0, url.length() - 1)
        }
        url
    }

    def getColourForStatus(status) {
        String colour;

        if (status =~ /extinct$/ || status =~ /wild/) {
            colour = "red";
        } else if (status =~ /Critically/ || status =~ /^Endangered/ || status =~ /Vulnerable/) {
            colour = "yellow";
        } else {
            colour = "green";
        }

        return colour;
    };

    /**
     * Removes all XML/HTML tags that are semantic and not for text formatting
     * At the moment only <i> and <b> are supported. Adding additional ones is trivial now
     * @param html
     * @return
     */
    static String stripTextFromNonFormattingHtmlTags(String html) {
        def regex = ~/<\/?(?!i>)(?!b>)([A-Za-z0-9.'()+,=:\s-])+>/
        return html.replaceAll(regex, "")
    }
}
