package au.org.ala.profile.hub

/**
 * Service for interacting with the Biodiversity Library
 *
 * http://www.biodiversitylibrary.org/api2/docs/docs.html
 */
class BiodiversityLibraryService {

    // TODO use metaprogramming to create a dynamic method of the form lookupXYZMetadataAsJSON|Text(id) e.g. lookupPageMetadataAsJSON(id) or lookupTitleMetadataAsText(id)

    def grailsApplication
    WebService webService

    def lookupItem(Integer itemId) {
        lookup("Item", itemId, true)
    }

    def lookupTitle(Integer titleId) {
        lookup("Title", titleId, true, "&items=t")
    }

    def lookupPage(Integer pageId) {
        lookup("Page", pageId, true)
    }

    private lookup = { String noun, Integer id, boolean json, String suffix = "" ->
        def apiKey = grailsApplication.config.biodiv.library.api.key

        String url = "${grailsApplication.config.biodiv.library.httpquery.url.prefix}op=Get${noun}Metadata&format=json&${noun.toLowerCase()}id=${id}&apikey=${apiKey}${suffix}"

        try {
            if (json) {
                webService.get(url).resp
            } else {
                new URL(url).text
            }
        }
        catch (Exception e) {
            log.warn("Failed to retrieve data from " + url, e)
        }
    }
}
