package au.org.ala.profile.hub

class KeybaseService {
    def grailsApplication
    WebService webService

    def findKeyForTaxon(classification, projectId) {
        if (!classification) {
            return null
        }
        String taxon
        if (classification.genus) {
            taxon = classification.genus;
        } else if (classification.family) {
            taxon = classification.family;
        } else if (classification.order) {
            taxon = classification.order;
        } else if (classification.subClass) {
            taxon = classification.subClass;
        } else if (classification.subClazz) {
            taxon = classification.subClazz;
        } else if (classification.class) {
            taxon = classification.class;
        } else if (classification.clazz) {
            taxon = classification.clazz;
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

    def retrieveAllProjects() {
        webService.get("${grailsApplication.config.keybase.project.lookup}")
    }
}
