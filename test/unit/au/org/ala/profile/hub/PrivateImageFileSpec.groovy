package au.org.ala.profile.hub

import au.org.ala.profile.LocalImage
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.io.FileType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.mock.web.MockMultipartHttpServletRequest
import org.springframework.web.multipart.MultipartFile
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by shi131 on 24/03/2016.
 *
 * Regression tests to check we can save, retrieve and delete local private images
 *
 *
 *
 */
@Mock(ImageService)
@Stepwise
@TestFor(ProfileController)
class PrivateImageFileSpec extends Specification {

    @Shared
    MultipartFile imageFile
    @Shared
    def mockRequest
    @Shared
    File profileDir
    @Shared
    File tempDir
    @Shared
    BiocacheService biocacheService
    @Shared
    BiocacheService biocacheServiceReal
    @Shared
    ProfileService profileService
    @Shared
    ImageService imageService


    def setupSpec() {

        String testId = UUID.randomUUID().toString()
        String path = System.getProperty("java.io.tmpdir") // /var/folders/dp/j3l0rt6s6fs5km4n4b_85f89pt4fc3/T/
        if (!path.endsWith("/")) {
            path += "/"
        }
        grailsApplication.config.image.private.dir = "${path}${testId}"
        Boolean isProfileDirectory = new File("${grailsApplication.config.image.private.dir}/collection1/profile1").mkdirs()
         Boolean isTempDir = new File("${grailsApplication.config.temp.file.location}").mkdirs()
        profileDir = new File("${grailsApplication.config.image.private.dir}/collection1/profile1/")
        tempDir = new File("${grailsApplication.config.temp.file.location}")
        imageFile = loadFileWIthContent('test/data/puffinswim.jpg', 'puffinswim.jpg', 'originalpuffin.jpg', 'image/jpg')

        imageService = new ImageService()
        biocacheService = Mock(BiocacheService)  //these communicate with external services
        profileService = Mock(ProfileService)    //these communicate with external services
        biocacheServiceReal = new BiocacheService()
        imageService.biocacheService = biocacheService
        imageService.profileService = profileService
        imageService.grailsApplication = grailsApplication
        imageService.profileService.getProfile(_, _, _) >> [profile: [scientificName: 'Olympia', uuid: 'profile1', privateImages: [123]], opus: [keepImagesPrivate: true, uuid: 'collection1']]
        imageService.biocacheService.uploadImage(_,_,_,_,_, _) >> [statusCode: 201, resp: ['images':['123']]]

        mockRequest = new MockMultipartHttpServletRequest()
        controller.metaClass.request = mockRequest

    }

    def cleanupSpec() {
        File directoryToDelete = new File("${grailsApplication.config.image.private.dir}")
        boolean deleted = directoryToDelete.deleteDir()
        println "Temporary directory has been successfully removed: ${deleted}"
    }


    //1. Can we save files: profileController.upLoadImage this has to run first or image file won't be there for other tests
    def "Private image is saved"() {
        given: "a new image file"
        mockRequest.addFile(imageFile)
        MultipartFile file = mockRequest.getFile("puffinswim.jpg")
        final fileTransferrableAdapter = new MultipartFileTransferrableAdapter(multipartFile: file)
        List images = []
        when: "we save the image"
        imageService.uploadImage('/profile-hub', 'collection1', 'profile1', '1', [:], fileTransferrableAdapter)
        File imageDir = profileDir.listFiles()[0] //this is generated by upLoadImage
        String imageId = imageDir.getName()
        imageDir.eachFile(FileType.FILES) { images << it.name }
        File imageFile = new File(imageDir.getAbsolutePath() + "/${imageId}.jpg")
        then: "the image has been persisted to disk"
        assert images.size() == 1
        and: "the image name is correct"
        assert imageFile.exists()
        and: "its existance has been recorded by the service"
        assert response.status == 200 //the new file metadata has been registered with profile service
    }


    def "Tiles are made for private image"() {
        given: "an existing private image file"
        List subDirectories = []
        File imageDir = profileDir.listFiles()[0]
        String imageId = imageDir.getName()
        imageDir.eachFile(FileType.DIRECTORIES) { subDirectories << it.name }
        when: "we examine disk storage for the existence of tiles"
        File tilesDir = new File(imageDir.getAbsolutePath() + "/${imageId}_tiles")
        then: "there is a tiles subdirectory"
        assert tilesDir.exists()
        and: "it contains tiles"
        assert tilesDir.listFiles().size() > 0
    }

    def "Thumbnails are made for private image"() {
        given: "an existing private image file"
        List subDirectories = []
        File imageDir = profileDir.listFiles()[0]
        String imageId = imageDir.getName()
        imageDir.eachFile(FileType.DIRECTORIES) { subDirectories << it.name }
        when: "we examine disk storage for the existence of tiles"
        File tilesDir = new File(imageDir.getAbsolutePath() + "/${imageId}_thumbnails")
        then: "there is a tiles subdirectory"
        assert tilesDir.exists()
        and: "it contains tiles"
        assertFalse("What happened to our thumbnails?", tilesDir.listFiles().size() == 0)
    }

    //2. Can we retrieve files: profileController.retrieveLocalThumbnail tricky to test as the imageId is dynamically regenerated at
    //the time the file is saved and this imageId forms part of the directory structure
    def "Private image thumbnail is retrieved"() {
        given: "an existing private image file"
        File imageDir = profileDir.listFiles()[0]
        String imageId = imageDir.getName() + '.jpg'
        and: "params matching this image"
        params.type = 'PRIVATE'
        params.opusId = 'collection1'
        params.profileId = 'profile1'
        params.imageId = imageId
        when: "the image is requested"
        def thumbNail = controller.retrieveLocalThumbnailImage()
        then: "the thumbnail is returned"
        assertNotNull("Image has not been retrieved ", thumbNail)

    }

    //3.biocacheService.copyFileForUpLoad
    def "File upload does not move the image file"(){
        given: "an existing private image and a real biocache service"
        File imageDir = profileDir.listFiles()[0]
        String imageId = imageDir.getName()
        String opusId = 'collection1'
        String profileId = 'profile1'
        LocalImage privateImage = populateLocalImage(imageId)
        when: "we copy it to the upload location"
        String filename = biocacheServiceReal.copyFileForUpload(imageId,imageDir, tempDir)
        then: "we keep the original image location in case the upload fails"
        assertFalse("File has been moved unexpectedly",profileDir.listFiles().size() == 0)
    }


    //4.imageService.publishPrivateImage
    def "A private image can be sent to central image location"() {
        given: "an  existing private image"
        File imageDir = profileDir.listFiles()[0]
        String imageId = imageDir.getName()
        String opusId = 'collection1'
        String profileId = 'profile1'
        LocalImage privateImage = populateLocalImage(imageId)
        ProfileService profileServiceMock = Mock(ProfileService)  //we don't seem to be able to override the mocked method once it has been set above, so we are creating a new mock
        imageService.setProfileService(profileServiceMock)
        imageService.profileService.getProfile(_, _, _) >> [profile: [privateImages: [privateImage],uuid: 'profile1'], opus: [keepImagesPrivate: true, uuid: 'collection1', usePrivateRecordData:true]]
        when: "we try to publish the image, update its status and remove it from local storage"
        imageService.publishPrivateImage(opusId,profileId,imageId)
        then: "its status is updated"
        1 * profileServiceMock.recordPrivateImage(_, _, _)
        and: "the file is deleted from local storage"
        assertFalse(imageDir.exists())

    }

    //@Todo
    //5. profileController.deleteLocalImage
    def "Private image is not deleted if upload fails"() {

    }


    //@Todo
    //6. imageService.retrieveImages
    def "Information about a private Image can be retrieved"() {

    }
     //@Todo
    //7. profileController.deleteLocalImage
    def "Private image is deleted"() {

    }

    /**
     *  Note we need a real file here although we are using a Mock as you cannot instantiate a MultipartFile manually.
     *  For example we need a real image if we are going to test images - the tiler is especially unforgiving if it
     *  can't load image data
     *
     * @param pathToFile - the relative path to file, doesn't start with / and ends with extension; e.g. "path/to/the/file.txt"
     * @param fileName - name of file including extension; e.g. "file.txt"
     * @param originalFileName - original file name including extension; e.g "originalFileName.txt"
     * @param contentType - content type; one of "text/plain","image/jpg", "image/gif", "application/pdf", "text/html", "application/zip"
     * @return MultipartFile  suitable for saving to disk or downloading or displaying on screen
     */
    MultipartFile loadFileWIthContent(String pathToFile, String fileName, String originalFileName, String contentType) {
        Path path = Paths.get(pathToFile);
        byte[] content = null;
        try {
            content = Files.readAllBytes(path);
        } catch (final IOException e) {
            log.error(e)
        }
        MultipartFile result = new MockMultipartFile(fileName,
                originalFileName, contentType, content);
    }

    LocalImage populateLocalImage(String imageId) {
        LocalImage privateImage = new LocalImage([imageId:imageId])
        privateImage.setImageId(imageId)
        privateImage.setOriginalFileName('originalName.jpg')
        privateImage.setTitle('title')
        privateImage.setDescription('description')
        privateImage.setRightsHolder('FindPuffins')
        privateImage.setCreator('maker')
        privateImage.setCreated(new Date().format('yyyy-MM-dd'))
        privateImage.setLicence('Creative Commons Attribution')

        privateImage

    }


}
