package au.org.ala

import au.org.ala.profile.hub.ProfileService
import au.org.ala.profile.hub.WebService
import grails.test.mixin.TestFor
import spock.lang.Specification


@TestFor(ProfileService)
class ProfileServiceSpec extends Specification {
    // TODO when testing updateAttribute, make sure the response includes the attribute for BOTH updating and adding a new attribute...

    ProfileService service
    WebService webService

    def setup() {
        grailsApplication.config.profile.service.url = "http://profile.service"

        webService = Mock(WebService)
        service = new ProfileService()
        service.grailsApplication = grailsApplication

        service.webService = webService

    }

    def "getClassification() should construct the correct Profile Service URL"() {
        setup:
        String expectedUrl = "http://profile.service/classification?profileId=guid"

        when:
        service.getClassification("guid")

        then:
        1 * webService.get(expectedUrl)
    }

}
