package au.org.ala.profile.hub

import au.org.ala.web.AuthService
import grails.test.mixin.TestFor
import org.apache.http.HttpStatus
import spock.lang.Specification

@TestFor(OpusController)
class OpusControllerSpec extends Specification {

    OpusController controller

    CollectoryService mockCollectory
    AuthService mockAuthService
    UserService mockUserService
    ProfileService mockProfileService

    def setup() {
        controller = new OpusController()
        mockCollectory = Mock(CollectoryService)
        controller.collectoryService = mockCollectory
        mockAuthService = Mock(AuthService)
        controller.authService = mockAuthService
        mockUserService = Mock(UserService)
        controller.userService = mockUserService

        mockProfileService = Mock(ProfileService)
        controller.profileService = mockProfileService
    }

    def "index should render the index view"() {
        when:
        controller.index()

        then:
        assert view == "/opus/index"
    }

    def "index should return a model with the expected number of elements"() {
        when:
        controller.index()

        then:
        assert model.size() == 5
        assert model.containsKey("opui")
        assert model.containsKey("dataResources")
        assert model.containsKey("logoUrl")
        assert model.containsKey("bannerUrl")
        assert model.containsKey("opui")
    }

    def "edit should render the edit view"() {
        setup:
        mockProfileService.getOpus(_) >> [uuid: "opusId1"]

        when:
        controller.edit()

        then:
        assert view == "/opus/edit"
    }

    def "edit should return a model with the expected number of elements"() {
        setup:
        mockProfileService.getOpus(_) >> [uuid: "opusId1"]

        when:
        controller.edit()

        then:
        assert model.size() == 5
        assert model.containsKey("opus")
        assert model.containsKey("logoUrl")
        assert model.containsKey("bannerUrl")
        assert model.containsKey("pageTitle")
        assert model.containsKey("currentUser")
    }

    def "show should render the show view"() {
        setup:
        mockProfileService.getOpus(_) >> [uuid: "opusId1"]

        when:
        controller.show()

        then:
        assert view == "/opus/show"
    }

    def "show should return a model with the expected number of elements"() {
        setup:
        mockProfileService.getOpus(_) >> [uuid: "opusId1"]

        when:
        controller.show()

        then:
        assert model.size() == 4
        assert model.containsKey("opus")
        assert model.containsKey("logoUrl")
        assert model.containsKey("bannerUrl")
        assert model.containsKey("pageTitle")
    }

    def "getJson should return a 400 (BAD REQUEST) if the opusId parameter is not set"() {
        when:
        controller.getJson()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "getJson should return the response from the service call on success"() {
        setup:
        mockProfileService.getOpus(_) >> [opus: [uuid: "opus1"]]

        when:
        params.opusId = "opus1"
        controller.getJson()

        then:
        assert response.status == HttpStatus.SC_OK
        assert response.json == [opus: [uuid: "opus1"]]
    }

    def "getJson should return a 404 code when no matching opus is found"() {
        setup:
        mockUserService.findUser(_) >> null

        when:
        params.opusId = "opus1"
        controller.getJson()

        then:
        assert response.status == HttpStatus.SC_NOT_FOUND
    }

    def "findUser should return a 400 (BAD REQUEST) if the userName parameter is not set"() {
        when:
        controller.findUser()

        then:
        assert response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "findUser should return the resp element of the response from the service call on success"() {
        setup:
        mockUserService.findUser(_) >> [resp: [resp: [userId: "user1"]], statusCode: 200]

        when:
        request.JSON = """{"userName": "fred"}"""
        controller.findUser()

        then:
        assert response.status == HttpStatus.SC_OK
        assert response.json == [resp: [userId: "user1"]]
    }

    def "findUser should return the error code from the service on failure of the service call"() {
        setup:
        mockUserService.findUser(_) >> [error: "something died!", statusCode: 666]

        when:
        request.JSON = """{"userName": "fred"}"""
        controller.findUser()

        then:
        assert response.status == 666
    }

}
