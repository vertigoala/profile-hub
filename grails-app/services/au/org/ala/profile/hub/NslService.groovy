package au.org.ala.profile.hub

class NslService {
    WebService webService
    def grailsApplication

    def listConcepts(String nslNameIdentifier) {
        if (nslNameIdentifier && nslNameIdentifier != "null") {
            webService.get("${grailsApplication.config.nsl.service.url.prefix}${nslNameIdentifier}${grailsApplication.config.nsl.service.apni.concept.suffix}")
        }
    }

    def getConcept(String nslNameIdentifier, String nslNomenclatureIdentifier) {
        def concepts = listConcepts(nslNameIdentifier)?.resp

        concepts?.references?.find { it?._links?.permalink?.link?.endsWith(nslNomenclatureIdentifier) }
    }
}
