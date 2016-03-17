package au.org.ala.profile.hub

import au.org.ala.images.thumb.ImageThumbnailer
import au.org.ala.images.thumb.ThumbDefinition
import au.org.ala.images.tiling.ImageTiler
import au.org.ala.images.tiling.ImageTilerConfig
import org.apache.commons.io.FileUtils
import org.springframework.web.multipart.MultipartFile

import javax.imageio.ImageIO
import java.awt.*
import java.awt.image.BufferedImage
import java.util.List

import static org.apache.http.HttpStatus.SC_OK

/**
 * Provides support to "local" private and staged images, publishing of uploaded images to BioCache and retrieval of
 * images from BioCache.
 *
 * The on disk directory structure for private images is:
 * /data/profile-hub/private_images/collectionId/profileUUID/imageID/imageID.jpg.
 *
 * The on disk directory structure for staged images is:
 * /data/profile-hub/staged_images/collectionId/profileUUID/imageID/imageID.jpg.
 *
 * The deprecated methods in this class are retained to support users using a previous version that has a different
 * file storage structure for private and staged images
 *
 */

class ImageService {
    ProfileService profileService
    BiocacheService biocacheService
    WebService webService
    def grailsApplication

    static final String separator = File.separator
    static final Integer THUMBNAIL_MAX_SIZE = 300

    /**
     * When you click on an image in the UI, this method retrieves the image
     * @param imageId
     * @param contextPath
     * @param includeFile
     * @return details required by ALA image plugin
     */
    Map getImageDetails(String imageId, String contextPath, boolean includeFile = false) {
        Map imageDetails = [:]

        Map response = profileService.getImageMetadata(imageId)

        if (response.statusCode == SC_OK) {
            Map imageProperties = response.resp as Map
            String dir = imageProperties.type as ImageType == ImageType.PRIVATE ? "${grailsApplication.config.image.private.dir}" : "${grailsApplication.config.image.staging.dir}"
            String extension = getExtension(imageProperties.originalFileName)
            String imageUrl
            String thumbnailUrl
            int tileZoomLevels
            String tileUrlPattern

            File file = getLocalImageFile(dir, imageProperties.opusId, imageProperties.profileId, imageId, extension)

            if (file.exists()) {
                String tileLocation = buildFilePath(dir, imageProperties.opusId, imageProperties.profileId, imageId) + separator + imageId + '_tiles/'
                imageUrl = "${contextPath}/opus/${imageProperties.opusId}/profile/${imageProperties.profileId}/image/${imageId}${extension}?type=${imageProperties.type}"
                thumbnailUrl = "${contextPath}/opus/${imageProperties.opusId}/profile/${imageProperties.profileId}/image/${imageId}${extension}?type=${imageProperties.type}"
                tileZoomLevels = new File(tileLocation)?.listFiles()?.size()
                tileUrlPattern = "${contextPath}/opus/${imageProperties.opusId}/profile/${imageProperties.profileId}/image/${imageId}/tile/{z}/{x}/{y}?type=${imageProperties.type}"

            } else {  //provide support for previously used file directory structure
                file = getLocalImageFile(dir, imageProperties.profileId, imageId, extension)
                imageUrl = "${contextPath}/opus/${imageProperties.opusId}/profile/${imageProperties.profileId}/image/${imageId}${extension}?type=${imageProperties.type}"
                thumbnailUrl = "${contextPath}/opus/${imageProperties.opusId}/profile/${imageProperties.profileId}/image/${imageId}${extension}?type=${imageProperties.type}"
                tileZoomLevels = new File("${dir}/${imageProperties.profileId}/${imageId}_tiles/")?.listFiles()?.size()
                tileUrlPattern = "${contextPath}/profile/${imageProperties.profileId}/image/${imageId}/tile/{z}/{x}/{y}?type=${imageProperties.type}"
            }
            BufferedImage bufferedImage = ImageIO.read(file)
            // additional metadata about the physical image, required by the image client plugin
            imageDetails = [
                    imageId       : imageId,
                    success       : true,
                    imageUrl      : imageUrl,
                    thumbnailUrl  : thumbnailUrl,
                    width         : bufferedImage.getWidth(),
                    height        : bufferedImage.getHeight(),
                    tileZoomLevels: tileZoomLevels,
                    tileUrlPattern: tileUrlPattern
            ]
            imageDetails.putAll(imageProperties)

            if (includeFile) {
                imageDetails.bufferedImage = bufferedImage
                imageDetails.file = file
            }
        }

        imageDetails
    }

    File getTile(String collectionId, String profileId, String imageId, String type, int zoom, int x, int y) {
        String baseDir = type as ImageType == ImageType.PRIVATE ? "${grailsApplication.config.image.private.dir}" : "${grailsApplication.config.image.staging.dir}"
        String fileLocation = buildFilePath(baseDir, collectionId, profileId, imageId)
        new File(fileLocation + "/${imageId}_tiles/${zoom}/${x}/${y}.png")
    }

    def uploadImage(String contextPath, String opusId, String profileId, String dataResourceId, Map metadata, MultipartFile file) {
        def profile = profileService.getProfile(opusId, profileId, true)

        metadata.scientificName = profile.profile.scientificName
        metadata.contentType = file.contentType

        def response

        if (profile.profile.privateMode && !profile.opus.keepImagesPrivate) {
            // if the collection is public and the profile is in draft mode, then stage the image: it will be
            // uploaded to the biocache when the profile's draft is released
            try {
                storeLocalImage(profile, metadata, file, "${grailsApplication.config.image.staging.dir}")
            } catch (IOException exception) {
                log.error("Error saving local staged image ", exception)
            }
            response = profileService.recordStagedImage(opusId, profileId, metadata)
        } else if (profile.opus.keepImagesPrivate) {
            // if the admin has elected not to publish images, then store the image in the local image dir
            try {
                storeLocalImage(profile, metadata, file, "${grailsApplication.config.image.private.dir}")
            } catch (IOException exception) {
                log.error("Error saving local private image ", exception)
            }
            response = profileService.recordPrivateImage(opusId, profileId, metadata)
        } else {
            // if the profile is not in draft mode, upload the image to the biocache immediately
            response = biocacheService.uploadImage(opusId, profile.profile.uuid, dataResourceId, file, metadata)
        }

        if (response?.statusCode == SC_OK) {
            response.resp = getImageDetails(metadata.imageId, contextPath)
        }

        response
    }

    private static storeLocalImage(Map profile, Map metadata, MultipartFile file, String directory) throws IOException {
        String extension = getExtension(file.originalFilename)
        metadata.imageId = UUID.randomUUID().toString()
        metadata.action = "add"
        String fileLocation = buildFilePath(directory, profile.opus.uuid, profile.profile.uuid, metadata.imageId)
        File localDir = new File(fileLocation)
        localDir.mkdirs()

        File imageFile = new File(localDir, "${metadata.imageId}${extension}")
        file.transferTo(imageFile)

        // create tiles
        File tileDir = new File(localDir, "${metadata.imageId}_tiles")
        tileDir.mkdirs()
        ImageTiler tiler = new ImageTiler(new ImageTilerConfig())
        tiler.tileImage(imageFile, tileDir)

        List<ThumbDefinition> thumbDefinitionList = new ArrayList<ThumbDefinition>(1)
        thumbDefinitionList.add(new ThumbDefinition(THUMBNAIL_MAX_SIZE, false, Color.white, "${metadata.imageId}_thumbnail${extension}"))
        makeThumbNails(localDir, "${metadata.imageId}_thumbnails", imageFile, thumbDefinitionList)

    }

    private
    static makeThumbNails(File parentDirectory, String thumbDir, File imageFile, List<ThumbDefinition> definitionList) throws IOException {
        File thumbnailDirectory = new File(parentDirectory, thumbDir)
        if (!thumbnailDirectory.exists()) {
            thumbnailDirectory.mkdir()
        }
        ImageThumbnailer imageThumbnailer = new ImageThumbnailer()
        byte[] imageBytes = FileUtils.readFileToByteArray(imageFile)
        imageThumbnailer.generateThumbnails(imageBytes, thumbnailDirectory, definitionList)
    }

    private
    static File getLocalImageFile(String directory, String collectionId, String profileId, String imageId, String extension) {
        new File("${directory}/$collectionId/${profileId}/${imageId}/${imageId}${extension}")
    }

    def deleteStagedImage(String opusId, String profileId, String imageId) {
        boolean deleted = false;

        def profile = profileService.getProfile(opusId, profileId, true)

        if (profile && profile.profile.stagedImages) {
            deleted = deleteLocalImage(profile.profile.stagedImages, opusId, profile.profile.uuid, imageId, "${grailsApplication.config.image.staging.dir}")

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

        def profile = profileService.getProfile(opusId, profileId, true) //profileId is its name, not its uuid

        if (profile && profile.profile.privateImages) {
            deleted = deleteLocalImage(profile.profile.privateImages, opusId, profile.profile.uuid, imageId, "${grailsApplication.config.image.private.dir}")
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
            List biocacheImages = publishedImages.resp?.occurrences?.findResults { biocacheImage ->
                boolean excluded = isExcluded(opus.approvedImageOption, profile.imageSettings ?: null, biocacheImage.image)

                Map image = [
                        imageId         : biocacheImage.image,
                        occurrenceId    : biocacheImage.uuid,
                        largeImageUrl   : biocacheImage.largeImageUrl,
                        thumbnailUrl    : biocacheImage.thumbnailUrl,
                        dataResourceName: biocacheImage.dataResourceName,
                        excluded        : excluded,
                        displayOption   : excluded ? ImageOption.EXCLUDE.name() : ImageOption.INCLUDE.name(),
                        caption         : profile.imageSettings.find {
                            it.imageId == biocacheImage.image
                        }?.caption ?: '',
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
            if (biocacheImages && biocacheImages.size() > 0) {
                images.addAll(biocacheImages)
            }
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

    private
    static boolean deleteLocalImage(List images, String collectionId, String profileId, String imageId, String directory) {
        String pathToFile = buildFilePath(directory, collectionId, profileId, imageId)
        File localDir = new File(pathToFile)
        if (localDir.exists()) {
            deleteDirectoryAndContents(localDir)
        } else {
            deleteLocalImage(images, imageId, directory + separator + profileId)
        }
    }

    private static boolean deleteDirectoryAndContents(File directoryToDelete) {
        directoryToDelete.deleteDir()
    }

    //RetrieveImages() calls this method and is itself called by ImageService (this class) and ExportService. The
    //latter needs to know the disk location of the images and will send useInternalPaths with a value of true
    private
    static convertLocalImages(List images, Map opus, Map profile, ImageType type, boolean useInternalPaths = false, boolean readonlyView = true) {
        String imageUrlPrefix = "/opus/${opus.uuid}/profile/${profile.uuid}/image"
        //this is the default, NOT for ExportService

        images?.findResults {
            boolean excluded = isExcluded(opus.approvedImageOption, profile.imageSettings ?: null, it.imageId)

            // only return images that have not been included, unless we are in the edit view, in which case we
            // need to show all available images in order for the editor to decide which to include/exclude
            Map image = null
            String prefix = imageUrlPrefix
            if (!excluded || !readonlyView) {
                String extension = getExtension(it.originalFileName)
                if (useInternalPaths) {
                    //new directory structure include a dir for image, we can have a mix of old and new files
                    prefix = calculatePrefixForFilePath(opus.uuid, profile.uuid, it.imageId)
                }
                image = [
                        imageId         : it.imageId,
                        thumbnailUrl    : "${prefix}/${it.imageId}${extension}?type=${type}",
                        largeImageUrl   : "${prefix}/${it.imageId}${extension}?type=${type}",
                        dataResourceName: opus.title,
                        metadata        : it,
                        excluded        : excluded,
                        displayOption   : excluded ? ImageOption.EXCLUDE.name() : ImageOption.INCLUDE.name(),
                        caption         : profile.imageSettings.find { setting -> setting.imageId == it.imageId }?.caption ?: '',
                        primary         : it.imageId == profile.primaryImage,
                        type            : type
                ]
            }

            image
        }
    }

    static boolean isCurrentFileStructure(String collectionId, String profileId, String imageId) {
        boolean isNewFileStructure = false
        File imageDir = new File("/data/profile-hub/private-images/${collectionId}/${profileId}/${imageId}")
        if (imageDir.exists()) {
            isNewFileStructure = true
        }
        isNewFileStructure
    }
//we are supporting 2 alternatives for backwards compatibility
    static String calculatePrefixForFilePath(String collectionId, String profileId, String imageId) {
        String prefix = "file:///data/profile-hub/private-images/${collectionId}/${profileId}/${imageId}"
        if (!isCurrentFileStructure(collectionId, profileId, imageId)) {
            prefix = "file:///data/profile-hub/private-images/${profileId}"
        }
        prefix
    }

    private static boolean isExcluded(String defaultOptionStr, List<Map> imageSettings, String imageId) {
        boolean excluded

        ImageOption defaultOption = ImageOption.byName(defaultOptionStr, ImageOption.INCLUDE)
        if (defaultOption == ImageOption.EXCLUDE) {
            excluded = imageSettings?.find {
                it.imageId == imageId && ImageOption.byName(it?.displayOption?.toString()) == ImageOption.INCLUDE
            } == null
        } else {
            excluded = imageSettings?.find {
                it.imageId == imageId && ImageOption.byName(it?.displayOption?.toString()) == ImageOption.EXCLUDE
            } != null
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
        List<File> imageDirectories = []
        List<File> imageFiles = []
        String localDirPath = staged ? grailsApplication.config.image.staging.dir : grailsApplication.config.image.private.dir
        //Check in old file directory structure
        File localDir = new File("${localDirPath}/${profile.uuid}")
        if (localDir.exists()) {
            imageFiles = localDir.listFiles().collect()
        }
        //Now check new directory structure
        File profileDir = new File("${localDirPath}/${opus.uuid}/${profile.uuid}/")
        if (profileDir.exists()) {
            imageDirectories = profileDir.listFiles()

            if (imageDirectories.size() > 0) {
                //when we get to the profile directory it will contain 1 directory for each image, go into
                //these directories and take the image files without going into the subdirectories containing
                //tiled images and thumbnails
                imageDirectories.each { file ->
                    file.traverse(maxDepth: 1) {
                        if (it.file) {
                            imageFiles << it
                        }
                    }
                }
            }
        }

        imageFiles.each {
            String imageId = imageIdFromFile(it)
            Map localImage = images[imageId]

            if (!localImage) {
                log.error("Skipping publishing $it with id $imageId because it is missing image metadata.")
                // Clean up staged / private file
                deleteFileAndDirIfEmpty(imageId, it, localDir)
            } else {

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
                    Map imageDisplayOption = profile.imageSettings?.find { it.imageId == imageId }
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
        }

        if (profileUpdates) {
            profileService.updateProfile(opus.uuid, profile.uuid, profileUpdates, true)
        }
    }

    //Changed directory structure, supporting existing files stored in old structure, use updated method - same name
    @Deprecated
    File getTile(String profileId, String imageId, String type, int zoom, int x, int y) {
        String baseDir = type as ImageType == ImageType.PRIVATE ? "${grailsApplication.config.image.private.dir}" : "${grailsApplication.config.image.staging.dir}"
        new File("${baseDir}/${profileId}/${imageId}_tiles/${zoom}/${x}/${y}.png")
    }

    //Changed directory structure, supporting existing files stored in old structure, use updated method - same name
    @Deprecated
    private static File getLocalImageFile(String directory, String profileId, String imageId, String extension) {
        new File("${directory}/${profileId}/${imageId}${extension}")
    }

    //Changed directory structure, supporting existing files stored in old structure, use updated method - same name
    @Deprecated
    private static boolean deleteLocalImage(List images, String imageId, String directory) {
        def image = images.find { it.imageId == imageId }
        String extension = getExtension(image.originalFileName)
        File localDir = new File(directory)
        File file = new File(localDir, "${imageId}${extension}")
        deleteFileAndDirIfEmpty(imageId, file, localDir)
    }

    //Changed directory structure, supporting existing files stored in old structure, use deleteDirectoryAndContents()
    @Deprecated
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


    private static String imageIdFromFile(File file) {
        file.name.substring(0, file.name.indexOf("."))
    }

    private static String getExtension(String fileName) {
        fileName.substring(fileName.lastIndexOf("."))
    }

    private static String buildFilePath(String parentDir, String collectionId, String profileId, String imageId) {
        parentDir + separator + collectionId + separator + profileId + separator + imageId
    }

}
