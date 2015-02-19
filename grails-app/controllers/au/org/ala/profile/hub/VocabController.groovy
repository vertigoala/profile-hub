package au.org.ala.profile.hub

class VocabController {

    ProfileService profileService

    def show() {
        profileService.getVocab(params.vocabId)
    }
}
