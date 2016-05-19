package au.org.ala.profile.hub

import au.org.ala.images.thumb.ImageThumbnailer
import au.org.ala.images.thumb.ThumbDefinition
import au.org.ala.images.tiling.ImageTiler
import au.org.ala.images.tiling.ImageTilerConfig
import au.org.ala.ws.service.WebService
import org.apache.commons.io.FileUtils
import org.springframework.web.multipart.MultipartFile

import javax.imageio.ImageIO
import java.awt.*
import java.awt.image.BufferedImage
import java.util.List

import static au.org.ala.profile.hub.Utils.getExtension
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

    private getMetadataFromAlaImageService(String imageId) {
        webService.get("${grailsApplication.config.uploaded.images.url}/ws/image/${imageId}", false, true)
    }

    /**
     * When you click on an image in the UI, this method retrieves the image
     * @param imageId
     * @param contextPath
     * @param includeFile
     * @return details required by ALA image plugin
     */
    Map getImageDetails(String imageId, String contextPath, boolean localImage = true, boolean includeFile = false) {
        Map imageDetails = [:]

        Map response = localImage ? profileService.getImageMetadata(imageId) : getMetadataFromAlaImageService(imageId)

        if (response.statusCode == SC_OK) {
            Map imageProperties = response.resp as Map

            if (localImage) {
                String dir = imageProperties.type as ImageType == ImageType.PRIVATE ? "${grailsApplication.config.image.private.dir}" : "${grailsApplication.config.image.staging.dir}"
                String extension = getExtension(imageProperties.originalFileName)
                String imageUrl
                String thumbnailUrl
                int tileZoomLevels
                String tileUrlPattern

                File file = getLocalImageFile(dir, imageProperties.opusId, imageProperties.profileId, imageId, extension)

                if (file?.exists()) {
                    String tileLocation = buildFilePath(dir, imageProperties.opusId, imageProperties.profileId, imageId) + separator + imageId + '_tiles/'
                    imageUrl = "${contextPath}/opus/${imageProperties.opusId}/profile/${imageProperties.profileId}/image/${imageId}${extension}?type=${imageProperties.type}"
                    thumbnailUrl = "${contextPath}/opus/${imageProperties.opusId}/profile/${imageProperties.profileId}/image/thumbnail/${imageId}${extension}?type=${imageProperties.type}"
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
                        success       : true,
                        imageUrl      : imageUrl,
                        thumbnailUrl  : thumbnailUrl,
                        width         : bufferedImage.getWidth(),
                        height        : bufferedImage.getHeight(),
                        tileZoomLevels: tileZoomLevels,
                        tileUrlPattern: tileUrlPattern
                ]

                if (includeFile) {
                    imageDetails.bufferedImage = bufferedImage
                    imageDetails.file = file
                }
            } else {
                imageProperties << [thumbnailUrl: imageProperties.imageUrl.replace("/original", "/thumbnail")]
            }

            if (!imageProperties.metadata) {
                // extract the metadata from the image service response and place it in a 'metadata' map so we have the
                // same format as for local images
                Map metadata = [:]
                metadata.rightsHolder = imageProperties.rightsHolder
                metadata.dateTaken = imageProperties.dateTaken
                metadata.creator = imageProperties.creator
                metadata.license = imageProperties.license
                metadata.description = imageProperties.description
                metadata.title = imageProperties.title
                metadata.rights = imageProperties.rights
                imageProperties.metadata = metadata
            }

            imageDetails.putAll(imageProperties)
            imageDetails.imageId = imageId
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

        boolean imageIsStoredLocally
        if (profile.profile.privateMode && !profile.opus.keepImagesPrivate) {
            // if the collection is public and the profile is in draft mode, then stage the image: it will be
            // uploaded to the biocache when the profile's draft is released
            try {
                storeLocalImage(profile, metadata, file, "${grailsApplication.config.image.staging.dir}")
            } catch (IOException exception) {
                log.error("Error saving local staged image ", exception)
            }
            response = profileService.recordStagedImage(opusId, profileId, metadata)
            imageIsStoredLocally = true
        } else if (profile.opus.keepImagesPrivate) {
            // if the admin has elected not to publish images, then store the image in the local image dir
            try {
                storeLocalImage(profile, metadata, file, "${grailsApplication.config.image.private.dir}")
            } catch (IOException exception) {
                log.error("Error saving local private image ", exception)
            }
            response = profileService.recordPrivateImage(opusId, profileId, metadata)
            imageIsStoredLocally = true
        } else {
            // if the profile is not in draft mode, upload the image to the biocache immediately
            response = biocacheService.uploadImage(opusId, profile.profile.uuid, dataResourceId, file, metadata)
            metadata.imageId = response?.resp?.images ? response.resp.images[0] : null
            imageIsStoredLocally = false
        }

        if (response?.statusCode == SC_OK) {
            response.resp = getImageDetails(metadata.imageId, contextPath, imageIsStoredLocally)
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

    /**
     *
     * @param opusId - the collection
     * @param profileId - the taxon
     * @param latest
     * @param searchIdentifier
     * @param useInternalPaths
     * @param readonlyView
     * @param pageSize - equivalent to rows in SOLR i.e how many records to return
     * @param startIndex - equivalent to start in SOLR i.e. the first record to return
     * @return MAP containing image metadata and 'statusCode'
     */
    def retrieveImagesPaged(String opusId, String profileId, boolean latest, String searchIdentifier, boolean useInternalPaths = false, boolean readonlyView = true, String pageSize, String startIndex) {
        Map response = [:]
        List allImages = []
        Integer numberOfLocalImages = 0
        def model = profileService.getProfile(opusId, profileId, latest)
        def profile = model.profile
        def opus = model.opus
        Map numberOfPublishedImagesMap = biocacheService.imageCount(searchIdentifier, opus)
        Map publishedImagesMap = biocacheService.retrieveImagesPaged(searchIdentifier, opus, pageSize, startIndex)
        //we want to display the images in a specific order - staged, private, published
        if (profile.privateMode && profile.stagedImages) {
            allImages.addAll(convertLocalImages(profile.stagedImages?:[], opus, profile, ImageType.STAGED, useInternalPaths, readonlyView))
            numberOfLocalImages = allImages.size()
        }

        // The collection may now, or may have been at some point, private, so look for any private images that may exist.
        // When a collection is changed from private to public, existing private images are NOT published automatically.
        //A public collection can also can have private images depending on the settings of "Image Visibility" in the Image Options section of the collection admin page
        if (profile.privateImages) {
            allImages.addAll(convertLocalImages(profile.privateImages ?: [], opus, profile, ImageType.PRIVATE, useInternalPaths, readonlyView))
            numberOfLocalImages = allImages.size()
        }
        if (publishedImagesMap?.resp?.occurrences?.size() > 0) {
            List publishedImageList = prepareImagesForDisplay(publishedImagesMap, opus, profile, readonlyView)
            if (publishedImageList && publishedImageList.size() > 0) {
                allImages.addAll(publishedImageList)
            }
        }
        response.statusCode = SC_OK
        //we don't have support for JSON objects or serialization so this is a workaround
        response.resp = [:]
        response.resp.images = allImages
        response.resp.count = numberOfPublishedImagesMap?.resp?.totalRecords + numberOfLocalImages

        response
    }


    def retrieveImages(String opusId, String profileId, boolean latest, String searchIdentifier, boolean useInternalPaths = false, boolean readonlyView = true) {
        Map response = [:]
        List allImages = []
        def model = profileService.getProfile(opusId, profileId, latest)
        def profile = model.profile
        def opus = model.opus

        def publishedImagesMap = biocacheService.retrieveImages(searchIdentifier, opus)
        if (publishedImagesMap?.resp?.occurrences?.size() > 0) {
            List publishedImageList = prepareImagesForDisplay(publishedImagesMap, opus, profile, readonlyView)
            allImages.addAll(publishedImageList)
        }

        //we want to display the images in a specific order - staged, private, published
        if (profile.privateMode && profile.stagedImages) {
            allImages.addAll(convertLocalImages(profile.stagedImages, opus, profile, ImageType.STAGED, useInternalPaths, readonlyView))
        }

        // The collection may now, or may have been at some point, private, so look for any private images that may exist.
        // When a collection is changed from private to public, existing private images are NOT published automatically.
        if (profile.privateImages) {
            allImages.addAll(convertLocalImages(profile.privateImages ?: [], opus, profile, ImageType.PRIVATE, useInternalPaths, readonlyView))
        }

        response.statusCode = SC_OK
        response.resp = allImages

        response
    }

    List prepareImagesForDisplay(def retrievedImages, def opus, def profile, boolean readonlyView) {
        List images = []

        if (retrievedImages && retrievedImages.statusCode == SC_OK) {
            List imagesAsMaps = retrievedImages.resp?.occurrences?.findResults { imageData ->
                boolean excluded = isExcluded(opus.approvedImageOption, profile.imageSettings ?: null, imageData.image)

                Map image = [
                        imageId         : imageData.image,
                        occurrenceId    : imageData.uuid,
                        largeImageUrl   : imageData.largeImageUrl,
                        thumbnailUrl    : imageData.thumbnailUrl,
                        dataResourceName: imageData.dataResourceName,
                        excluded        : excluded,
                        displayOption   : excluded ? ImageOption.EXCLUDE.name() : ImageOption.INCLUDE.name(),
                        caption         : profile.imageSettings.find {
                            it.imageId == imageData.image
                        }?.caption ?: '',
                        primary         : imageData.image == profile.primaryImage,
                        metadata        : imageData.imageMetadata && !imageData.imageMetadata.isEmpty() ? imageData.imageMetadata[0] : [:],
                        type            : ImageType.OPEN
                ]

                // only return images that have not been included, unless we are in the edit view, in which case we
                // need to show all available images in order for the editor to decide which to include/exclude
                if (!excluded || !readonlyView) {
                    image
                }
            }
            if (imagesAsMaps && imagesAsMaps.size() > 0) {
                images.addAll(imagesAsMaps)
            }
        } else {
            log.error("A HTTP status of ${retrievedImages.statusCode} was returned from the biocache image lookup")
        }
        images
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
                        thumbnailUrl    : "${prefix}/thumbnail/${it.imageId}${extension}?type=${type}",
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

    def updateLocalImageMetadata(String imageId, Map metadata) {
        webService.post("${grailsApplication.config.profile.service.url}/image/${enc(imageId)}/metadata", metadata)
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
        List<File> imagesToPublish = []
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
                    file.traverse(maxDepth: 0) {
                        if (it.file) {
                            imageFiles << it
                        }
                    }
                }
            }
        }

        imageFiles.each {
            String imageId = imageIdFromFile(it)
            def image = images[imageId]
            if (image) {
                imagesToPublish << it
            }
        }


        imagesToPublish.each {
            String imageId = imageIdFromFile(it)
            List<Map> localImage = [images[imageId]]
            List<Map> multimedia = localImage ? [
                    [
                            creator         : localImage.creator ?: "",
                            rights          : localImage.rights ?: "",
                            rightsHolder    : localImage.rightsHolder ?: "",
                            license         : localImage.licence ?: "",
                            title           : localImage.title ?: "",
                            description     : localImage.description ?: "",
                            created         : localImage.created ?: "",
                            originalFilename: localImage.originalFileName,
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
                //that has been assigned by uploadImage call
                Map imageDisplayOption = profile.imageSettings?.find { it.imageId == imageId }
                if (imageDisplayOption) {
                    imageDisplayOption?.imageId = uploadResponse.resp.images[0]
                }
            }
            if ((uploadResponse?.statusCode?.toInteger() >= 200) && (uploadResponse?.statusCode?.toInteger() <= 299)) {
                //don't delete the local images if the upload failed
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

    private static String buildFilePath(String parentDir, String collectionId, String profileId, String imageId) {
        parentDir + separator + collectionId + separator + profileId + separator + imageId
    }

    def enc(String value) {
        value ? URLEncoder.encode(value, "UTF-8") : ""
    }

}
