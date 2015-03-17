package au.org.ala.profile.hub

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(SpeciesListService)
class SpeciesListServiceSpec extends Specification {
    SpeciesListService service
    WebService webService

    def setup() {
        grailsApplication.config.lists.base.url = "http://lists.base"

        webService = Mock(WebService)
        service = new SpeciesListService()
        service.grailsApplication = grailsApplication

        service.webService = webService

    }

    def "getListsForGuid() should construct the correct Species List URL"() {
        setup:
        String expectedUrl = "http://lists.base/ws/species/guid"

        when:
        service.getListsForGuid("guid")

        then:
        1 * webService.get(expectedUrl)
    }
}
