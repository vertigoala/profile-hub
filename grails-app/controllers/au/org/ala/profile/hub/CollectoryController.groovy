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
    def getHub() {
        if (!params.dataHubUid) {
            badRequest();
        } else {
            def response = collectoryService.getDataHub(params.dataHubUid)

            handle response
        }
    }

    def listHubs() {
        response.setContentType(CONTENT_TYPE_JSON)
        def resp = collectoryService.getDataHubs()
        render resp as JSON
    }

    def listResources() {
        response.setContentType(CONTENT_TYPE_JSON)
        def resp = collectoryService.getDataResources()
        render resp as JSON
    }

    def licences() {
        handle collectoryService.getLicences()
    }
}
