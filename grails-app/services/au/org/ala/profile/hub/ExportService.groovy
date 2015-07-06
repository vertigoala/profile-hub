package au.org.ala.profile.hub

import grails.converters.JSON
import org.springframework.scheduling.annotation.Async
import org.springframework.web.context.request.RequestContextHolder

import java.util.concurrent.ConcurrentLinkedQueue

import static groovyx.gpars.GParsPool.*

class ExportService {

    private static final int THREAD_COUNT = 10

    ProfileService profileService
    BiocacheService biocacheService
    WebService webService
    EmailService emailService
    def wkhtmltoxService
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

        if (params.nomenclature && model.profile.nslNameIdentifier) {
            model.profile.nomenclatureHtml = webService.get("${grailsApplication.config.nsl.name.url.prefix}${model.profile.nslNameIdentifier}", false)?.resp?.replaceAll("&", "&amp;")
        }

        String occurrenceQuery = createOccurrenceQuery(model.profile, opus)
        model.profile.mapImageUrl = createMapImageUrl(opus, occurrenceQuery)

        model
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

    byte[] createPdf(Map params) {
        def model = [
                options          : params,
//                grailsApplication: grailsApplication,
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

        (model as JSON).toString(true)

        wkhtmltoxService.makePdf(
                view: "/pdf/_profile",
//                footer: "/pdf/_pdfFooter",
//                header: "/pdf/_pdfHeader",
                model: model,
                marginLeft: 15,
                marginTop: 20,
                marginBottom: 20,
                marginRight: 15,
                headerSpacing: 10,
        )
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
}
