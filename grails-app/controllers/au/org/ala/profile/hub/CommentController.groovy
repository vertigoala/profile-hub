package au.org.ala.profile.hub

import au.org.ala.profile.security.Role
import au.org.ala.profile.security.Secured
import au.org.ala.web.AuthService

class CommentController extends BaseController {

    AuthService authService
    ProfileService profileService

    @Secured(role = Role.ROLE_PROFILE_REVIEWER)
    def addComment() {
        def json = request.getJSON()

        if (!params.profileId || !json) {
            badRequest()
        } else {
            def response = profileService.addComment(params.opusId, params.profileId, json)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_REVIEWER)
    def updateComment() {
        def json = request.getJSON()

        if (!params.profileId || !params.commentId || !json) {
            badRequest()
        } else if (checkCommentPermissions(params.profileId, params.commentId)) {
            def response = profileService.updateComment(params.opusId, params.profileId, params.commentId, json)

            handle response
        } else {
            notAuthorised()
        }
    }

    @Secured(role = Role.ROLE_PROFILE_REVIEWER)
    def getComments() {
        if (!params.profileId) {
            badRequest()
        } else {
            def response = profileService.getComments(params.opusId, params.profileId)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_ADMIN)
    def deleteComment() {
        if (!params.commentId) {
            badRequest()
        } else {
            def response = profileService.deleteComment(params.opusId, params.profileId, params.commentId)

            handle response
        }
    }

    boolean checkCommentPermissions(String profileId, String commentId) {
        def comment = profileService.getComment(params.opusId, profileId, commentId)?.resp
        // editors and above can do anything.
        // reviewers can only edit/delete their own comments

        boolean allowed = false
        if (params.isOpusAdmin || params.isOpusEditor || params.isOpusAuthor || (params.isOpusReviewer && comment.author.userId == authService.getUserId())) {
            allowed = true
        }

        return allowed
    }
}
