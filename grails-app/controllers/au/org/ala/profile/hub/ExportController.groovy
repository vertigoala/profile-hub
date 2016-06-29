package au.org.ala.profile.hub

import au.org.ala.profile.analytics.Analytics
import grails.converters.JSON
import org.springframework.web.context.request.RequestContextHolder

class ExportController extends BaseController {

    ProfileService profileService
    ExportService exportService

    @Analytics
    def getPdf() {
        if (!params.profileId || !params.opusId) {
            badRequest "profileId and opusId are required parameters"
        } else {
            boolean latest = params.isOpusReviewer || params.isOpusEditor || params.isOpusAdmin
            def model = profileService.getProfile(params.opusId as String, params.profileId as String, latest)
            if (!model) {
                notFound()
            } else {
                if (params.children && params.email) {
                    // By default, the RequestAttributes thread local used by Grails/Spring is not inheritable, so new threads
                    // will not have access to the request context when calling web services. This line works around this issue
                    // by resetting the request attributes with the inheritable flag set to true, meaning spawned threads will
                    // inherit the state.
                    RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true)
                    Map config = extractOptionsFromParams()
                    config.opusTitle = model.opus.title
                    exportService.createPdfAsych(config, latest)

                    render [:] as JSON
                } else {
                    response.contentType = 'application/pdf'
                    response.setHeader 'Content-disposition', "attachment; filename=\"${model.profile.scientificName.replaceAll(/\\W/, '')}.pdf\""
                    exportService.createPdf(extractOptionsFromParams(), { response.outputStream }, latest)
                    response.outputStream.flush()
                }
            }
        }
    }

    def extractOptionsFromParams() {
        [
                profileId   : params.profileId,
                opusId      : params.opusId,
                attributes  : params.attributes,
                map         : params.map,
                nomenclature: params.nomenclature,
                taxonomy    : params.taxonomy || params.taxon,
                key         : params.key,
                bibliography: params.bibliography,
                links       : params.links,
                bhllinks    : params.bhllinks,
                specimens   : params.specimens,
                conservation: params.conservation,
                status      : params.features,
                images      : params.images,
                children    : params.children,
                email       : params.email
        ]
    }


}


