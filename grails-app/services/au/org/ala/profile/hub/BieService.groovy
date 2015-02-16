package au.org.ala.profile.hub

import au.org.ala.profile.hub.util.JsonUtil

class BieService {

    def grailsApplication

    JsonUtil jsonUtil = new JsonUtil()

    def getSpeciesProfile(String profileId) {
        def speciesProfile = null

        if (profileId) {
            try {
                speciesProfile = jsonUtil.fromUrl("${grailsApplication.config.bie.base.url}/ws/species/${profileId}")
            } catch (Exception e) {
                log.warn("Unable to load species profile for ${profileId}", e)
            }
        }

        speciesProfile
    }
}
