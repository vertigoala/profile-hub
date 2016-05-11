package au.org.ala.profile.hub

import grails.converters.JSON

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
        response.setContentType(CONTENT_TYPE_JSON)
        def resp = collectoryService.getDataResources()
        render resp as JSON
    }

    def licences() {
        handle collectoryService.getLicences()
    }
}
