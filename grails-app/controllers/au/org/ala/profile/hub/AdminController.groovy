package au.org.ala.profile.hub

import au.org.ala.profile.security.Role
import au.org.ala.profile.security.Secured
import au.org.ala.ws.service.WebService
import grails.converters.JSON
import grails.util.Environment
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

import javax.validation.constraints.NotNull

class AdminController extends BaseController {

    WebService webService
    ProfileService profileService

    @Secured(role = Role.ROLE_ADMIN, opusSpecific = false)
    def index() {
        render view: "admin.gsp"
    }

    @Secured(role = Role.ROLE_ADMIN, opusSpecific = false)
    def alaIndex() {
        render view: "alaAdmin.gsp"
    }

    @Secured(role = Role.ROLE_ADMIN, opusSpecific = false)
    def reindex() {
        def response = webService.post("${grailsApplication.config.profile.service.url}/admin/search/reindex", [:])

        handle response
    }

    @Secured(role = Role.ROLE_ADMIN, opusSpecific = false)
    def rematchNames() {
        def response = webService.post("${grailsApplication.config.profile.service.url}/admin/rematchNames", [opusIds: request.getJSON()?.opusIds?.split(",")])

        handle response
    }

    @Secured(role = Role.ROLE_ADMIN, opusSpecific = false)
    def listPendingJobs() {
        def response = webService.get("${grailsApplication.config.profile.service.url}/job")

        handle response
    }

    @Secured(role = Role.ROLE_ADMIN, opusSpecific = false)
    def deleteJob(@NotNull String jobType, @NotNull String jobId) {
        def response = webService.delete("${grailsApplication.config.profile.service.url}/job/${jobType}/${jobId}")

        handle response
    }
}
