package au.org.ala.profile.hub

import au.org.ala.web.AuthService

class UserService {
    def grailsApplication
    AuthService authService
    WebService webService

    def getCurrentUserDisplayName() {
        getUser()?.displayName ?: "" //?:"mark.woolston@csiro.au"
    }

    def getCurrentUserId() {
        getUser()?.userId ?: ""
    }

    public UserDetails getUser() {
        def u = authService.userDetails()
        def user

        if (u?.userId) {
            user = new UserDetails()
            user.displayName = u.userDisplayName
            user.userId = u.userId
            user.userName = u.email
        }

        return user
    }

    def findUser(String username) {
        webService.doPost("${grailsApplication.config.userdetails.service.url}/userDetails/getUserDetails?userName=${username?.encodeAsURL()}", [:])
    }

    def userInRole(role) {
        authService.userInRole(role)
    }

    def userIsSiteAdmin() {
        (authService.userInRole(grailsApplication.config.security.cas.officerRole)
                || authService.userInRole(grailsApplication.config.security.cas.adminRole)
                || authService.userInRole(grailsApplication.config.security.cas.alaAdminRole))
    }

    def checkEmailExists(String email) {
        def url = "${grailsApplication.config.userdetails.service.url}/userdetails/userDetails/getUserDetails?userName=${email}"
        def resp = webService.doPost(url.toString(), [:])
        return resp?.resp?.userId ?: ""
    }
}