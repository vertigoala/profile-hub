package au.org.ala.profile.hub

class KeybaseService {
    static final List<String> RANKS = ["kingdom", "phylum", "class", "subclass", "order", "family", "genus", "species", "subspecies"]

    def grailsApplication
    WebService webService

    def findKeyForTaxon(classifications, projectId) {
        if (!classifications) {
            return null
        }
        String taxon = null

        RANKS.each {
            String name = findClassification(classifications, it);
            if (name) {
                taxon = name
                return
            }
        }

        String key = null

        def json = webService.get("${grailsApplication.config.keybase.taxon.lookup}${taxon}").resp
        json.Items.each {
            if (it.ProjectsID == projectId) {
                key = it.KeysID
            }
        }

        key
    }

    def findClassification(classifications, rank) {
        classifications.find { it.rank == rank }?.name
    }

    def retrieveAllProjects() {
        webService.get("${grailsApplication.config.keybase.project.lookup}")
    }
}
