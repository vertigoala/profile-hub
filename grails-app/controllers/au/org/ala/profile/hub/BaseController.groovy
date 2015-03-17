package au.org.ala.profile.hub

import grails.converters.JSON

import static org.apache.http.HttpStatus.*

class BaseController {

    public static final String CONTEXT_TYPE_JSON = "application/json"

    def notFound = {String message = null ->
        sendError(SC_NOT_FOUND, message ?: "")
    }

    def badRequest = {String message = null ->
        sendError(SC_BAD_REQUEST, message ?: "")
    }

    def success = { resp ->
        response.status = SC_OK
        response.setContentType(CONTEXT_TYPE_JSON)
        render resp as JSON
    }

    def saveFailed = {
        sendError(SC_INTERNAL_SERVER_ERROR)
    }

    def sendError = {int status, String msg = null ->
        response.status = status
        response.sendError(status, msg)
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
}
