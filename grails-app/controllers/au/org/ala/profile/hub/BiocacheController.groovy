package au.org.ala.profile.hub

class BiocacheController extends BaseController {

    BiocacheService biocacheService

    def index() {}

    def lookupSpecimen() {
        if (!params.specimenId) {
            badRequest()
        } else {
            def response = biocacheService.lookupSpecimen(params.specimenId)

            handle response
        }
    }
}
