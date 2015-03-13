package au.org.ala.profile.hub

class VocabController extends BaseController {

    ProfileService profileService

    def show() {
        if (!params.vocabId) {
            badRequest()
        } else {
            def response = profileService.getVocab(params.vocabId)

            handle response
        }
    }

    def update() {
        def json = request.getJSON();
        if (!params.vocabId || !json) {
            badRequest()
        } else {
            def response = profileService.updateVocabulary(params.vocabId, json)

            handle response
        }
    }

    def findUsagesOfTerm() {
        if (!params.vocabId || !params.termName) {
            badRequest()
        } else {
            def response = profileService.findUsagesOfVocabTerm(params.vocabId, params.termName)

            handle response
        }
    }

    def replaceUsagesOfTerm() {
        def json = request.getJSON();
        if (!json) {
            badRequest()
        } else {
            def response = profileService.replaceUsagesOfVocabTerm(json)

            handle response
        }
    }
}
