package au.org.ala.profile.hub

import grails.converters.JSON
import grails.transaction.NotTransactional
import net.sf.jasperreports.engine.data.JsonDataSource
import net.sf.jasperreports.engine.util.SimpleFileResolver
import org.apache.commons.io.IOUtils
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.scheduling.annotation.Async
import org.springframework.web.context.request.RequestContextHolder

import java.util.concurrent.ConcurrentLinkedQueue

import static groovyx.gpars.GParsPool.withPool

class ExportService {

    private static final int THREAD_COUNT = 10
    def transactional = false

    ProfileService profileService
    BiocacheService biocacheService
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


    private Map loadProfileData(String profileId, opus, Map params) {
        JSONObject.NULL.metaClass.asBoolean = {-> false}
        JSONObject.NULL.metaClass.toString = {-> ''}
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
            def images = biocacheService.retrieveImages(searchIdentifier, opus.imageSources.join(","))?.resp

            model.profile.images = images?.occurrences?.collect {
                [
                        excluded        : model.profile.excludedImages.contains(it.image),
                        largeImageUrl   : it.largeImageUrl,
                        dataResourceName: it.dataResourceName
                ]
            }
        }

        // Don't make them String if you want the groovy truth to work
        def nslNameIdentifier = model.profile.nslNameIdentifier
        def nslNomenclatureIdentifier = model.profile.nslNomenclatureIdentifier
        if (params.nomenclature && nslNameIdentifier && nslNomenclatureIdentifier) {
            model.profile.nomenclature = nslService.getConcept(nslNameIdentifier, nslNomenclatureIdentifier)
            model.profile.nomenclature.citations.each {citation ->
                citation.relationship = stripHtmlTextFromNonFormattingTags(citation.relationship)
            }
        }

        String occurrenceQuery = createOccurrenceQuery(model.profile, opus)
        model.profile.mapImageUrl = createMapImageUrl(opus, occurrenceQuery)

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

    @Async
    void createPdfAsych(Map params) {
        try {
            byte[] pdf = createPdf(params)

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
    byte[] createPdf(Map params) {
        def model = [
                options          : params,
                profiles         : [] as ConcurrentLinkedQueue
        ]

        model.opus = webService.get("${grailsApplication.config.profile.service.url}/opus/${URLEncoder.encode(params.opusId, "UTF-8")}")?.resp
        model.profiles << loadProfileData(params.profileId as String, model.opus, params)

        if (params.children) {
            def children = profileService.findByNameAndTaxonLevel(params.opusId, model.profiles[0].profile.rank, model.profiles[0].profile.scientificName, "99999", "0", false)?.resp

            // By default, the RequestAttributes thread local used by Grails/Spring is not inheritable, so new threads
            // will not have access to the request context when calling web services. This line works around this issue
            // by resetting the request attributes with the inheritable flag set to true, meaning spawned threads will
            // inherit the state.
            RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true)

            withPool(THREAD_COUNT) {
                children.eachParallel {
                    if (it.profileId != params.profileId && it.scientificName != params.profileId) {
                        model.profiles << loadProfileData(it.profileId, model.opus, params)
                    }
                }
            }

            model.profiles = model.profiles.sort { it.profile.scientificName }
        }

        // Create curated report model
        Map curatedModel = curateModel(model)

        // Transform curated model to JSON format input stream
        InputStream inputStream = IOUtils.toInputStream((curatedModel as JSON).toString())

        File reportsDir = new File(grailsApplication.mainContext.getResource('classpath:reports/profiles/PROFILES.jrxml').URL.file.replaceFirst("/[\\w_]+.jrxml\$", ""))

        // Generate report and return byte array
        JasperReportDef reportDef = new JasperReportDef(
                name: 'profiles/PROFILES.jrxml',
                fileFormat: JasperExportFormat.PDF_FORMAT,
                dataSource: new JsonDataSource(inputStream),
                parameters: [
                        'PROFILES_REPORT_OPTIONS': model.options,
                        'REPORT_FILE_RESOLVER': new SimpleFileResolver(reportsDir)
                ]
        )

        ByteArrayOutputStream baos = jasperNonTransactionalService.generateReport(reportDef)

        return baos.toByteArray()
    }

    private Map curateModel(Map model) {
        Map curatedModel = [
               cover: [
                       title: model.opus.title,
                       subtitle: model.profiles[0]?.profile?.scientificName
               ],
               profiles: model.profiles

        ]

        return curatedModel

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
     *
     * @param html
     * @return
     */
    static String stripHtmlTextFromNonFormattingTags(String html) {
        def regex = ~/<\/?(?!i>)(?!b>)([A-Za-z0-9.'()+,=:\s])+>/
        return html.replaceAll(regex, "")
    }
}
