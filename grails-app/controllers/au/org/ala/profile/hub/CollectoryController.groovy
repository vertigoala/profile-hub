package au.org.ala.profile.hub

import grails.converters.JSON

import static org.apache.http.HttpStatus.SC_OK

class CollectoryController extends BaseController {

    CollectoryService collectoryService

    def getResource() {
        if (!params.dataResourceUid) {
            badRequest();
        } else {
            def response = collectoryService.getDataResource(params.dataResourceUid)

            handle response
        }
    }

    def list() {
        response.setContentType(CONTEXT_TYPE_JSON)
        def resp = collectoryService.getDataResources()
        render resp as JSON
    }
}
