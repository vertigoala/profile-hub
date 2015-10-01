package au.org.ala.profile.hub

import grails.converters.JSON

class KeybaseController extends BaseController {

    ProfileService profileService
    KeybaseService keybaseService

    def findKey() {
        if (!params.opusId || !params.scientificName) {
            badRequest "opusId and scientificName are required parameters"
        } else {
            def model = profileService.getProfile(params.opusId as String, params.scientificName as String)

            String key = keybaseService.findKeyForTaxon(model.profile.scientificName, model.profile.classification, model.opus.keybaseProjectId)

            render ([keyId: key] as JSON)
        }
    }

}
