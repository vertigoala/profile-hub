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

        if (profile.profile.privateMode && !profile.opus.keepImagesPrivate) {
            // if the collection is public and the profile is in draft (private) mode, then stage the image: it will be
            // uploaded to the biocache when the profile's draft is released
            storeLocalImage(profile.profile, metadata, file, "${grailsApplication.config.image.staging.dir}")

            response = profileService.recordStagedImage(opusId, profileId, metadata)
        } else if (profile.opus.keepImagesPrivate) {
            // if the admin has elected not to publish images, then store the image in the local image dir
            storeLocalImage(profile.profile, metadata, file, "${grailsApplication.config.image.private.dir}")

            response = profileService.recordPrivateImage(opusId, profileId, metadata)
        } else {
            // if the profile is not in draft mode, upload the image to the biocache immediately
            response = biocacheService.uploadImage(opusId, profile.profile.uuid, dataResourceId, file, metadata)
        }

        response
    }

    private static storeLocalImage(Map profile, Map metadata, MultipartFile file, String directory) {
        File localDir = new File("${directory}/${profile.uuid}")
        if (!localDir.exists()) {
            localDir.mkdir()
        }

        String extension = getExtension(file.originalFilename)
        metadata.imageId = UUID.randomUUID().toString()
        metadata.action = "add"

        file.transferTo(new File(localDir, "${metadata.imageId}${extension}"))
    }

    private static boolean deleteLocalImage(List images, String imageId, String directory) {
        def image = images.find { it.imageId == imageId }
        String extension = getExtension(image.originalFileName)
        File localDir = new File(directory)
        File file = new File(localDir, "${imageId}${extension}")
        boolean deleted = file.delete();

        if (localDir.listFiles().length == 0) {
            localDir.delete()
        }

        deleted
    }

    def deleteStagedImage(String opusId, String profileId, String imageId) {
        boolean deleted = false;

        def profile = profileService.getProfile(opusId, profileId, true)

        if (profile && profile.profile.stagedImages) {
            deleted = deleteLocalImage(profile.profile.stagedImages, imageId, "${grailsApplication.config.image.staging.dir}/${profile.profile.uuid}/")

            if (deleted) {
                profileService.recordStagedImage(opusId, profileId, [imageId: imageId, action: "delete"])
            } else {
                log.warn("Failed to delete staged image file ${file.getAbsolutePath()}")
            }
        }

        deleted
    }

    def deletePrivateImage(String opusId, String profileId, String imageId) {
        boolean deleted = false;

        def profile = profileService.getProfile(opusId, profileId, true)

        if (profile && profile.profile.privateImages) {
            deleted = deleteLocalImage(profile.profile.privateImages, imageId, "${grailsApplication.config.image.private.dir}/${profile.profile.uuid}/")

            if (deleted) {
                profileService.recordStagedImage(opusId, profileId, [imageId: imageId, action: "delete"])
            } else {
                log.warn("Failed to delete private image file ${file.getAbsolutePath()}")
            }
        }

        deleted
    }

    def retrieveImages(String opusId, String profileId, boolean latest, String imageSources, String searchIdentifier, boolean useInternalPaths = false) {
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
                        type            : ImageType.PUBLIC
                ]
            }

            images.addAll(biocacheImages)

            if (profile.privateMode && profile.stagedImages) {
                images.addAll(convertLocalImages(profile.stagedImages, opus, profile, ImageType.STAGED, useInternalPaths))
            }

            // The collection may now, or may have been at some point, private, so look for any private images that may exist.
            // When a collection is changed from private to public, existing private images are NOT published automatically.
            images.addAll(convertLocalImages(profile.privateImages ?: [], opus, profile, ImageType.PRIVATE, useInternalPaths))

            response.statusCode = SC_OK
            response.resp = images
        }

        response
    }

    private static convertLocalImages(List images, Map opus, Map profile, ImageType type, boolean useInternalPaths = false) {
        String imageUrlPrefix = useInternalPaths ? "file:///data/profile-hub/private-images/${profile.uuid}" : "/opus/${opus.uuid}/profile/${profile.uuid}/image"

        images?.collect {
            String extension = getExtension(it.originalFileName)
            boolean excluded = false
            if (profile.excludedImages && profile.excludedImages.contains(it.imageId)) {
                excluded = true
            }

            [
                    imageId         : it.imageId,
                    thumbnailUrl    : "${imageUrlPrefix}/${it.imageId}${extension}?type=${type}",
                    largeImageUrl   : "${imageUrlPrefix}/${it.imageId}${extension}?type=${type}",
                    dataResourceName: opus.title,
                    metadata        : it,
                    excluded        : excluded,
                    primary         : it.imageId == profile.primaryImage,
                    type            : type
            ]
        }
    }

    def publishStagedImages(String opusId, String profileId) {
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
