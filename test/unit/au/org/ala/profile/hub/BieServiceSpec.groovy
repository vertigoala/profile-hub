package au.org.ala.profile.hub

import grails.test.mixin.TestFor
import spock.lang.Specification


@TestFor(BieService)
class BieServiceSpec extends Specification {
    BieService service
    WebService webService

    def setup() {
        grailsApplication.config.bie.base.url = "http://bie.service"

        webService = Mock(WebService)
        service = new BieService()
        service.grailsApplication = grailsApplication

        service.webService = webService

    }

    def "getSpeciesProfile() should construct the correct BIE Service URL"() {
        setup:
        String expectedUrl = "http://bie.service/ws/species/guid"

        when:
        service.getSpeciesProfile("guid")

        then:
        1 * webService.get(expectedUrl)
    }

}
