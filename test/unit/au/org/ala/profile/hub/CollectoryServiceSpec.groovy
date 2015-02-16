package au.org.ala.profile.hub

import au.org.ala.profile.hub.util.JsonUtil
import grails.test.mixin.TestFor
import groovy.json.JsonSlurper
import spock.lang.Specification

@TestFor(CollectoryService)
class CollectoryServiceSpec extends Specification {

    def CollectoryService service = new CollectoryService()

    def "getDataResources should return an empty map if there are no data resources available"() {
        setup:
        grailsApplication.config.collectory.service.url = "test"
        service.grailsApplication = grailsApplication
        JsonUtil mockJson = Mock(JsonUtil)
        mockJson.fromUrl(_) >> null
        service.jsonUtil = mockJson

        when:
        Map<String, String> result = service.getDataResources()

        then:
        assert result == [:]

    }

    def "getDataResources should return a map of UUID|Name for each data resource"() {
        setup:
        grailsApplication.config.collectory.service.url = "test"
        service.grailsApplication = grailsApplication
        JsonUtil mockJson = Mock(JsonUtil)
        mockJson.fromUrl(_) >>  new JsonSlurper().parseText("""[{"name": "Resource1",
                                                            "uri": "uri1",
                                                            "uid": "dr1"},
                                                           {"name": "Resource2",
                                                           "uri": "uri2",
                                                           "uid": "dr2"}]""")
        service.jsonUtil = mockJson

        when:
        Map<String, String> result = service.getDataResources()

        then:
        assert result == [dr1: "Resource1", dr2: "Resource2"]
    }
}
