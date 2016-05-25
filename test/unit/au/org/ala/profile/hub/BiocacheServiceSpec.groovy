package au.org.ala.profile.hub

import static au.org.ala.profile.hub.Utils.enc
import au.org.ala.ws.service.WebService
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import org.codehaus.groovy.grails.plugins.codecs.URLCodec

@TestFor(BiocacheService)
@TestMixin(GrailsUnitTestMixin)
class BiocacheServiceSpec extends Specification {

    BiocacheService service
    WebService webService

    def setup() {
        grailsApplication.config.biocache.occurrence.search.path = "/occurrence/search.json"
        grailsApplication.config.biocache.base.url = "http://biocache.base"
        grailsApplication.config.image.search.url = "http://biocache.base"

        webService = Mock(WebService)
        service = new BiocacheService()
        service.grailsApplication = grailsApplication
        service.webService = webService

        mockCodec(URLCodec)
    }

    def "retrieveImages() should construct the correct Biocache Occurrance Search URL"() {
        setup:
        String expectedUrl = "http://biocache.base/occurrence/search.json?q=searchId+AND+%28data_resource_uid%3Aid1+OR+data_resource_uid%3Aid2+OR+data_resource_uid%3Aid3%29&fq=multimedia:Image&format=json&im=true&pageSize=${BiocacheService.DEFAULT_BIOCACHE_PAGE_SIZE}&startIndex=0"

        when:
        service.retrieveImages("searchId", [dataResourceUid: "id1", dataResourceConfig: [imageSources: ["id2", "id3"], imageResourceOption: DataResourceOption.RESOURCES.name()]])

        then:
        1 * webService.get(expectedUrl)
    }

    def "constructQuery should return the search identifier if there is no opus"() {
        when:
        String query = service.constructQueryString("id1", null)

        then:
        query == "id1"
    }

    def "constructQuery should return the search identifier if there is no data resource config"() {
        when:
        String query = service.constructQueryString("id1", [uuid: "123"])

        then:
        query == "id1"
    }

    def "constructQuery should return the search identifier and the opus' data resource if there is no data resource config"() {
        when:
        String query = service.constructQueryString("id1", [uuid: "123", dataResourceUid: "uid1"])

        then:
        query == enc("id1 AND data_resource_uid:uid1")
    }

    def "constructQuery should include the data resources when the config option is RESOURCE"() {
        when:
        String query = service.constructQueryString("id1", [
                uuid: "123", dataResourceUid: "uid1", dataResourceConfig: [
                imageResourceOption: DataResourceOption.RESOURCES.name(),
                imageSources: ["dr1", "dr2"]
        ]])

        then:
        query == enc("id1 AND (data_resource_uid:uid1 OR data_resource_uid:dr1 OR data_resource_uid:dr2)")
    }

    def "constructQuery should include the data hubs when the config option is HUB)"() {
        when:
        String query = service.constructQueryString("id1", [
                uuid: "123", dataResourceUid: "uid1", dataResourceConfig: [
                imageResourceOption: DataResourceOption.HUBS.name(),
                imageSources: ["dh1", "dh2"]
        ]])

        then:
        query == enc("id1 AND (data_resource_uid:uid1 OR data_hub_uid:dh1 OR data_hub_uid:dh2)")
    }

    def "constructQuery should NOT include the opus' data resource when the config option is ALL)"() {
        when:
        String query = service.constructQueryString("id1", [
                uuid: "123", dataResourceUid: "uid1", dataResourceConfig: [
                imageResourceOption: DataResourceOption.ALL.name()
        ]])

        then:
        query == enc("id1")
    }

    def "constructQuery should include the opus' data resource when the config option is NONE)"() {
        when:
        String query = service.constructQueryString("id1", [
                uuid: "123", dataResourceUid: "uid1", dataResourceConfig: [
                imageResourceOption: DataResourceOption.NONE.name()
        ]])

        then:
        query == enc("id1 AND data_resource_uid:uid1")
    }
}