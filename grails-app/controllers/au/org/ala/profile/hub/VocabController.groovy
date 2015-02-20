package au.org.ala.profile.hub

class VocabController extends BaseController {

    ProfileService profileService

    def show() {
        profileService.getVocab(params.vocabId)
    }
}
