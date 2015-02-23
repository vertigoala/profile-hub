package au.org.ala.profile.hub

import javax.annotation.PostConstruct

class BiocacheService {

    WebService webService
    def grailsApplication

    String biocacheImageSearchUrl

    @PostConstruct
    def init() {
        biocacheImageSearchUrl = "${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.search.path}"
    }

    def retrieveImages(String searchIdentifier, String imageSources) {
        log.debug("Fetching images for ${searchIdentifier} using sources ${imageSources}")

        String imagesQuery = searchIdentifier + " AND (data_resource_uid:" + imageSources.split(",").join(" OR data_resource_uid:") + ")"
        imagesQuery = imagesQuery.encodeAsURL()

        log.debug("Image query = ${imagesQuery}")

        webService.get("${biocacheImageSearchUrl}?q=${imagesQuery}&fq=multimedia:Image&format=json")
    }
}
