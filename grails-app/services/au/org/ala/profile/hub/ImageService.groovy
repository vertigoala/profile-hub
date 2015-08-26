package au.org.ala.profile.hub

import org.springframework.web.multipart.MultipartFile

import static org.apache.http.HttpStatus.SC_OK

class ImageService {
    ProfileService profileService
    BiocacheService biocacheService
    def grailsApplication

    def uploadImage(String opusId, String profileId, String dataResourceId, Map metadata, MultipartFile file) {
        def profile = profileService.getProfile(opusId, profileId, true)

        metadata.scientificName = profile.profile.scientificName

        def response

        if (profile.profile.privateMode) {
            // if the profile is in draft (private) mode, then stage the image: it will be uploaded to the biocache when the profile's draft is released
            File stagingDir = new File("${grailsApplication.config.image.staging.dir}/${profile.profile.uuid}")
            if (!stagingDir.exists()) {
                log.debug("Creating directory for profile")
                stagingDir.mkdir()
            }

            String extension = getExtension(file.originalFilename)
            metadata.imageId = UUID.randomUUID().toString()
            metadata.action = "add"

            file.transferTo(new File(stagingDir, "${metadata.imageId}${extension}"))

            response = profileService.recordStagedImage(opusId, profileId, metadata)
        } else {
            // if the profile is not in draft mode, upload the image to the biocache immediately
            response = biocacheService.uploadImage(opusId, profile.profile.uuid, dataResourceId, file, metadata)
        }

        response
    }

    def deleteStagedImage(String opusId, String profileId, String imageId) {
        boolean deleted = false;

        def profile = profileService.getProfile(opusId, profileId, true)

        if (profile && profile.profile.stagedImages) {
            def image = profile.profile.stagedImages.find { it.imageId == imageId }
            String extension = getExtension(image.originalFileName)
            File stagingDir = new File("${grailsApplication.config.image.staging.dir}/${profile.profile.uuid}/")
            File file = new File(stagingDir, "${imageId}${extension}")
            deleted = file.delete();

            if (deleted) {
                profileService.recordStagedImage(opusId, profileId, [imageId: imageId, action: "delete"])
            } else {
                log.warn("Failed to delete staged image file ${file.getAbsolutePath()}")
            }

            if (stagingDir.listFiles().length == 0) {
                stagingDir.delete()
            }
        }

        deleted
    }

    def retrieveImages(String opusId, String profileId, boolean latest, String imageSources, String searchIdentifier) {
        Map response = [:]

        def model = profileService.getProfile(opusId, profileId, latest)
        def profile = model.profile
        def opus = model.opus

        def publishedImages = biocacheService.retrieveImages(searchIdentifier, imageSources)

        List images = []

        if (publishedImages.statusCode != SC_OK) {
            log.debug "Response status ${publishedImages.resp} returned from operation"
            response = publishedImages
        } else {
            List biocacheImages = publishedImages.resp.occurrences.collect {
                boolean excluded = false
                if (profile.excludedImages && profile.excludedImages.contains(it.image)) {
                    excluded = true
                }

                [
                        imageId         : it.image,
                        occurrenceId    : it.uuid,
                        largeImageUrl   : it.largeImageUrl,
                        thumbnailUrl    : it.thumbnailUrl,
                        dataResourceName: it.dataResourceName,
                        excluded        : excluded,
                        primary         : it.image == profile.primaryImage,
                        metadata        : it.imageMetadata && !it.imageMetadata.isEmpty() ? it.imageMetadata[0] : [:],
                        staged          : false
                ]
            }

            images.addAll(biocacheImages)

            if (profile.privateMode && profile.stagedImages) {
                List stagedImages = profile.stagedImages.collect {
                    String extension = getExtension(it.originalFileName)
                    boolean excluded = false
                    if (profile.excludedImages && profile.excludedImages.contains(it.imageId)) {
                        excluded = true
                    }

                    [
                            imageId         : it.imageId,
                            thumbnailUrl    : "/opus/${opusId}/profile/${profile.uuid}/stagedImage/${it.imageId}${extension}",
                            largeImageUrl   : "/opus/${opusId}/profile/${profile.uuid}/stagedImage/${it.imageId}${extension}",
                            dataResourceName: opus.title,
                            metadata        : it,
                            excluded        : excluded,
                            primary         : it.imageId == profile.primaryImage,
                            staged          : true
                    ]
                }

                images.addAll(stagedImages)
            }

            response.statusCode = SC_OK
            response.resp = images
        }

        response
    }

    def publishImages(String opusId, String profileId) {
        def profile = profileService.getProfile(opusId, profileId, true)

        Map stagedImages = [:]
        if (profile.profile.stagedImages) {
            stagedImages = profile.profile.stagedImages?.collectEntries {
                [(it.imageId): it]
            }
        }

        Map profileUpdates = [:]

        File stagingDir = new File("${grailsApplication.config.image.staging.dir}/${profileId}/")
        stagingDir.listFiles().each {
            String imageId = it.name.substring(0, it.name.indexOf("."))
            Map stagedImage = stagedImages[imageId]

            List<Map> multimedia = [
                    [
                            creator         : stagedImage.creator ?: "",
                            rights          : stagedImage.rights ?: "",
                            rightsHolder    : stagedImage.rightsHolder ?: "",
                            license         : stagedImage.licence ?: "",
                            title           : stagedImage.title ?: "",
                            description     : stagedImage.description ?: "",
                            dateCreated     : stagedImage.dateCreated ?: "",
                            originalFilename: stagedImage.originalFilename
                    ]
            ]
            Map metadata = [multimedia: multimedia, scientificName: profile.profile.scientificName]

            def uploadResponse = biocacheService.uploadImage(opusId, profile.profile.uuid, profile.opus.dataResourceUid, it, metadata)

            // check if the staged image was set as the primary or an excluded image, and swap the staged id for the new permanent id
            if (profile.profile.primaryImage == imageId) {
                profileUpdates.primaryImage = uploadResponse.resp.images[0]
            }
            if (profile.profile.excludedImages?.contains(imageId)) {
                profile.profile.excludedImages.remove(imageId)
                profile.profile.excludedImages << uploadResponse.resp.images[0]
                profileUpdates.excludedImages = profile.profile.excludedImages
            }

            deleteStagedImage(opusId, profileId, imageId)
        }

        if (profileUpdates) {
            profileService.updateProfile(opusId, profileId, profileUpdates, true)
        }
    }

    private static String getExtension(String fileName) {
        fileName.substring(fileName.lastIndexOf("."))
    }
}
