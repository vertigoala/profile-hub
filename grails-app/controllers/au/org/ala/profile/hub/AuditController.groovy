package au.org.ala.profile.hub

class AuditController extends BaseController {

    static Integer DEFAULT_PAGE_SIZE = 100
    static Integer DEFAULT_OFFSET = 0

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
            Integer offset = params.getInt('offset', DEFAULT_OFFSET)
            Integer max = params.getInt('max', DEFAULT_PAGE_SIZE)
            def response = profileService.getAuditHistory(objectId, userId, offset, max)

            handle response
        }
    }
}
