package au.org.ala.profile.filter

import au.org.ala.profile.hub.ProfileService
import au.org.ala.profile.security.Role
import au.org.ala.profile.security.Secured
import au.org.ala.web.AuthService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.mixin.web.FiltersUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

import java.security.Principal

@TestFor(AccessControlFilters)
@TestMixin([GrailsUnitTestMixin, FiltersUnitTestMixin])
@Unroll
@Mock([AuthService, ProfileService])
class AccessControlFiltersSpec extends Specification {

    def controller = new SecuredController()

    void "ALA Administrators are allowed to do everything"() {
        setup:
        // need to do this because grailsApplication.controllerClasses is empty in the filter when run from the unit test
        // unless we manually add the dummy controller class used in this test
        grailsApplication.addArtefact("Controller", SecuredController)

        defineBeans {
            authService(MockAuthService)
            profileService(MockProfileService)
        }

        when:
        params.opusId = "12345"
        request.userPrincipal = new User([authority: "ROLE_ADMIN"])

        withFilters(controller: "secured", action: action) {

            controller."${action}"()
        }

        then:
        response.status == responseCode
        params.isALAAdmin == true
        params.isOpusAdmin == true
        params.isOpusEditor == true

        where:
        action                           | responseCode
        "publicAction"                   | 200
        "alaAdminAction"                 | 200
        "profileAdminAction"             | 200
        "profileEditorAction"            | 200
    }

    void "public users are only allowed to access unsecured actions"() {
        setup:
        // need to do this because grailsApplication.controllerClasses is empty in the filter when run from the unit test
        // unless we manually add the dummy controller class used in this test
        grailsApplication.addArtefact("Controller", SecuredController)

        defineBeans {
            authService(MockAuthService)
            profileService(MockProfileService)
        }

        when:
        params.opusId = "12345"
        request.userPrincipal = new User([authority: ""])

        withFilters(controller: "secured", action: action) {

            controller."${action}"()
        }

        then:
        response.status == responseCode
        params.isALAAdmin == false
        params.isOpusAdmin == false
        params.isOpusEditor == false

        where:
        action                           | responseCode
        "publicAction"                   | 200
        "alaAdminAction"                 | 403
        "profileAdminAction"             | 403
        "profileEditorAction"            | 403
    }

    void "profile admins can access public and opus-specific admin and edit actions"() {
        setup:
        // need to do this because grailsApplication.controllerClasses is empty in the filter when run from the unit test
        // unless we manually add the dummy controller class used in this test
        grailsApplication.addArtefact("Controller", SecuredController)

        defineBeans {
            authService(MockAuthService)
            profileService(MockProfileService)
        }

        when:
        params.opusId = "admin1"
        request.userPrincipal = new User([authority: "", userid: "PROFILE_ADMIN_USER"])

        withFilters(controller: "secured", action: action) {

            controller."${action}"()
        }

        then:
        response.status == responseCode
        params.isALAAdmin == false
        params.isOpusAdmin == true
        params.isOpusEditor == true

        where:
        action                           | responseCode
        "publicAction"                   | 200
        "alaAdminAction"                 | 403
        "profileAdminAction"             | 200
        "profileEditorAction"            | 200

    }

    void "profile editors can access public and opus-specific edit actions"() {
        setup:
        // need to do this because grailsApplication.controllerClasses is empty in the filter when run from the unit test
        // unless we manually add the dummy controller class used in this test
        grailsApplication.addArtefact("Controller", SecuredController)

        defineBeans {
            authService(MockAuthService)
            profileService(MockProfileService)
        }

        when:
        params.opusId = "editor1"
        request.userPrincipal = new User([authority: "", userid: "PROFILE_EDITOR_USER"])

        withFilters(controller: "secured", action: action) {

            controller."${action}"()
        }

        then:
        response.status == responseCode
        params.isALAAdmin == false
        params.isOpusAdmin == false
        params.isOpusEditor == true

        where:
        action                           | responseCode
        "publicAction"                   | 200
        "alaAdminAction"                 | 403
        "profileAdminAction"             | 403
        "profileEditorAction"            | 200
    }
}

class User implements Principal {

    Map attributes

    User(Map attributes) {
        this.attributes = attributes
    }

    @Override
    String getName() {
        return "Fred"
    }

    Map getAttributes() {
        return attributes
    }
}

class SecuredController {
    def publicAction() {

    }

    @Secured(role = Role.ROLE_ADMIN)
    def alaAdminAction() {

    }

    @Secured(role = Role.ROLE_PROFILE_ADMIN)
    def profileAdminAction() {

    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def profileEditorAction() {

    }
}

class MockAuthService extends AuthService {
    @Override
    def getUserId() {
        666
    }

    @Override
    def getDisplayName() {
        "Fred Bloggs"
    }
}

class MockProfileService extends ProfileService {
    @Override
    def getOpus(String opusId) {
        def opus
        if (opusId == "admin1") {
            opus = [uuid: opusId, admins: [[userId: "PROFILE_ADMIN_USER"], [userId: 2]], editors: [[userId: 3], [userId: 4]]]
        } else if (opusId == "editor1") {
            opus = [uuid: opusId, admins: [[userId: 1], [userId: 2]], editors: [[userId: 3], [userId: "PROFILE_EDITOR_USER"]]]
        } else {
            opus = [uuid: opusId, admins: [[userId: 1], [userId: 2]], editors: [[userId: 3], [userId: 4]]]
        }
        return opus
    }
}