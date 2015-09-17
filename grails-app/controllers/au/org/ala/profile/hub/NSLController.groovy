package au.org.ala.profile.hub

class NSLController extends BaseController {
    NslService nslService

    def nameDetails() {
        if (!params.nslNameIdentifier) {
            badRequest "nslNameIdentifier is a required parameter"
        } else {
            def response = nslService.getNameDetails(params.nslNameIdentifier)

            handle response
        }
    }

    def listConcepts() {
        if (!params.nslNameIdentifier) {
            badRequest "nslNameIdentifier is a required parameter"
        } else {
            def response = nslService.listConcepts(params.nslNameIdentifier)

            handle response
        }
    }
}
