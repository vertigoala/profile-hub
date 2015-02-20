package au.org.ala.profile.hub

import static org.apache.http.HttpStatus.*

class BaseController {

    def notFound() {
        sendError(SC_NOT_FOUND)
    }

    def badRequest() {
        sendError(SC_BAD_REQUEST)
    }

    def sendError = {int status, String msg = null ->
        response.status = status
        response.sendError(status, msg)
    }

}
