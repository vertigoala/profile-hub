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

        metadata.multimedia[0].identifier = "${grailsApplication.config.grails.serverURL}/opus/${enc(opusId)}/profile/${enc(profileId)}/file/${enc(filename)}"

        // make sure the spelling of licenSe is US to match the Darwin Core standard
        if (metadata.multimedia[0].containsKey("licence")) {
            metadata.multimedia[0].license = metadata.licence
            metadata.multimedia[0].remove("licence")
        }

        log.debug("Uploading image ${metadata.multimedia[0].identifier} to ${grailsApplication.config.image.upload.url}${dataResourceId} with metadata ${metadata}")

        // TODO: REMOVE THIS - IT IS FOR TESTING ONLY!!!!
        String hostname = InetAddress.getLocalHost().hostName
        if (hostname == "nci-profiles" || hostname == "nci-profiles-dev" || hostname == "maccy-bm") {
            Map drIdMapping = [
                    dr382: "dr4",
                    dr2172: "dr5",
                    dr2341: "dr7",
                    dr2484: "dr12",
                    dr2485: "dr13",
                    dr2486: "dr15",
                    dr2482: "dr11",
                    dr2487: "dr14",
                    dr2483: "dr16",
                    dr2488: "dr17"
            ]
            log.debug "Mapping prod drId ${dataResourceId} to collectory-dev drId ${drIdMapping[dataResourceId]}"
            dataResourceId = drIdMapping[dataResourceId]
        }

        webService.doPost("${grailsApplication.config.image.upload.url}${dataResourceId}?apiKey=${grailsApplication.config.image.upload.apiKey}", metadata)
    }

    private static enc(String str) {
        URLEncoder.encode(str, "utf-8")
    }

    def lookupSpecimen(String specimenId) {
        webService.get("${grailsApplication.config.biocache.base.url}/ws/occurrences/${specimenId}")
    }
}
