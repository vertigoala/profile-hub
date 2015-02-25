package au.org.ala

import au.org.ala.profile.hub.ProfileService
import au.org.ala.profile.hub.WebService
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
        String expectedUrl = "http://profile.service/classification?profileId=guid"

        when:
        service.getClassification("guid")

        then:
        1 * webService.get(expectedUrl)
    }

    def "updateLinks() should construct the correct Profile Service URL"() {
        setup:
        String expectedUrl = "http://profile.service/profile/links/profileId"

        when:
        service.updateLinks("profileId", "linkdata")

        then:
        1 * webService.doPost(expectedUrl, [profileId: "profileId", links: "linkdata", userId: "user1", userDisplayName: "fred smith"])
    }

}
