package au.org.ala.profile.hub

class NslService {
    WebService webService
    def grailsApplication

    def listConcepts(String nslNameIdentifier) {
        if (nslNameIdentifier) {
            webService.get("${grailsApplication.config.nsl.service.url.prefix}${nslNameIdentifier}${grailsApplication.config.nsl.service.apni.concept.suffix}")
        }
    }

    def getConcept(String nslNameIdentifier, String nslNomenclatureIdentifier) {
        def concepts = listConcepts(nslNameIdentifier).resp.data

        concepts.references.find { it._links.permalink.id.endsWith(nslNomenclatureIdentifier) }
    }
}
