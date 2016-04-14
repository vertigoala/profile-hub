package au.org.ala.profile.hub

import au.org.ala.ws.service.WebService

class BieService {

    def grailsApplication
    WebService webService

    def getSpeciesProfile(String guid) {
        webService.get("${grailsApplication.config.bie.base.url}/ws/species/${guid}")
    }
}
