package au.org.ala.profile.hub

class UserController extends BaseController {

    UserService userService

    def findUser() {
        if (!params.userName) {
            badRequest "userName is a required parameter"
        } else {
            log.debug "Searching for user ${params.userName}....."

            def response = userService.findUser(params.userName)

            handle response
        }
    }
}
