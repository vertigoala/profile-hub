package au.org.ala.profile.hub.util

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

@TestMixin([GrailsUnitTestMixin])
@Unroll
class JsonUtilSpec extends Specification {

    def "a url must be provided"() {
        when:
        new JsonUtil().fromUrl(url)

        then:
        thrown java.lang.AssertionError

        where:
        url << [null, "", "   ", "\t"]
    }
}
