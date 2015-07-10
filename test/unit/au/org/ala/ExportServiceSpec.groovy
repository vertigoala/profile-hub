package au.org.ala

import au.org.ala.profile.hub.ExportService
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ExportService)
class ExportServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test html text clean up"() {
        expect:
        ExportService.stripHtmlTextFromNonFormattingTags(html) == cleanHtml

        where:
        html | cleanHtml
        "nomenclatural synonym: <scientific><name id='200482'><scientific><name id='72513'><element><i>Mimosa</i></element></name></scientific> <element><i>paradoxa</i></element> <authors>(<base id='6840' title='Candolle, A.P. de'>DC.</base>) <author id='7060' title='Poiret, J.L.M.'>Poir.</author></authors></name></scientific>" | "nomenclatural synonym: <i>Mimosa</i> <i>paradoxa</i> (DC.) Poir."
    }
}
