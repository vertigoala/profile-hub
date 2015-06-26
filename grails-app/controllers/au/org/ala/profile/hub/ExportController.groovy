package au.org.ala.profile.hub

import grails.converters.JSON
import org.springframework.web.context.request.RequestContextHolder

class ExportController extends BaseController {

    ProfileService profileService
    ExportService exportService

    def getPdf() {
        if (!params.profileId || !params.opusId) {
            badRequest "profileId and opusId are required parameters"
        } else {
            def model = profileService.getProfile(params.opusId as String, params.profileId as String)
            if (!model) {
                notFound()
            } else {
                if (params.children) {
                    // By default, the RequestAttributes thread local used by Grails/Spring is not inheritable, so new threads
                    // will not have access to the request context when calling web services. This line works around this issue
                    // by resetting the request attributes with the inheritable flag set to true, meaning spawned threads will
                    // inherit the state.
                    RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true)
                    Map config = extractOptionsFromParams()
                    config.opusTitle = model.opus.title
                    exportService.createPdfAsych(config)

                    render [:] as JSON
                } else {
                    byte[] pdfData = exportService.createPdf(extractOptionsFromParams())

                    if (!pdfData) {
                        notFound()
                    } else {
                        response.contentType = 'application/x-pdf'
                        response.setHeader 'Content-disposition', "attachment; filename=\"${model.profile.scientificName.replaceAll(/\\W/, '')}.pdf\""
                        response.outputStream << pdfData
                        response.outputStream.flush()
                    }
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
                taxonomy    : params.taxonomy,
                bibliography: params.bibliography,
                links       : params.links,
                bhllinks    : params.bhllinks,
                specimens   : params.specimens,
                conservation: params.conservation,
                images      : params.images,
                children    : params.children,
                email       : params.email
        ]
    }


}


