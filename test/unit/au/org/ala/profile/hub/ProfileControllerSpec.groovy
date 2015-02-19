package au.org.ala.profile.hub

import au.org.ala.web.AuthService
import grails.test.mixin.TestFor
import org.apache.http.HttpStatus
import spock.lang.Specification

@TestFor(ProfileController)
class ProfileControllerSpec extends Specification {

    ProfileController controller
    ProfileService profileService
    AuthService authService

    def setup() {
        controller = new ProfileController()

        profileService = Mock(ProfileService)
        authService = Mock(AuthService)
        controller.profileService = profileService
        controller.authService = authService

        authService.getDisplayName() >> "Fred Bloggs"
    }

    def "edit should return a 404 if the profile is not found"() {
        setup:
        profileService.getProfile(_) >> null

        when:
        params.profileId = "bla"
        controller.edit()

        then:
        assert response.status == HttpStatus.SC_NOT_FOUND
    }

    def "edit should return a 400 (BAD REQUEST) if a profile id is not provided"() {
        when:
        controller.edit()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "edit should return the profile, with edit and current user properties added to the model"() {
        setup:
        profileService.getProfile(_) >> [profile: "bla"]

        when:
        params.profileId = "bla"
        controller.edit()

        then:
        assert response.status == HttpStatus.SC_OK
        assert model.containsKey("profile")
        assert model.edit == true
        assert model.currentUser == "Fred Bloggs"
    }

    def "show should return a 404 if the profile is not found"() {
        setup:
        profileService.getProfile(_) >> null

        when:
        params.profileId = "bla"
        controller.show()

        then:
        assert response.status == HttpStatus.SC_NOT_FOUND
    }

    def "show should return a 400 (BAD REQUEST) if a profile id is not provided"() {
        when:
        controller.show()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "show should return the profile, with edit = false added to the model"() {
        setup:
        profileService.getProfile(_) >> [profile: "bla"]

        when:
        params.profileId = "bla"
        controller.show()

        then:
        assert response.status == HttpStatus.SC_OK
        assert model.containsKey("profile")
        assert model.edit == false
    }

    def "getJson should return a 404 if the profile is not found"() {
        setup:
        profileService.getProfile(_) >> null

        when:
        params.profileId = "bla"
        controller.getJson()

        then:
        assert response.status == HttpStatus.SC_NOT_FOUND
    }

    def "getJson should return a 400 (BAD REQUEST) if a profile id is not provided"() {
        when:
        controller.getJson()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "getJson should return the profile, with edit = false added to the model"() {
        setup:
        profileService.getProfile(_) >> [profile: "bla"]

        when:
        params.profileId = "bla"
        controller.getJson()

        then:
        assert response.status == HttpStatus.SC_OK
        assert response.getJson() != null
    }

    def "updateBHLLinks should return a 400 (BAD REQUEST) if the json body is empty"() {
        when:
        controller.updateBHLLinks()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "updateBHLLinks should return a 400 (BAD REQUEST) if the profile id is not included in the json body"() {
        when:
        request.JSON = """{links: "xyz"}"""
        controller.updateBHLLinks()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "updateBHLLinks should return a 400 (BAD REQUEST) if the links are not included in the json body"() {
        when:
        request.JSON = """{profileId: "1"}"""
        controller.updateBHLLinks()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "updateBHLLinks should return the updated links as JSON on success"() {
        setup:
        profileService.updateBHLLinks(_, _) >> [resp: ["link1", "link2"], statusCode: 200]

        when:
        request.JSON = """{profileId: "1", links: "xyz"}"""
        controller.updateBHLLinks()

        then:
        assert response.status == HttpStatus.SC_OK
        assert response.json == ["link1", "link2"]
    }

    def "updateBHLLinks should return the error code from the service on failure of the service call"() {
        setup:
        profileService.updateBHLLinks(_, _) >> [error: "something died!", statusCode: 666]

        when:
        request.JSON = """{profileId: "1", links: "xyz"}"""
        controller.updateBHLLinks()

        then:
        assert response.status == 666
    }

    def "updateLinks should return a 400 (BAD REQUEST) if the json body is empty"() {
        when:
        controller.updateLinks()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "updateLinks should return a 400 (BAD REQUEST) if the profile id is not included in the json body"() {
        when:
        request.JSON = """{links: "xyz"}"""
        controller.updateLinks()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "updateLinks should return a 400 (BAD REQUEST) if the links are not included in the json body"() {
        when:
        request.JSON = """{profileId: "1"}"""
        controller.updateLinks()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "updateLinks should return the updated links as JSON on success"() {
        setup:
        profileService.updateLinks(_, _) >> [resp: ["link1", "link2"], statusCode: 200]

        when:
        request.JSON = """{profileId: "1", links: "xyz"}"""
        controller.updateLinks()

        then:
        assert response.status == HttpStatus.SC_OK
        assert response.json == ["link1", "link2"]
    }

    def "updateLinks should return the error code from the service on failure of the service call"() {
        setup:
        profileService.updateLinks(_, _) >> [error: "something died!", statusCode: 666]

        when:
        request.JSON = """{profileId: "1", links: "xyz"}"""
        controller.updateLinks()

        then:
        assert response.status == 666
    }

    def "updateAttribute should return a 400 (BAD REQUEST) if the json body is empty"() {
        when:
        controller.updateAttribute()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "updateAttribute should return a 400 (BAD REQUEST) if the profile id is not included in the json body"() {
        when:
        request.JSON = """{attributeId: "xyz"}"""
        controller.updateAttribute()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "updateAttribute should return a 400 (BAD REQUEST) if the attribute id is not included in the json body"() {
        when:
        request.JSON = """{profileId: "1"}"""
        controller.updateAttribute()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "updateAttribute should accept a blank attribute id to create a new Attribute"() {
        setup:
        profileService.updateAttribute(_, _, _, _) >> [resp: [attributeId: "id1", success: true], statusCode: 200]

        when:
        request.JSON = """{profileId: "1", attributeId: ""}"""
        controller.updateAttribute()

        then:
        assert response.status == HttpStatus.SC_OK
        assert response.json == [attributeId: "id1", success: true]
    }

    def "updateAttribute should return the updated attribute id and a success indicator as JSON on success"() {
        setup:
        profileService.updateAttribute(_, _, _, _) >> [resp: [attributeId: "id1", success: true], statusCode: 200]

        when:
        request.JSON = """{profileId: "1", attributeId: "xyz"}"""
        controller.updateAttribute()

        then:
        assert response.status == HttpStatus.SC_OK
        assert response.json == [attributeId: "id1", success: true]
    }

    def "updateAttribute should return the error code from the service on failure of the service call"() {
        setup:
        profileService.updateAttribute(_, _, _, _) >> [error: "something died!", statusCode: 666]

        when:
        request.JSON = """{profileId: "1", attributeId: "xyz"}"""
        controller.updateAttribute()

        then:
        assert response.status == 666
    }

    def "deleteAttribute should return a 400 (BAD REQUEST) if the json body is empty"() {
        when:
        controller.deleteAttribute()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "deleteAttribute should return a 400 (BAD REQUEST) if the profile id is not included in the json body"() {
        when:
        params.attributeId = "att1"
        controller.deleteAttribute()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "deleteAttribute should return a 400 (BAD REQUEST) if the attribute id is not included in the json body"() {
        when:
        params.profileId = "profile1"
        controller.deleteAttribute()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "deleteAttribute should return a success indicator as JSON"() {
        setup:
        profileService.deleteAttribute(_, _) >> [success: true, statusCode: 200]

        when:
        params.profileId = "profile1"
        params.attributeId = "att1"
        controller.deleteAttribute()

        then:
        assert response.status == HttpStatus.SC_OK
        assert response.json == [success: true]
    }

    def "deleteAttribute should return the error code from the service on failure of the service call"() {
        setup:
        profileService.deleteAttribute(_, _) >> [error: "something died!", statusCode: 666]

        when:
        params.profileId = "profile1"
        params.attributeId = "att1"
        controller.deleteAttribute()

        then:
        assert response.status == 666
    }
}
