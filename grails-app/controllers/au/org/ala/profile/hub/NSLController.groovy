package au.org.ala.profile.hub

class NSLController extends BaseController {
    NslService nslService

    def listConcepts() {
        if (!params.nslNameIdentifier) {
            badRequest "nslNameIdentifier is a required parameter"
        } else {
            def response = nslService.listConcepts(params.nslNameIdentifier)

            handle response
        }
    }
}
