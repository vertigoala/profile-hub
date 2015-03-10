package au.org.ala.profile.hub

import grails.converters.JSON

import static org.apache.http.HttpStatus.SC_OK

class AuditController extends BaseController {

    ProfileService profileService

    def object() {
        getAudit(params.id, null)
    }

    def user() {
        getAudit(null, params.id)
    }

    private getAudit(String objectId, String userId) {
        if (!objectId && !userId) {
            badRequest()
        } else {
            def resp = profileService.getAuditHistory(objectId, userId)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                response.sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render resp.resp as JSON
            }
        }
    }
}
