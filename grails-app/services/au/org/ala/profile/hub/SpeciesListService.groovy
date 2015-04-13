package au.org.ala.profile.hub


class SpeciesListService {

    def grailsApplication
    WebService webService

    def getListsForGuid(String guid) {
        webService.get("${grailsApplication.config.lists.base.url}/ws/species/${guid}")
    }

    def getAllLists() {
        webService.get("${grailsApplication.config.lists.base.url}/ws/speciesList?max=1000")
    }
}
