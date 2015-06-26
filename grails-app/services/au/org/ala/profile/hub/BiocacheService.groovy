package au.org.ala.profile.hub

import org.springframework.web.multipart.MultipartFile

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

    def uploadImage(String opusId, String profileId, String dataResourceId, MultipartFile file, Map metadata) {
        String imageId = UUID.randomUUID()
        String extension = file.originalFilename.substring(file.originalFilename.lastIndexOf("."))
        file.transferTo(new File("${grailsApplication.config.temp.file.location}/${imageId}${extension}"))

        metadata.multimedia[0].identifier = "${grailsApplication.config.grails.serverURL}/opus/${opusId}/profile/${profileId}/file/${imageId}${extension}"

        webService.doPost("${grailsApplication.config.image.upload.url}${dataResourceId}?apiKey=${grailsApplication.config.image.upload.apiKey}", metadata)
    }

    def lookupSpecimen(String specimenId) {
        webService.get("${grailsApplication.config.biocache.base.url}/ws/occurrences/${specimenId}")
    }
}
