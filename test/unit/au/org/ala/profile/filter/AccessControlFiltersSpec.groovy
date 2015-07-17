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

    def setup() {
        grailsApplication.config.security.authorisation.disable = false
    }

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
        params.isOpusReviewer == true

        where:
        action                  | responseCode
        "publicAction"          | 200
        "alaAdminAction"        | 200
        "profileAdminAction"    | 200
        "profileEditorAction"   | 200
        "profileReviewerAction" | 200
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
        params.isOpusReviewer == false

        where:
        action                  | responseCode
        "publicAction"          | 200
        "alaAdminAction"        | 403
        "profileAdminAction"    | 403
        "profileEditorAction"   | 403
        "profileReviewerAction" | 403
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
        params.isOpusReviewer == true

        where:
        action                  | responseCode
        "publicAction"          | 200
        "alaAdminAction"        | 403
        "profileAdminAction"    | 200
        "profileEditorAction"   | 200
        "profileReviewerAction" | 200

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
        params.isOpusReviewer == true

        where:
        action                  | responseCode
        "publicAction"          | 200
        "alaAdminAction"        | 403
        "profileAdminAction"    | 403
        "profileEditorAction"   | 200
        "profileReviewerAction" | 200
    }

    void "profile reviewers can access public and opus-specific review actions"() {
        setup:
        // need to do this because grailsApplication.controllerClasses is empty in the filter when run from the unit test
        // unless we manually add the dummy controller class used in this test
        grailsApplication.addArtefact("Controller", SecuredController)

        defineBeans {
            authService(MockAuthService)
            profileService(MockProfileService)
        }

        when:
        params.opusId = "reviewer1"
        request.userPrincipal = new User([authority: "", userid: "PROFILE_REVIEWER_USER"])

        withFilters(controller: "secured", action: action) {

            controller."${action}"()
        }

        then:
        response.status == responseCode
        params.isALAAdmin == false
        params.isOpusAdmin == false
        params.isOpusEditor == false
        params.isOpusReviewer == true

        where:
        action                  | responseCode
        "publicAction"          | 200
        "alaAdminAction"        | 403
        "profileAdminAction"    | 403
        "profileEditorAction"   | 403
        "profileReviewerAction" | 200
    }

    void "users who are not logged in should not be able to see private collections"() {
        setup:
        // need to do this because grailsApplication.controllerClasses is empty in the filter when run from the unit test
        // unless we manually add the dummy controller class used in this test
        grailsApplication.addArtefact("Controller", SecuredController)

        defineBeans {
            authService(MockAuthService)
            profileService(MockProfileService)
        }

        when:
        params.opusId = "private"
        request.userPrincipal = null

        withFilters(controller: "secured", action: "publicAction") {
            controller.publicAction()
        }

        then:
        response.status == 403
    }

    void "logged in users cannot see private collections they are not registered with"() {
        setup:
        // need to do this because grailsApplication.controllerClasses is empty in the filter when run from the unit test
        // unless we manually add the dummy controller class used in this test
        grailsApplication.addArtefact("Controller", SecuredController)

        defineBeans {
            authService(MockAuthService)
            profileService(MockProfileService)
        }

        when:
        params.opusId = "private"
        request.userPrincipal = new User([authority: "", userid: "9876"])

        withFilters(controller: "secured", action: "publicAction") {
            controller.publicAction()
        }

        then:
        response.status == 403
    }

    void "logged in users can see private collections they are registered with"() {
        setup:
        // need to do this because grailsApplication.controllerClasses is empty in the filter when run from the unit test
        // unless we manually add the dummy controller class used in this test
        grailsApplication.addArtefact("Controller", SecuredController)

        defineBeans {
            authService(MockAuthService)
            profileService(MockProfileService)
        }

        when:
        params.opusId = "private"
        request.userPrincipal = new User([authority: "", userid: "1234"])

        withFilters(controller: "secured", action: "publicAction") {
            controller.publicAction()
        }

        then:
        response.status == 200
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

    @Secured(role = Role.ROLE_PROFILE_REVIEWER)
    def profileReviewerAction() {

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
            opus = [uuid: opusId, authorities: [[userId: "PROFILE_ADMIN_USER", role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 2, role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 3, role: Role.ROLE_PROFILE_EDITOR.toString()],
                                                [userId: 4, role: Role.ROLE_PROFILE_EDITOR.toString()]]]
        } else if (opusId == "editor1") {
            opus = [uuid: opusId, authorities: [[userId: 1, role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 2, role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 3, role: Role.ROLE_PROFILE_EDITOR.toString()],
                                                [userId: "PROFILE_EDITOR_USER", role: Role.ROLE_PROFILE_EDITOR.toString()]]]
        } else if (opusId == "reviewer1") {
            opus = [uuid: opusId, authorities: [[userId: 1, role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 2, role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 3, role: Role.ROLE_PROFILE_EDITOR.toString()],
                                                [userId: "PROFILE_REVIEWER_USER", role: Role.ROLE_PROFILE_REVIEWER.toString()]]]
        } else if (opusId == "private") {
            opus = [uuid: opusId, privateCollection: true, authorities: [[userId: "1234", role: Role.ROLE_USER.toString()]]]
        } else {
            opus = [uuid: opusId, authorities: [[userId: 1],
                                                [userId: 2],
                                                [userId: 3],
                                                [userId: 4]]]
        }
        return opus
    }
}