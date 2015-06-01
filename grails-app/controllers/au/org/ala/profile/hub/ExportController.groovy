package au.org.ala.profile.hub

class ExportController extends BaseController {

    ProfileService profileService
    ExportService exportService

    def getPdf() {
        if (!params.profileId) {
            badRequest "profileId is a required parameter"
        } else {
            def model = profileService.getProfile(params.opusId as String, params.profileId as String)
            if (!model) {
                notFound()
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
                images      : params.images
        ]
    }


}


