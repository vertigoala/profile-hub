package au.org.ala.profile.hub

import au.org.ala.profile.security.Role
import au.org.ala.profile.security.Secured
import grails.converters.JSON

class AdminController extends BaseController {

    @Secured(role = Role.ROLE_ADMIN)
    def index() {
        render view: "admin.gsp"
    }

    @Secured(role = Role.ROLE_ADMIN)
    def postMessage() {
        if (request.getJSON().message) {
            servletContext.setAttribute("alaAdminMessage", request.getJSON().message)
            servletContext.setAttribute("alaAdminMessageTimestamp", new Date().format("dd/MM/yyyy HH:mm"))
        } else {
            servletContext.removeAttribute("alaAdminMessage")
            servletContext.removeAttribute("alaAdminMessageTimestamp")
        }
    }

    def getMessage() {
        render ([message: servletContext.getAttribute("alaAdminMessage"),
        timestamp: servletContext.getAttribute("alaAdminMessageTimestamp")] as JSON)
    }
}
