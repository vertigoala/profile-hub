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
            def response = profileService.getAuditHistory(objectId, userId)

            handle response
        }
    }
}
