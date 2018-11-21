package au.org.ala.profile.hub

import com.google.common.io.Resources
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ResourceController)
class ResourceControllerSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test facets"() {
        when:
        controller.facets()

        then:
        response.contentType == 'application/json'
        response.text == Resources.getResource('grouped_facets_ala.json').text

        // ensure cached facets returned
        when:
        response.reset()
        def file = File.createTempFile('profile-hub', 'test')
        file.deleteOnExit()
        file << '[]'
        controller.grailsApplication.config.facets = file.canonicalPath
        controller.facets()

        then:
        response.text == Resources.getResource('grouped_facets_ala.json').text

        // remove cache and ensure external facets can be loaded
        when:
        response.reset()
        controller.facets = null
        controller.facets()

        then:
        response.text == '[]'

    }
}
