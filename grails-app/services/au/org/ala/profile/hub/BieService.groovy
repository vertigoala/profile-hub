package au.org.ala.profile.hub

class BieService {

    def grailsApplication
    WebService webService

    def getSpeciesProfile(String guid) {
        webService.get("${grailsApplication.config.bie.base.url}/ws/species/${guid}")
    }
}
