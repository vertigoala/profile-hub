package au.org.ala.profile.hub

import net.sf.json.JSON

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
        }

        def audit = profileService.getAuditHistory(objectId, userId)

        render audit as JSON
    }
}
