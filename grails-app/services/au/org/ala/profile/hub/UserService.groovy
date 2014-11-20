package au.org.ala.profile.hub

import au.org.ala.web.UserDetails

class UserService {
    def grailsApplication, authService, webService

    def getCurrentUserDisplayName() {
        getUser()?.displayName?:"" //?:"mark.woolston@csiro.au"
    }

    def getCurrentUserId() {
        getUser()?.userId?:""
    }

    public UserDetails getUser() {
        def u = authService.userDetails()
        def user

        if (u?.userId) {
            user = new UserDetails(u.userDisplayName, u.email, u.userId)
        }

        return null
    }

    def userInRole(role) {
        authService.userInRole(role)
    }

    def userIsSiteAdmin() {
        authService.userInRole(grailsApplication.config.security.cas.officerRole) || authService.userInRole(grailsApplication.config.security.cas.adminRole) || authService.userInRole(grailsApplication.config.security.cas.alaAdminRole)
    }

    def checkEmailExists(String email) {
        def url = "http://auth.ala.org.au/userdetails/userDetails/getUserDetails?userName=${email}"
        def resp = webService.doPost(url.toString(), [:])
        return resp?.resp?.userId?:""
    }
}