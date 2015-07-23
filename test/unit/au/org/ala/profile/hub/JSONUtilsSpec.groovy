package au.org.ala.profile.hub

import grails.converters.JSON
import org.codehaus.groovy.runtime.typehandling.GroovyCastException
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by rui008 on 23/07/15.
 */
class JSONUtilsSpec extends Specification {
    def "safe nesteds null"() {
        when: "this shows how JSON.parse fails misarable because they decided to return JSONObject.Null en vez de null"
        def object = JSON.parse("{'nullList':null}")
        List fallo = object.nullList

        then: "Fails because JSONObject.Null cannot be casted to anything but Object"
        thrown(GroovyCastException)

        when: "This shows a way to fix it"
        object = JSONUtils.validateAndParseJSON("{'nullList':null}")
        List nullList = object.nullList

        then:
        noExceptionThrown()
        nullList == null

        when: "test to see if it works with nested objects"

        object = JSONUtils.validateAndParseJSON("{'a':[{'nullList':null}]}")
        List nullList2 = object.a[0].nullList
        then:
        noExceptionThrown()
        nullList2 == null
    }

    @Unroll
    def "safe null"() {
        when: "this shows how JSON.parse fails misarable because they decided to return JSONObject.Null en vez de null"
        List nullList = JSON.parse("null")

        then: "Fails because JSONObject.Null cannot be casted to anything but Object"
        thrown(GroovyCastException)

        when: "This shows a way to fix it"
        nullList = JSONUtils.validateAndParseJSON(nullableElement)

        then:
        noExceptionThrown()
        nullList == null


        where:
        nullableElement << ["", " ", "null", " null "]
    }

    def "drop nulls"() {
        when:
        Map object = JSONUtils.validateAndParseJSON("{'doesNotExist':null, 'list':[{'doesNotExist':null}, null]}", true)

        then:
        noExceptionThrown()
        object.size() == 1
        !object.containsKey('doesNotExist')
        object.list.size() == 2
        object.list[0] == [:]
        object.list[1] == null

    }
}
