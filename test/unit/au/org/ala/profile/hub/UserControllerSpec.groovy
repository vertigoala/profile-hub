package au.org.ala.profile.hub

import grails.test.mixin.TestFor
import org.apache.http.HttpStatus
import spock.lang.Specification

@TestFor(UserController)
class UserControllerSpec extends Specification {
    UserController controller
    UserService mockUserService

    def setup() {
        controller = new UserController()
        mockUserService = Mock(UserService)
        controller.userService = mockUserService
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
        params.userName = "fred"
        controller.findUser()

        then:
        assert response.status == HttpStatus.SC_OK
        assert response.json == [resp: [userId: "user1"]]
    }

    def "findUser should return the error code from the service on failure of the service call"() {
        setup:
        mockUserService.findUser(_) >> [error: "something died!", statusCode: 666]

        when:
        params.userName = "fred"
        controller.findUser()

        then:
        assert response.status == 666
    }
}
