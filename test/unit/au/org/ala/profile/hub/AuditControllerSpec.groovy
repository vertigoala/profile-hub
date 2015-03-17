package au.org.ala.profile.hub

import grails.test.mixin.TestFor
import org.apache.http.HttpStatus
import spock.lang.Specification

@TestFor(AuditController)
class AuditControllerSpec extends Specification {

    ProfileService profileService
    AuditController controller

    def setup() {
        profileService = Mock(ProfileService)
        controller = new AuditController();

        controller.profileService = profileService
    }

    def "object() should pass params.id as an 'objectId' parameter to the profile service"() {
        when:
        params.id = "12345"
        controller.object()

        then:
        1 * profileService.getAuditHistory("12345", null) >> [resp: [], statusCode: 200]
    }

    def "object() should return a 400 (BAD_REQUEST) if no id has been provided"() {
        when:
        params.somethingElse = "fred" // to make sure that the code doesn't expect an empty param map in order to fail
        controller.object()

        then:
        response.status == HttpStatus.SC_BAD_REQUEST
    }

    def "user() should pass params.id as a 'userId' parameter to the profile service"() {
        when:
        params.id = "12345"
        controller.user()

        then:
        1 * profileService.getAuditHistory(null, "12345") >> [resp: [], statusCode: 200]
    }

    def "user() should return a 400 (BAD_REQUEST) if no id has been provided"() {
        when:
        controller.user()

        then:
        response.status == HttpStatus.SC_BAD_REQUEST
    }
}
