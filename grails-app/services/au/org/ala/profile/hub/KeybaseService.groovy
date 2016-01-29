package au.org.ala.profile.hub

class KeybaseService {
    static final String CHAR_ENCODING = "utf-8"

    def grailsApplication
    WebService webService

    String findKeyForTaxon(scientificName, projectId) {
        String key = null

        if (projectId) {
            key = findKey(scientificName, projectId)
        }

        key
    }

    private findKey(String name, String projectId) {
        name = URLEncoder.encode(name, CHAR_ENCODING)

        String key = null

        def json = webService.get("${grailsApplication.config.keybase.taxon.lookup}${name}").resp
        json?.Items?.each {
            if (it.ProjectsID == projectId) {
                key = it.KeysID
            }
        }

        key
    }

    def retrieveAllProjects() {
        webService.get("${grailsApplication.config.keybase.project.lookup}")
    }
}
