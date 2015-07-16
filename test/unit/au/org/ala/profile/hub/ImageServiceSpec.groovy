package au.org.ala.profile.hub

import grails.test.mixin.TestFor
import org.apache.commons.httpclient.HttpStatus
import org.springframework.web.multipart.MultipartFile
import spock.lang.Specification

@TestFor(ImageService)
class ImageServiceSpec extends Specification {

    ImageService imageService
    BiocacheService biocacheService
    ProfileService profileService
    MultipartFile dummyFile

    String testId = UUID.randomUUID().toString()

    def setup() {
//        grailsApplication.config.image.staging.dir = System.getProperty("java.io.tmpdir") + "${testId}"
        grailsApplication.config.image.staging.dir = "/tmp/${testId}"
        new File("${grailsApplication.config.image.staging.dir}").mkdir()

        println "Test files will be written to ${grailsApplication.config.image.staging.dir}"

        biocacheService = Mock(BiocacheService)
        profileService = Mock(ProfileService)
        imageService = new ImageService()
        imageService.biocacheService = biocacheService
        imageService.profileService = profileService
        imageService.grailsApplication = grailsApplication

        dummyFile = Mock(MultipartFile)
        dummyFile.getName() >> "image.jpg"
        dummyFile.getOriginalFilename() >> "image.jpg"
    }

    def cleanup() {
        println "Deleting test directory ${grailsApplication.config.image.staging.dir}"
        boolean deleted = new File("${grailsApplication.config.image.staging.dir}").delete()
        println deleted ? "Succeded" : "Failed"
    }

    def "uploadImage should send the image to the biocache immediately if the profile is not in draft mode"() {
        setup:
        profileService.getProfile(_, _, _) >> [profile: [privateMode: false]]

        when:
        imageService.uploadImage("opusId", "profileId", "dataResourceId", [:], dummyFile)

        then:
        1 * biocacheService.uploadImage(_, _, _, _, _)
    }

    def "uploadImage should move the image to the staging directory and invoke profileService.recordStagedImage if the profile is in draft mode"() {
        setup:
        profileService.getProfile(_, _, _) >> [profile: [uuid: "profile1", privateMode: true]]

        when:
        imageService.uploadImage("opusId", "profileId", "dataResourceId", [:], dummyFile)

        then:
        1 * dummyFile.transferTo(_) // the actual destination file is randomly named, so we can't check the path
        1 * profileService.recordStagedImage(_, _, _)
        0 * biocacheService.uploadImage(_, _, _, _, _)
    }

    def "deleteStagedImage should do nothing if the profile has no staged images"() {
        setup:
        profileService.getProfile(_, _, _) >> [profile: [uuid: "profile1", privateMode: true]]

        when:
        boolean deleted = imageService.deleteStagedImage("opusId", "profileId", "imageId")

        then:
        deleted == false
        0 * profileService.recordStagedImage(_, _, _)
    }

    def "deleteStagedImage should delete the image file, and invoke profileService.recordStagedImage"() {
        setup:
        profileService.getProfile(_, _, _) >> [profile: [uuid: "profile1", stagedImages: [[imageId: "image1", originalFileName: "image1.jpg"], [imageId: "image2", originalFileName: "image2.jpg"]]]]
        File stagedDir = new File("${grailsApplication.config.image.staging.dir}/profile1")
        stagedDir.mkdir()
        File stagedFile1 = new File("${grailsApplication.config.image.staging.dir}/profile1/image1.jpg")
        File stagedFile2 = new File("${grailsApplication.config.image.staging.dir}/profile1/image2.jpg")
        stagedFile1.createNewFile()
        stagedFile2.createNewFile()

        expect:
        new File("${grailsApplication.config.image.staging.dir}/profile1").list().size() == 2

        when:
        boolean deleted = imageService.deleteStagedImage("opusId", "profileId", "image1")

        then:
        deleted == true
        1 * profileService.recordStagedImage(_, _, _)
        new File("${grailsApplication.config.image.staging.dir}/profile1").list().size() == 1
    }

    def "deleteStagedImage should delete the profile's temp dir if there are no more staged images"() {
        setup:
        profileService.getProfile(_, _, _) >> [profile: [uuid: "profile1", stagedImages: [[imageId: "image1", originalFileName: "image1.jpg"]]]]
        File stagedDir = new File("${grailsApplication.config.image.staging.dir}/profile1")
        stagedDir.mkdir()
        File stagedFile1 = new File("${grailsApplication.config.image.staging.dir}/profile1/image1.jpg")
        stagedFile1.createNewFile()

        expect:
        new File("${grailsApplication.config.image.staging.dir}/profile1").list().size() == 1

        when:
        boolean deleted = imageService.deleteStagedImage("opusId", "profileId", "image1")

        then:
        deleted == true
        1 * profileService.recordStagedImage(_, _, _)
        new File("${grailsApplication.config.image.staging.dir}/profile1").exists() == false
    }

    def "retrieveImages should return the response object if the call to the biocache fails"() {
        profileService.getProfile(_, _, _) >> [profile: [uuid: "profile1"]]
        setup:
        biocacheService.retrieveImages(_, _) >> [statusCode: HttpStatus.SC_BAD_REQUEST, resp: "Something blew up!!"]

        when:
        Map result = imageService.retrieveImages("opusId", "profileId", true, "sources", "search string")

        then:
        result == [statusCode: HttpStatus.SC_BAD_REQUEST, resp: "Something blew up!!"]
    }

    def "retrieveImages should fetch images from the biocache"() {
        setup:
        profileService.getProfile(_, _, _) >> [profile: [uuid: "profile1"]]

        Map image1Metadata = [title: "image 1"]
        Map image2Metadata = [title: "image 2"]

        biocacheService.retrieveImages(_, _) >> [statusCode: HttpStatus.SC_OK, resp: [occurrences: [
                [image: "image1", uuid: "occurrenceId1", largeImageUrl: "largeUrl1", thumbnailUrl: "thumbnailUrl1", dataResourceName: "resource1", imageMetadata: image1Metadata],
                [image: "image2", uuid: "occurrenceId2", largeImageUrl: "largeUrl2", thumbnailUrl: "thumbnailUrl2", dataResourceName: "resource2", imageMetadata: image2Metadata]
        ]]]

        when:
        Map result = imageService.retrieveImages("opusId", "profileId", true, "sources", "search string")

        then:
        result.resp.size() == 2
        result.resp[0].imageId == "image1"
        result.resp[0].occurrenceId == "occurrenceId1"
        result.resp[0].dataResourceName == "resource1"
        result.resp[0].largeImageUrl == "largeUrl1"
        result.resp[0].thumbnailUrl == "thumbnailUrl1"
        result.resp[0].metadata == image1Metadata
        result.resp[0].staged == false
        result.resp[1].imageId == "image2"
        result.resp[1].occurrenceId == "occurrenceId2"
        result.resp[1].dataResourceName == "resource2"
        result.resp[1].largeImageUrl == "largeUrl2"
        result.resp[1].thumbnailUrl == "thumbnailUrl2"
        result.resp[1].metadata == image2Metadata
        result.resp[1].staged == false
    }

    def "retrieveImages should fetch staged images if the profile is in draft mode"() {
        setup:
        Map stagedImage1 = [imageId: "staged1", originalFileName: "staged1.jpg", title: "staged image 1" ]
        Map stagedImage2 = [imageId: "staged2", originalFileName: "staged2.jpg", title: "staged image 2" ]

        profileService.getProfile(_, _, _) >> [opus: [title: "opus title"], profile: [uuid: "profileId", privateMode: true, stagedImages: [
                stagedImage1, stagedImage2
        ]]]

        Map image1Metadata = [title: "image 1"]
        Map image2Metadata = [title: "image 2"]

        biocacheService.retrieveImages(_, _) >> [statusCode: HttpStatus.SC_OK, resp: [occurrences: [
                [image: "image1", uuid: "occurrenceId1", largeImageUrl: "largeUrl1", thumbnailUrl: "thumbnailUrl1", dataResourceName: "resource1", imageMetadata: image1Metadata],
                [image: "image2", uuid: "occurrenceId2", largeImageUrl: "largeUrl2", thumbnailUrl: "thumbnailUrl2", dataResourceName: "resource2", imageMetadata: image2Metadata]
        ]]]

        when:
        Map result = imageService.retrieveImages("opusId", "profileId", true, "sources", "search string")

        then:
        result.resp.size() == 4
        result.resp[0].imageId == "image1"
        result.resp[0].occurrenceId == "occurrenceId1"
        result.resp[0].dataResourceName == "resource1"
        result.resp[0].largeImageUrl == "largeUrl1"
        result.resp[0].thumbnailUrl == "thumbnailUrl1"
        result.resp[0].metadata == image1Metadata
        result.resp[0].staged == false
        result.resp[1].imageId == "image2"
        result.resp[1].occurrenceId == "occurrenceId2"
        result.resp[1].dataResourceName == "resource2"
        result.resp[1].largeImageUrl == "largeUrl2"
        result.resp[1].thumbnailUrl == "thumbnailUrl2"
        result.resp[1].metadata == image2Metadata
        result.resp[1].staged == false

        result.resp[2].imageId == "staged1"
        result.resp[2].dataResourceName == "opus title"
        result.resp[2].largeImageUrl == "/opus/opusId/profile/profileId/stagedImage/staged1.jpg"
        result.resp[2].thumbnailUrl == "/opus/opusId/profile/profileId/stagedImage/staged1.jpg"
        result.resp[2].metadata == stagedImage1
        result.resp[2].staged == true
        result.resp[3].imageId == "staged2"
        result.resp[3].dataResourceName == "opus title"
        result.resp[3].largeImageUrl == "/opus/opusId/profile/profileId/stagedImage/staged2.jpg"
        result.resp[3].thumbnailUrl == "/opus/opusId/profile/profileId/stagedImage/staged2.jpg"
        result.resp[3].metadata == stagedImage2
        result.resp[3].staged == true
    }

    def "retrieveImages should not fetch staged images if the profile is not in draft mode"() {
        setup:
        Map stagedImage1 = [imageId: "staged1", originalFileName: "staged1.jpg", title: "staged image 1" ]
        Map stagedImage2 = [imageId: "staged2", originalFileName: "staged2.jpg", title: "staged image 2" ]

        profileService.getProfile(_, _, _) >> [opus: [title: "opus title"], profile: [uuid: "profileId", privateMode: false, stagedImages: [
                stagedImage1, stagedImage2
        ]]]

        Map image1Metadata = [title: "image 1"]
        Map image2Metadata = [title: "image 2"]

        biocacheService.retrieveImages(_, _) >> [statusCode: HttpStatus.SC_OK, resp: [occurrences: [
                [image: "image1", uuid: "occurrenceId1", largeImageUrl: "largeUrl1", thumbnailUrl: "thumbnailUrl1", dataResourceName: "resource1", imageMetadata: image1Metadata],
                [image: "image2", uuid: "occurrenceId2", largeImageUrl: "largeUrl2", thumbnailUrl: "thumbnailUrl2", dataResourceName: "resource2", imageMetadata: image2Metadata]
        ]]]

        when:
        Map result = imageService.retrieveImages("opusId", "profileId", true, "sources", "search string")

        then:
        result.resp.size() == 2
        result.resp[0].imageId == "image1"
        result.resp[1].imageId == "image2"
    }

    def "retrieveImages should fetch staged images even if there are no biocache images"() {
        setup:
        Map stagedImage1 = [imageId: "staged1", originalFileName: "staged1.jpg", title: "staged image 1" ]
        Map stagedImage2 = [imageId: "staged2", originalFileName: "staged2.jpg", title: "staged image 2" ]

        profileService.getProfile(_, _, _) >> [opus: [title: "opus title"], profile: [uuid: "profileId", privateMode: true, stagedImages: [
                stagedImage1, stagedImage2
        ]]]

        biocacheService.retrieveImages(_, _) >> [statusCode: HttpStatus.SC_OK, resp: [occurrences: []]]

        when:
        Map result = imageService.retrieveImages("opusId", "profileId", true, "sources", "search string")

        then:
        result.resp.size() == 2
        result.resp[0].imageId == "staged1"
        result.resp[1].imageId == "staged2"
    }

    def "retrieveImages should set the excluded and primary flags for staged images"() {
        setup:
        Map stagedImage1 = [imageId: "staged1", originalFileName: "staged1.jpg", title: "staged image 1" ]
        Map stagedImage2 = [imageId: "staged2", originalFileName: "staged2.jpg", title: "staged image 2" ]

        profileService.getProfile(_, _, _) >> [opus: [title: "opus title"], profile: [uuid: "profileId", privateMode: true, stagedImages: [
                stagedImage1, stagedImage2
        ], primaryImage: "staged1", excludedImages: ["staged2"]]]

        biocacheService.retrieveImages(_, _) >> [statusCode: HttpStatus.SC_OK, resp: [occurrences: []]]

        when:
        Map result = imageService.retrieveImages("opusId", "profileId", true, "sources", "search string")

        then:
        result.resp.size() == 2
        result.resp[0].imageId == "staged1"
        result.resp[0].primary == true
        result.resp[0].excluded == false
        result.resp[1].imageId == "staged2"
        result.resp[1].primary == false
        result.resp[1].excluded == true
    }

    def "retrieveImages should set the excluded and primary flags for biocache images"() {
        setup:
        profileService.getProfile(_, _, _) >> [profile: [uuid: "profile1", primaryImage: "image1", excludedImages: ["image2"]]]

        Map image1Metadata = [title: "image 1"]
        Map image2Metadata = [title: "image 2"]

        biocacheService.retrieveImages(_, _) >> [statusCode: HttpStatus.SC_OK, resp: [occurrences: [
                [image: "image1", uuid: "occurrenceId1", largeImageUrl: "largeUrl1", thumbnailUrl: "thumbnailUrl1", dataResourceName: "resource1", imageMetadata: image1Metadata],
                [image: "image2", uuid: "occurrenceId2", largeImageUrl: "largeUrl2", thumbnailUrl: "thumbnailUrl2", dataResourceName: "resource2", imageMetadata: image2Metadata]
        ]]]

        when:
        Map result = imageService.retrieveImages("opusId", "profileId", true, "sources", "search string")

        then:
        result.resp.size() == 2
        result.resp[0].imageId == "image1"
        result.resp[0].primary == true
        result.resp[0].excluded == false
        result.resp[1].imageId == "image2"
        result.resp[1].primary == false
        result.resp[1].excluded == true
    }

    def "publishImage should do nothing if there are no staged images"() {
        setup:
        profileService.getProfile(_, _, _) >> [profile: [uuid: "profile1"]]

        when:
        imageService.publishImages("opusId", "profileId")

        then:
        0 * profileService.updateProfile(_, _, _, _)
        0 * biocacheService.uploadImage(_, _, _, _, _)
        0 * profileService.recordStagedImage(_, _, _)
    }

    def "publishImages should upload images to the biocache and delete the staged files"() {
        setup:
        profileService.getProfile(_, _, _) >> [opus: [dataResourceUid: "dr1"], profile: [uuid: "profile1", stagedImages: [
                [imageId: "image1", originalFileName: "image1.jpg"],
                [imageId: "image2", originalFileName: "image2.jpg"]
        ]]]
        File stagedDir = new File("${grailsApplication.config.image.staging.dir}/profile1")
        stagedDir.mkdir()
        File stagedFile1 = new File("${grailsApplication.config.image.staging.dir}/profile1/image1.jpg")
        File stagedFile2 = new File("${grailsApplication.config.image.staging.dir}/profile1/image2.jpg")
        stagedFile1.createNewFile()
        stagedFile2.createNewFile()

        expect:
        new File("${grailsApplication.config.image.staging.dir}/profile1").list().size() == 2

        when:
        imageService.publishImages("opusId", "profile1")

        then:
        2 * profileService.recordStagedImage(_, _, _)
        2 * biocacheService.uploadImage(_, _, _, _, _)
        new File("${grailsApplication.config.image.staging.dir}/profile1").exists() == false
    }

    def "publishImages should replace staged ids with permanent ids for primary and excluded images that were staged"() {
        setup:
        profileService.getProfile(_, _, _) >> [opus: [dataResourceUid: "dr1"], profile: [uuid: "profile1", stagedImages: [
                [imageId: "image1", originalFileName: "image1.jpg"],
                [imageId: "image2", originalFileName: "image2.jpg"]
        ], primaryImage: "image1", excludedImages: ["image2"]]]
        File stagedDir = new File("${grailsApplication.config.image.staging.dir}/profile1")
        stagedDir.mkdir()
        File stagedFile1 = new File("${grailsApplication.config.image.staging.dir}/profile1/image1.jpg")
        File stagedFile2 = new File("${grailsApplication.config.image.staging.dir}/profile1/image2.jpg")
        stagedFile1.createNewFile()
        stagedFile2.createNewFile()

        biocacheService.uploadImage(_, _, _, stagedFile1, _) >> [resp: [images: ["permId1"]]]
        biocacheService.uploadImage(_, _, _, stagedFile2, _) >> [resp: [images: ["permId2"]]]

        expect:
        new File("${grailsApplication.config.image.staging.dir}/profile1").list().size() == 2

        Map expectedUpdates = [primaryImage: "permId1", excludedImages: ["permId2"]]

        when:
        imageService.publishImages("opusId", "profile1")

        then:
        1 * profileService.updateProfile(_, _, expectedUpdates, true)
    }
}
