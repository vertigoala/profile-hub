package au.org.ala.profile.hub

import grails.converters.JSON

import static org.apache.http.HttpStatus.*

class BaseController {

    public static final String CONTEXT_TYPE_JSON = "application/json"

    def notFound() {
        sendError(SC_NOT_FOUND)
    }

    def badRequest() {
        sendError(SC_BAD_REQUEST)
    }

    def handle (resp) {
        if (resp.statusCode != SC_OK) {
            response.status = resp.statusCode
            sendError(resp.statusCode, resp.error ?: "")
        } else {
            response.setContentType(CONTEXT_TYPE_JSON)
            render resp.resp as JSON
        }
    }

    def sendError = {int status, String msg = null ->
        response.status = status
        response.sendError(status, msg)
    }

}
