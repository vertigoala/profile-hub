package au.org.ala.profile.hub

import org.springframework.web.multipart.MultipartFile

import javax.annotation.PostConstruct

class BiocacheService {

    WebService webService
    def grailsApplication

    def retrieveImages(String searchIdentifier, String imageSources) {
        log.debug("Fetching images for ${searchIdentifier} using sources ${imageSources}")
        String biocacheImageSearchUrl = "${grailsApplication.config.image.search.url}${grailsApplication.config.biocache.occurrence.search.path}"

        String imagesQuery = searchIdentifier + " AND (data_resource_uid:" + imageSources.split(",").join(" OR data_resource_uid:") + ")"
        imagesQuery = imagesQuery.encodeAsURL()

        log.debug("Image query = ${imagesQuery}")

        webService.get("${biocacheImageSearchUrl}?q=${imagesQuery}&fq=multimedia:Image&format=json")
    }

    def uploadImage(String opusId, String profileId, String dataResourceId, file, Map metadata) {
        String imageId = UUID.randomUUID()

        String filename
        File tempDir = new File("${grailsApplication.config.temp.file.location}")
        if (file instanceof MultipartFile) {
            String extension = file.originalFilename.substring(file.originalFilename.lastIndexOf("."))
            filename = "${imageId}${extension}"
            file.transferTo(new File(tempDir, "/${filename}"))
        } else if (file instanceof File) {
            filename = file.getName().substring(0, file.getName().indexOf(0))
            file.renameTo(new File(tempDir, file.getName()));
        }

        metadata.multimedia[0].identifier = "${grailsApplication.config.grails.serverURL}/opus/${opusId}/profile/${profileId}/file/${filename}"

        log.debug("Uploading image ${metadata.multimedia[0].identifier} to ${grailsApplication.config.image.upload.url}${dataResourceId} with metadata ${metadata}")

        if (dataResourceId == "dr382") {
            dataResourceId = "dr4"
        } else if (dataResourceId == "dr2172") {
            dataResourceId = "dr5"
        }


        webService.doPost("${grailsApplication.config.image.upload.url}${dataResourceId}?apiKey=${grailsApplication.config.image.upload.apiKey}", metadata)
    }

    def lookupSpecimen(String specimenId) {
        webService.get("${grailsApplication.config.biocache.base.url}/ws/occurrences/${specimenId}")
    }
}
