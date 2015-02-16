package au.org.ala.profile.hub

import au.org.ala.profile.hub.util.JsonUtil
import grails.test.mixin.TestFor
import org.apache.commons.logging.Log
import spock.lang.Specification


@TestFor(BieService)
class BieServiceSpec extends Specification {

    def "getSpeciesProfile should log a warning and return null when an exception occurs"() {
        setup:
        BieService service = new BieService()
        JsonUtil mockJson = Mock(JsonUtil)
        service.jsonUtil = mockJson
        mockJson.fromUrl(_) >> { throw new Exception("test") }
        Log mockLog = Mock(Log)
        service.log = mockLog


        when:
        def result = service.getSpeciesProfile("test")

        then:
        1 * mockLog.warn(_,_)
        assert result == null
    }

}
