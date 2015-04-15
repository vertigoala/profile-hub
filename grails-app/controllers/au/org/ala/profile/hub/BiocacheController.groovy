package au.org.ala.profile.hub

class BiocacheController extends BaseController {

    WebService webService

    def index() {}

    def lookupSpecimen() {
        if (!params.specimenId) {
            badRequest()
        } else {
            def response = webService.get("${grailsApplication.config.biocache.base.url}/ws/occurrences/${params.specimenId}")

            handle response
        }
    }
}
