package au.org.ala.profile.hub

import org.springframework.web.multipart.MultipartFile

class BiocacheService {

    WebService webService
    def grailsApplication

    def retrieveImages(String searchIdentifier, String imageSources) {
        log.debug("Fetching images for ${searchIdentifier} using sources ${imageSources}")
        String biocacheImageSearchUrl = "${grailsApplication.config.image.search.url}${grailsApplication.config.biocache.occurrence.search.path}"

        String imagesQuery = searchIdentifier + " AND (data_resource_uid:" + imageSources.split(",").join(" OR data_resource_uid:") + ")"
        imagesQuery = imagesQuery.encodeAsURL()

        log.debug("Image query = ${imagesQuery}")

        webService.get("${biocacheImageSearchUrl}?q=${imagesQuery}&fq=multimedia:Image&format=json&im=true")
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
            filename = file.getName()
            file.renameTo(new File(tempDir, file.getName()));
        }

        metadata.multimedia[0].identifier = "${grailsApplication.config.grails.serverURL}/opus/${opusId}/profile/${profileId}/file/${filename}"

        // make sure the spelling of licenSe is US to match the Darwin Core standard
        if (metadata.multimedia[0].containsKey("licence")) {
            metadata.multimedia[0].license = metadata.licence
            metadata.multimedia[0].remove("licence")
        }

        log.debug("Uploading image ${metadata.multimedia[0].identifier} to ${grailsApplication.config.image.upload.url}${dataResourceId} with metadata ${metadata}")

        // TODO: REMOVE THIS - IT IS FOR TESTING ONLY!!!!
        String hostname = InetAddress.getLocalHost().hostName
        println "Hostname = ${hostname}"
        if (hostname == "nci-profiles" || hostname == "nci-profiles-dev" || hostname == "maccy-bm") {
            if (dataResourceId == "dr382") { // foa
                dataResourceId = "dr4"
            } else if (dataResourceId == "dr2172") { // nsw
                dataResourceId = "dr5"
            } else if (dataResourceId == "dr2341") { // olkola
                dataResourceId = "dr7"
            }
        }

        webService.doPost("${grailsApplication.config.image.upload.url}${dataResourceId}?apiKey=${grailsApplication.config.image.upload.apiKey}", metadata)
    }

    def lookupSpecimen(String specimenId) {
        webService.get("${grailsApplication.config.biocache.base.url}/ws/occurrences/${specimenId}")
    }
}
