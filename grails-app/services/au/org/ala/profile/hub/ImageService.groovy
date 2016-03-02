package au.org.ala.profile.hub

import au.org.ala.images.tiling.ImageTiler
import au.org.ala.images.tiling.ImageTilerConfig
import org.springframework.web.multipart.MultipartFile

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import static org.apache.http.HttpStatus.SC_OK

class ImageService {
    ProfileService profileService
    BiocacheService biocacheService
    WebService webService
    def grailsApplication

    Map getImageDetails(String imageId, String contextPath, boolean includeFile = false) {
        Map imageDetails = [:]

        Map response = profileService.getImageMetadata(imageId)

        if (response.statusCode == SC_OK) {
            Map imageProperties = response.resp as Map
            String dir = imageProperties.type as ImageType == ImageType.PRIVATE ? "${grailsApplication.config.image.private.dir}" : "${grailsApplication.config.image.staging.dir}"
            String extension = getExtension(imageProperties.originalFileName)

            File file = getLocalImageFile(dir, imageProperties.profileId, imageId, extension)

            BufferedImage image = ImageIO.read(file)

            // additional metadata about the physical image, required by the image client plugin
            imageDetails = [
                    success       : true,
                    imageUrl      : "${contextPath}/opus/${imageProperties.opusId}/profile/${imageProperties.profileId}/image/${imageId}${extension}?type=${imageProperties.type}",
                    width         : image.getWidth(),
                    height        : image.getHeight(),
                    tileZoomLevels: new File("${dir}/${imageProperties.profileId}/${imageId}_tiles/").listFiles().size(),
                    tileUrlPattern: "${contextPath}/profile/${imageProperties.profileId}/image/${imageId}/tile/{z}/{x}/{y}?type=${imageProperties.type}"
            ]

            imageDetails.putAll(imageProperties)

            if (includeFile) {
                imageDetails.bufferedImage = image
                imageDetails.file = file
            }
        }

        imageDetails
    }

    File getTile(String profileId, String imageId, String type, int zoom, int x, int y) {
        String baseDir = type as ImageType == ImageType.PRIVATE ? "${grailsApplication.config.image.private.dir}" : "${grailsApplication.config.image.staging.dir}"

        new File("${baseDir}/${profileId}/${imageId}_tiles/${zoom}/${x}/${y}.png")
    }

    def uploadImage(String opusId, String profileId, String dataResourceId, Map metadata, MultipartFile file) {
        def profile = profileService.getProfile(opusId, profileId, true)

        metadata.scientificName = profile.profile.scientificName
        metadata.contentType = file.contentType

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

        File imageFile = new File(localDir, "${metadata.imageId}${extension}")
        file.transferTo(imageFile)

        // create tiles
        File tileDir = new File(localDir, "${metadata.imageId}_tiles")
        tileDir.mkdir()

        ImageTiler tiler = new ImageTiler(new ImageTilerConfig())
        tiler.tileImage(imageFile, tileDir)
    }

    private static File getLocalImageFile(String directory, String profileId, String imageId, String extension) {
        new File("${directory}/${profileId}/${imageId}${extension}")
    }

    def deleteStagedImage(String opusId, String profileId, String imageId) {
        boolean deleted = false;

        def profile = profileService.getProfile(opusId, profileId, true)

        if (profile && profile.profile.stagedImages) {
            deleted = deleteLocalImage(profile.profile.stagedImages, imageId, "${grailsApplication.config.image.staging.dir}/${profile.profile.uuid}/")

            if (deleted) {
                profileService.recordStagedImage(opusId, profileId, [imageId: imageId, action: "delete"])
            } else {
                log.warn("Failed to delete staged image ${imageId}")
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
                profileService.recordPrivateImage(opusId, profileId, [imageId: imageId, action: "delete"])
            } else {
                log.warn("Failed to delete private image ${imageId}")
            }
        }

        deleted
    }

    def retrieveImages(String opusId, String profileId, boolean latest, String imageSources, String searchIdentifier, boolean useInternalPaths = false, boolean readonlyView = true) {
        Map response = [:]

        def model = profileService.getProfile(opusId, profileId, latest)
        def profile = model.profile
        def opus = model.opus

        def publishedImages = biocacheService.retrieveImages(searchIdentifier, imageSources)

        List images = []

        if (publishedImages && publishedImages.statusCode == SC_OK) {
            List biocacheImages = publishedImages.resp.occurrences.findResults { biocacheImage ->
                boolean excluded = isExcluded(opus.approvedImageOption, profile.imageDisplayOptions ?: null, biocacheImage.image)

                Map image = [
                        imageId         : biocacheImage.image,
                        occurrenceId    : biocacheImage.uuid,
                        largeImageUrl   : biocacheImage.largeImageUrl,
                        thumbnailUrl    : biocacheImage.thumbnailUrl,
                        dataResourceName: biocacheImage.dataResourceName,
                        excluded        : excluded,
                        displayOption   : excluded ? ImageOption.EXCLUDE.name() : ImageOption.INCLUDE.name(),
                        primary         : biocacheImage.image == profile.primaryImage,
                        metadata        : biocacheImage.imageMetadata && !biocacheImage.imageMetadata.isEmpty() ? biocacheImage.imageMetadata[0] : [:],
                        type            : ImageType.OPEN
                ]

                // only return images that have not been included, unless we are in the edit view, in which case we
                // need to show all available images in order for the editor to decide which to include/exclude
                if (!excluded || !readonlyView) {
                    image
                }
            }

            images.addAll(biocacheImages)
        } else {
            log.error("A HTTP status of ${publishedImages.statusCode} was returned from the biocache image lookup")
        }

        if (profile.privateMode && profile.stagedImages) {
            images.addAll(convertLocalImages(profile.stagedImages, opus, profile, ImageType.STAGED, useInternalPaths, readonlyView))
        }

        // The collection may now, or may have been at some point, private, so look for any private images that may exist.
        // When a collection is changed from private to public, existing private images are NOT published automatically.
        images.addAll(convertLocalImages(profile.privateImages ?: [], opus, profile, ImageType.PRIVATE, useInternalPaths, readonlyView))

        response.statusCode = SC_OK
        response.resp = images

        response
    }


    private static boolean deleteLocalImage(List images, String imageId, String directory) {
        def image = images.find { it.imageId == imageId }
        String extension = getExtension(image.originalFileName)
        File localDir = new File(directory)
        File file = new File(localDir, "${imageId}${extension}")
        deleteFileAndDirIfEmpty(imageId, file, localDir)
    }

    private static deleteFileAndDirIfEmpty(String imageId, File file, File localDir) {
        boolean deleted = file.delete()

        File tileDir = new File(localDir, "${imageId}_tiles")
        if (tileDir.exists()) {
            deleted &= tileDir.deleteDir()
        }

        if (localDir.listFiles()?.length == 0) {
            localDir.delete()
        }

        deleted
    }

    private
    static convertLocalImages(List images, Map opus, Map profile, ImageType type, boolean useInternalPaths = false, boolean readonlyView = true) {
        String imageUrlPrefix = useInternalPaths ? "file:///data/profile-hub/private-images/${profile.uuid}" : "/opus/${opus.uuid}/profile/${profile.uuid}/image"

        images?.findResults {
            boolean excluded = isExcluded(opus.approvedImageOption, profile.imageDisplayOptions ?: null, it.imageId)

            // only return images that have not been included, unless we are in the edit view, in which case we
            // need to show all available images in order for the editor to decide which to include/exclude
            Map image = null
            if (!excluded || !readonlyView) {
                String extension = getExtension(it.originalFileName)

                image = [
                        imageId         : it.imageId,
                        thumbnailUrl    : "${imageUrlPrefix}/${it.imageId}${extension}?type=${type}",
                        largeImageUrl   : "${imageUrlPrefix}/${it.imageId}${extension}?type=${type}",
                        dataResourceName: opus.title,
                        metadata        : it,
                        excluded        : excluded,
                        displayOption   : excluded ? ImageOption.EXCLUDE.name() : ImageOption.INCLUDE.name(),
                        primary         : it.imageId == profile.primaryImage,
                        type            : type
                ]
            }

            image
        }
    }

    private static boolean isExcluded(String defaultOptionStr, List<Map> imageDisplayOptions, String imageId) {
        boolean excluded

        ImageOption defaultOption = ImageOption.byName(defaultOptionStr, ImageOption.INCLUDE)
        if (defaultOption == ImageOption.EXCLUDE) {
            excluded = imageDisplayOptions?.find { it.imageId == imageId && ImageOption.byName(it.displayOption) == ImageOption.INCLUDE } == null
        } else {
            excluded = imageDisplayOptions?.find { it.imageId == imageId && ImageOption.byName(it.displayOption) == ImageOption.EXCLUDE } != null
        }

        excluded
    }

    def publishPrivateImage(String opusId, String profileId, String imageId) {
        def model = profileService.getProfile(opusId, profileId, true)

        if (model.profile.privateImages) {
            def image = model.profile.privateImages.find { it.imageId == imageId }

            publishImages(model.opus, model.profile, [(imageId): image], false)
        }
    }

    def publishStagedImages(String opusId, String profileId) {
        def model = profileService.getProfile(opusId, profileId, true)

        if (model.profile.stagedImages) {
            Map images = model.profile.stagedImages?.collectEntries {
                [(it.imageId): it]
            }

            publishImages(model.opus, model.profile, images, true)
        }
    }

    private def publishImages(Map opus, Map profile, Map images, boolean staged) {
        Map profileUpdates = [:]

        String localDirPath = staged ? grailsApplication.config.image.staging.dir : grailsApplication.config.image.private.dir

        File localDir = new File("${localDirPath}/${profile.uuid}")
        for (def it : localDir.listFiles()) {
            String imageId = imageIdFromFile(it)
            Map localImage = images[imageId]

            if (!localImage) {
                log.error("Skipping publishing $it with id $imageId because it is missing image metadata.")
                // Clean up staged / private file
                deleteFileAndDirIfEmpty(imageId, it, localDir)
                continue;
            }

            List<Map> multimedia = localImage ? [
                    [
                            creator         : localImage.creator ?: "",
                            rights          : localImage.rights ?: "",
                            rightsHolder    : localImage.rightsHolder ?: "",
                            license         : localImage.licence ?: "",
                            title           : localImage.title ?: "",
                            description     : localImage.description ?: "",
                            dateCreated     : localImage.dateCreated ?: "",
                            originalFilename: localImage.originalFilename,
                            contentType     : localImage.contentType
                    ]
            ] : []
            Map metadata = [multimedia: multimedia, scientificName: profile.scientificName]

            def uploadResponse = biocacheService.uploadImage(opus.uuid, profile.uuid, opus.dataResourceUid, it, metadata)

            if (uploadResponse?.resp) {
                // check if the local image was set as the primary, and swap the local id for the new permanent id
                if (profile.primaryImage == imageId) {
                    profileUpdates.primaryImage = uploadResponse.resp.images[0]
                }

                // check for any display options that were set for the local image, and swap the local id for the new permanent id
                Map imageDisplayOption = profile.imageDisplayOptions?.find { it.imageId == imageId }
                if (imageDisplayOption) {
                    imageDisplayOption?.imageId = uploadResponse.resp.images[0]
                }
            }

            if (staged) {
                deleteStagedImage(opus.uuid, profile.uuid, imageId)
            } else {
                deletePrivateImage(opus.uuid, profile.uuid, imageId)
            }
        }

        if (profileUpdates) {
            profileService.updateProfile(opus.uuid, profile.uuid, profileUpdates, true)
        }
    }

    private static String imageIdFromFile(File file) {
        file.name.substring(0, file.name.indexOf("."))
    }

    private static String getExtension(String fileName) {
        fileName.substring(fileName.lastIndexOf("."))
    }
}
