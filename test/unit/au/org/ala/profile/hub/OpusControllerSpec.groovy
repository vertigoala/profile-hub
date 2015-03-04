package au.org.ala.profile.hub

import au.org.ala.profile.hub.util.JsonUtil
import au.org.ala.web.AuthService
import grails.converters.JSON
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(OpusController)
class OpusControllerSpec extends Specification {

    OpusController controller

    JsonUtil mockJsonUtil
    CollectoryService mockCollectory
    AuthService mockAuthService
    UserService mockUserService
    ProfileService mockProfileService

    def setup() {
        controller = new OpusController()
        mockJsonUtil = Mock(JsonUtil)
        controller.jsonUtil = mockJsonUtil
        mockJsonUtil.fromUrl(_) >> [attributeVocabUuid: "test", dataResourceUid: "test"]
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
        when:
        controller.edit()

        then:
        assert view == "/opus/edit"
    }

    def "edit should return a model with the expected number of elements"() {
        when:
        controller.edit()

        then:
        assert model.size() == 8
        assert model.containsKey("opus")
        assert model.containsKey("dataResource")
        assert model.containsKey("dataResources")
        assert model.containsKey("logoUrl")
        assert model.containsKey("bannerUrl")
        assert model.containsKey("pageTitle")
        assert model.containsKey("vocab")
        assert model.containsKey("currentUser")
    }

    def "show should render the show view"() {
        when:
        controller.show()

        then:
        assert view == "/opus/show"
    }

    def "show should return a model with the expected number of elements"() {
        when:
        controller.show()

        then:
        assert model.size() == 4
        assert model.containsKey("opus")
        assert model.containsKey("logoUrl")
        assert model.containsKey("bannerUrl")
        assert model.containsKey("pageTitle")
    }


    def "findUser should render the user details as JSON"() {
        when:
        params.userName = "fred"
        controller.findUser()

        then:
        assert response.contentType == "application/json"
    }

}
