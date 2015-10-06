package au.org.ala.profile.hub

class KeybaseService {
    static final List<String> RANKS = ["kingdom", "phylum", "class", "subclass", "order", "family", "genus", "species", "subspecies"]
    static final String CHAR_ENCODING = "utf-8"

    def grailsApplication
    WebService webService

    String findKeyForTaxon(scientificName, classifications, projectId) {
        if (!projectId) {
            return null
        }

        String key = null

        if (classifications) {
            RANKS.reverse().find {
                String name = findClassification(classifications, it)
                if (name) {
                    key = findKey(name, projectId)
                    if (key) {
                        return true
                    }
                }
            }
        } else {
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

    private findClassification(classifications, rank) {
        classifications.find { it.rank == rank }?.name
    }

    def retrieveAllProjects() {
        webService.get("${grailsApplication.config.keybase.project.lookup}")
    }
}
