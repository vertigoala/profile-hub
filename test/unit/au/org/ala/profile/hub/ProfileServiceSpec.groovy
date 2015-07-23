package au.org.ala.profile.hub

import au.org.ala.web.AuthService
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(ProfileService)
class ProfileServiceSpec extends Specification {
    // TODO when testing updateAttribute, make sure the response includes the attribute for BOTH updating and adding a new attribute...

    ProfileService service
    WebService webService
    AuthService authService

    def setup() {
        grailsApplication.config.profile.service.url = "http://profile.service"

        authService = Mock(AuthService)
        authService.getUserId() >> "user1"
        authService.userDetails() >> [userDisplayName: "fred smith"]

        webService = Mock(WebService)
        service = new ProfileService()
        service.grailsApplication = grailsApplication

        service.webService = webService
        service.authService = authService
    }

    def "getClassification() should construct the correct Profile Service URL"() {
        setup:
        String expectedUrl = "http://profile.service/opus/opusid/profile/profileid/classification?guid=guid&opusId=opusid"

        when:
        service.getClassification("opusid", "profileid", "guid")

        then:
        1 * webService.get(expectedUrl)
    }

    def "updateLinks() should construct the correct Profile Service URL"() {
        setup:
        String expectedUrl = "http://profile.service/opus/opusid/profile/profileId/links"

        when:
        service.updateLinks("opusid", "profileId", "linkdata")

        then:
        1 * webService.doPost(expectedUrl, [profileId: "profileId", links: "linkdata", userId: "user1", userDisplayName: "fred smith"])
    }

    def "updateAuthorship() should construct the correct Profile Service URL"() {
        setup:
        String expectedUrl = "http://profile.service/opus/opusid/profile/profileId/authorship"

        when:
        service.updateAuthorship("opusid", "profileId", [category: "author", text: "fred"])

        then:
        1 * webService.doPost(expectedUrl, [category: "author", text: "fred"])
    }

}
