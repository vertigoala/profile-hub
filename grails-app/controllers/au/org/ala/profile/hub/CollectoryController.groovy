package au.org.ala.profile.hub

import grails.converters.JSON

import static org.apache.http.HttpStatus.SC_OK

class CollectoryController extends BaseController {

    CollectoryService collectoryService

    def getResource() {
        def resp
        if (!params.dataResourceUid) {
            badRequest();
        } else {
            resp = collectoryService.getDataResource(params.dataResourceUid)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                sendError(resp.statusCode, resp.error)
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render resp.resp as JSON
            }
        }
    }

    def list() {
        response.setContentType(CONTEXT_TYPE_JSON)
        def resp = collectoryService.getDataResources()
        render resp as JSON
    }
}
