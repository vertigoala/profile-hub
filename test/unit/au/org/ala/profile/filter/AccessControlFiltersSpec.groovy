package au.org.ala.profile.filter

import au.org.ala.profile.hub.AuditController
import au.org.ala.profile.hub.AuditControllerSpec
import au.org.ala.profile.hub.CommentController
import au.org.ala.profile.hub.ExportService
import au.org.ala.profile.hub.FlorulaCookieService
import au.org.ala.profile.hub.MapService
import au.org.ala.profile.hub.OpusController
import au.org.ala.profile.hub.ProfileController
import au.org.ala.profile.hub.ProfileService
import au.org.ala.profile.security.PrivateCollectionSecurityExempt
import au.org.ala.profile.security.Role
import au.org.ala.profile.security.Secured
import au.org.ala.web.AuthService
import au.org.ala.ws.service.WebService
import grails.converters.JSON
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.mixin.web.FiltersUnitTestMixin
import org.codehaus.groovy.grails.commons.InstanceFactoryBean
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.annotation.Inherited
import java.security.Principal

import static au.org.ala.profile.hub.util.HubConstants.getDEFAULT_OPUS_BANNER_URL
import static au.org.ala.profile.hub.util.HubConstants.getDEFAULT_OPUS_LOGOS
import static au.org.ala.profile.hub.util.HubConstants.getDEFAULT_OPUS_TITLE

@TestFor(AccessControlFilters)
@TestMixin([GrailsUnitTestMixin, FiltersUnitTestMixin])
@Unroll
@Mock([AuthService, ProfileService])
class AccessControlFiltersSpec extends Specification {

    def controller = new SecuredController()
    Map users

    // the following is required to get defineBeans to work in grails 2.5.2
    def doWithSpring = {
        authService au.org.ala.web.AuthService
        profileService au.org.ala.profile.hub.ProfileService
    }

    def setup() {
        grailsApplication.config.security.authorisation.disable = false
        users = [
                'NOT_LOGGED_IN_USER' : null,
                'LOGGED_IN_USER': new User([authority: "", userid: "9876"]),
                'REVIEWER': new User([authority: "", userid: "PROFILE_REVIEWER_USER"]),
                'EDITOR': new User([authority: "", userid: "PROFILE_EDITOR_USER"]),
                'AUTHOR': new User([authority: "", userid: "PROFILE_AUTHOR_USER"]),
                'ADMIN': new User([authority: "", userid: "PROFILE_ADMIN_USER"]),
                'ALA_ADMIN': new User([authority: "ROLE_ADMIN"])
        ]
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
        params.isOpusAuthor == true
        params.isOpusReviewer == true

        where:
        action                  | responseCode
        "publicAction"          | 200
        "alaAdminAction"        | 200
        "profileAdminAction"    | 200
        "profileEditorAction"   | 200
        "profileAuthorAction"   | 200
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
        params.isOpusAuthor == false
        params.isOpusReviewer == false

        where:
        action                  | responseCode
        "publicAction"          | 200
        "alaAdminAction"        | 403
        "profileAdminAction"    | 403
        "profileEditorAction"   | 403
        "profileAuthorAction"   | 403
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
        params.isOpusAuthor == true
        params.isOpusReviewer == true

        where:
        action                  | responseCode
        "publicAction"          | 200
        "alaAdminAction"        | 403
        "profileAdminAction"    | 200
        "profileEditorAction"   | 200
        "profileAuthorAction"   | 200
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
        params.isOpusAuthor == false
        params.isOpusReviewer == true

        where:
        action                  | responseCode
        "publicAction"          | 200
        "alaAdminAction"        | 403
        "profileAdminAction"    | 403
        "profileEditorAction"   | 200
        "profileAuthorAction"   | 403
        "profileReviewerAction" | 200
    }

    void "profile authors can access public and opus-specific author actions"() {
        setup:
        // need to do this because grailsApplication.controllerClasses is empty in the filter when run from the unit test
        // unless we manually add the dummy controller class used in this test
        grailsApplication.addArtefact("Controller", SecuredController)

        defineBeans {
            authService(MockAuthService)
            profileService(MockProfileService)
        }

        when:
        params.opusId = "author1"
        request.userPrincipal = new User([authority: "", userid: "PROFILE_AUTHOR_USER"])

        withFilters(controller: "secured", action: action) {

            controller."${action}"()
        }

        then:
        response.status == responseCode
        params.isALAAdmin == false
        params.isOpusAdmin == false
        params.isOpusEditor == false
        params.isOpusAuthor == true
        params.isOpusReviewer == true

        where:
        action                  | responseCode
        "publicAction"          | 200
        "alaAdminAction"        | 403
        "profileAdminAction"    | 403
        "profileEditorAction"   | 200
        "profileAuthorAction"   | 200
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
        params.isOpusAuthor == false
        params.isOpusReviewer == true

        where:
        action                  | responseCode
        "publicAction"          | 200
        "alaAdminAction"        | 403
        "profileAdminAction"    | 403
        "profileEditorAction"   | 403
        "profileAuthorAction"   | 403
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

    void "methods for private collections annotated with @PrivateCollectionSecurityExempt can be accessed by anyone"() {
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

        withFilters(controller: "secured", action: "privateCollectionSecurityExemptAction") {
            controller.privateCollectionSecurityExemptAction()
        }

        then:
        response.status == 200
    }

    void "check to ensure users with correct privilege are accessing certain actions on profile controller"() {
        setup:
        // need to do this because grailsApplication.controllerClasses is empty in the filter when run from the unit test
        // unless we manually add the dummy controller class used in this test
        grailsApplication.addArtefact("Controller", ProfileController)
        ProfileService pService = Mock(ProfileService)
        ExportService eService = Mock(ExportService)
        defineBeans {
            authService(MockAuthService)
            profileService(InstanceFactoryBean, pService, ProfileService)
            exportService(InstanceFactoryBean, eService, ExportService)
            mapService(MockMapService)
        }

        def testData = getTestCollectionAndProfile()
        def opus = testData.opus
        def profile = testData.profile
        def wsResponse = [statusCode: 200, resp: [:]]
        Map methodReturnValues = [
                "getProfile" : profile,
                "createProfile": wsResponse,
                "deleteProfile": wsResponse,
                "toggleDraftMode": wsResponse,
                "archiveProfile": wsResponse,
                "savePublication": wsResponse
        ]

        // Adding this interaction to make sure controller action is not called when authorization fails.
        numberOfCallsToFunction * pService."${functionCalled}"(*_) >> methodReturnValues[functionCalled]
        pService.getOpus(*_) >> opus
        pService.getProfile(*_) >> profile
        pService.getCitation(*_) >> ""
        pService.hasMatchedName(*_) >> true
        pService.createProfile(*_) >> wsResponse
        pService.archiveProfile(*_) >> wsResponse
        pService.toggleDraftMode(*_) >> wsResponse
        pService.savePublication(*_) >> wsResponse
        eService.createPdf(*_) >> null

        controller = new ProfileController()
        controller.profileService = pService

        when:
        log.info("Controller - profile; Action - ${action}; User - ${type};")
        params.opusId = "allusers"
        params.profileId = "test"
        request.userPrincipal = users[type]
        request.json = profile as JSON

        withFilters(controller: "profile", action: action) {
            controller."${action}"()
        }

        then:
        response.status == responseCode

        where:
        action                  | type                        | responseCode | functionCalled  | numberOfCallsToFunction
        // view profile
        "show"                  | "NOT_LOGGED_IN_USER"        | 200          | "getProfile"    | 1
        "show"                  | "LOGGED_IN_USER"            | 200          | "getProfile"    | 1
        "show"                  | "REVIEWER"                  | 200          | "getProfile"    | 1
        "show"                  | "EDITOR"                    | 200          | "getProfile"    | 1
        "show"                  | "AUTHOR"                    | 200          | "getProfile"    | 1
        "show"                  | "ADMIN"                     | 200          | "getProfile"    | 1
        "show"                  | "ALA_ADMIN"                 | 200          | "getProfile"    | 1
        // edit profile
        "edit"                  | "NOT_LOGGED_IN_USER"        | 403          | "getProfile"    | 0
        "edit"                  | "LOGGED_IN_USER"            | 403          | "getProfile"    | 0
        "edit"                  | "REVIEWER"                  | 403          | "getProfile"    | 0
        "edit"                  | "EDITOR"                    | 200          | "getProfile"    | 1
        "edit"                  | "AUTHOR"                    | 200          | "getProfile"    | 1
        "edit"                  | "ADMIN"                     | 200          | "getProfile"    | 1
        "edit"                  | "ALA_ADMIN"                 | 200          | "getProfile"    | 1
        // create profile
        "createProfile"         | "NOT_LOGGED_IN_USER"        | 403          | "createProfile" | 0
        "createProfile"         | "LOGGED_IN_USER"            | 403          | "createProfile" | 0
        "createProfile"         | "REVIEWER"                  | 403          | "createProfile" | 0
        "createProfile"         | "EDITOR"                    | 403          | "createProfile" | 0
        "createProfile"         | "AUTHOR"                    | 200          | "createProfile" | 1
        "createProfile"         | "ADMIN"                     | 200          | "createProfile" | 1
        "createProfile"         | "ALA_ADMIN"                 | 200          | "createProfile" | 1
        // delete profile
        "deleteProfile"         | "NOT_LOGGED_IN_USER"        | 403          | "deleteProfile" | 0
        "deleteProfile"         | "LOGGED_IN_USER"            | 403          | "deleteProfile" | 0
        "deleteProfile"         | "REVIEWER"                  | 403          | "deleteProfile" | 0
        "deleteProfile"         | "EDITOR"                    | 403          | "deleteProfile" | 0
        "deleteProfile"         | "AUTHOR"                    | 403          | "deleteProfile" | 0
        "deleteProfile"         | "ADMIN"                     | 200          | "deleteProfile" | 1
        "deleteProfile"         | "ALA_ADMIN"                 | 200          | "deleteProfile" | 1
        // lock for major revision
        "createDraftMode"       | "NOT_LOGGED_IN_USER"        | 403          | "toggleDraftMode" | 0
        "createDraftMode"       | "LOGGED_IN_USER"            | 403          | "toggleDraftMode" | 0
        "createDraftMode"       | "REVIEWER"                  | 403          | "toggleDraftMode" | 0
        "createDraftMode"       | "EDITOR"                    | 200          | "toggleDraftMode" | 1
        "createDraftMode"       | "AUTHOR"                    | 200          | "toggleDraftMode" | 1
        "createDraftMode"       | "ADMIN"                     | 200          | "toggleDraftMode" | 1
        "createDraftMode"       | "ALA_ADMIN"                 | 200          | "toggleDraftMode" | 1
        // Archive profiles
        "archiveProfile"        | "NOT_LOGGED_IN_USER"        | 403          | "archiveProfile"  | 0
        "archiveProfile"        | "LOGGED_IN_USER"            | 403          | "archiveProfile"  | 0
        "archiveProfile"        | "REVIEWER"                  | 403          | "archiveProfile"  | 0
        "archiveProfile"        | "EDITOR"                    | 403          | "archiveProfile"  | 0
        "archiveProfile"        | "AUTHOR"                    | 200          | "archiveProfile"  | 1
        "archiveProfile"        | "ADMIN"                     | 200          | "archiveProfile"  | 1
        "archiveProfile"        | "ALA_ADMIN"                 | 200          | "archiveProfile"  | 1
        // publish draft profiles
        "publishDraft"          | "NOT_LOGGED_IN_USER"        | 403          | "getProfile"      | 0
        "publishDraft"          | "LOGGED_IN_USER"            | 403          | "getProfile"      | 0
        "publishDraft"          | "REVIEWER"                  | 403          | "getProfile"      | 0
        "publishDraft"          | "EDITOR"                    | 403          | "getProfile"      | 0
        "publishDraft"          | "AUTHOR"                    | 403          | "getProfile"      | 0
        "publishDraft"          | "ADMIN"                     | 200          | "getProfile"      | 1
        "publishDraft"          | "ALA_ADMIN"                 | 200          | "getProfile"      | 1
        // create profile snapshot
        "savePublication"       | "NOT_LOGGED_IN_USER"        | 403          | "savePublication" | 0
        "savePublication"       | "LOGGED_IN_USER"            | 403          | "savePublication" | 0
        "savePublication"       | "REVIEWER"                  | 403          | "savePublication" | 0
        "savePublication"       | "EDITOR"                    | 403          | "savePublication" | 0
        "savePublication"       | "AUTHOR"                    | 403          | "savePublication" | 0
        "savePublication"       | "ADMIN"                     | 200          | "savePublication" | 1
        "savePublication"       | "ALA_ADMIN"                 | 200          | "savePublication" | 1
    }

    void "check to ensure users with correct privilege can comment on a profile"() {
        setup:
        // need to do this because grailsApplication.controllerClasses is empty in the filter when run from the unit test
        // unless we manually add the dummy controller class used in this test
        grailsApplication.addArtefact("Controller", CommentController)
        ProfileService pService = Mock(ProfileService)
        defineBeans {
            authService(MockAuthService)
            profileService(InstanceFactoryBean, pService, ProfileService)
        }

        def testData = getTestCollectionAndProfile()
        def opus = testData.opus
        def profile = testData.profile
        def wsResponse = [statusCode: 200, resp: [ author: [userId: "PROFILE_REVIEWER_USER"]]]
        Map methodReturnValues = [
                "getComments": wsResponse,
                "addComment": wsResponse,
                "deleteComment": wsResponse,
                "updateComment": wsResponse
        ]

        // Adding this interaction to make sure controller action is not called when authorization fails.
        numberOfCallsToFunction * pService."${functionCalled}"(*_) >> methodReturnValues[functionCalled]
        pService.getOpus(_) >> opus
        pService.addComment(*_) >> wsResponse
        pService.getComments(*_) >> wsResponse
        pService.getComment(*_) >> wsResponse

        controller = new CommentController()
        controller.profileService = pService

        when:
        log.info("Controller - profile; Action - ${action}; User - ${type};")
        params.opusId = "allusers"
        params.profileId = "test"
        params.commentId = "commentId"
        request.userPrincipal = users[type]
        request.json = profile as JSON

        withFilters(controller: "comment", action: action) {
            controller."${action}"()
        }

        then:
        response.status == responseCode

        where:
        action                  | type                        | responseCode | functionCalled  | numberOfCallsToFunction
        // create comment
        "addComment"            | "NOT_LOGGED_IN_USER"        | 403          | "addComment"    | 0
        "addComment"            | "LOGGED_IN_USER"            | 403          | "addComment"    | 0
        "addComment"            | "REVIEWER"                  | 200          | "addComment"    | 1
        "addComment"            | "EDITOR"                    | 200          | "addComment"    | 1
        "addComment"            | "AUTHOR"                    | 200          | "addComment"    | 1
        "addComment"            | "ADMIN"                     | 200          | "addComment"    | 1
        "addComment"            | "ALA_ADMIN"                 | 200          | "addComment"    | 1
        // view comments
        "getComments"           | "NOT_LOGGED_IN_USER"        | 403          | "getComments"   | 0
        "getComments"           | "LOGGED_IN_USER"            | 403          | "getComments"   | 0
        "getComments"           | "REVIEWER"                  | 200          | "getComments"   | 1
        "getComments"           | "EDITOR"                    | 200          | "getComments"   | 1
        "getComments"           | "AUTHOR"                    | 200          | "getComments"   | 1
        "getComments"           | "ADMIN"                     | 200          | "getComments"   | 1
        "getComments"           | "ALA_ADMIN"                 | 200          | "getComments"   | 1
        // update comment
        "updateComment"         | "NOT_LOGGED_IN_USER"        | 403          | "updateComment"    | 0
        "updateComment"         | "LOGGED_IN_USER"            | 403          | "updateComment"    | 0
        "updateComment"         | "REVIEWER"                  | 200          | "updateComment"    | 1
        "updateComment"         | "EDITOR"                    | 200          | "updateComment"    | 1
        "updateComment"         | "AUTHOR"                    | 200          | "updateComment"    | 1
        "updateComment"         | "ADMIN"                     | 200          | "updateComment"    | 1
        "updateComment"         | "ALA_ADMIN"                 | 200          | "updateComment"    | 1
        // delete comment
        "deleteComment"         | "NOT_LOGGED_IN_USER"        | 403          | "deleteComment" | 0
        "deleteComment"         | "LOGGED_IN_USER"            | 403          | "deleteComment" | 0
        "deleteComment"         | "REVIEWER"                  | 403          | "deleteComment" | 0
        "deleteComment"         | "EDITOR"                    | 403          | "deleteComment" | 0
        "deleteComment"         | "AUTHOR"                    | 403          | "deleteComment" | 0
        "deleteComment"         | "ADMIN"                     | 200          | "deleteComment" | 1
        "deleteComment"         | "ALA_ADMIN"                 | 200          | "deleteComment" | 1

    }

    void "check to ensure user with correct privilege have access to restricted actions on opus controller"() {
        setup:
        // need to do this because grailsApplication.controllerClasses is empty in the filter when run from the unit test
        // unless we manually add the dummy controller class used in this test
        grailsApplication.addArtefact("Controller", OpusController)
        ProfileService pService = Mock(ProfileService)
        FlorulaCookieService fCookieService = Mock(FlorulaCookieService)
        WebService wService = Mock(WebService)
        defineBeans {
            authService(MockAuthService)
            profileService(InstanceFactoryBean, pService, ProfileService)
            florulaCookieService(InstanceFactoryBean, fCookieService, FlorulaCookieService)
            webService(InstanceFactoryBean, wService, WebService)
        }

        def testData = getTestCollectionAndProfile()
        def opus = testData.opus
        def profile = testData.profile
        def wsResponse = [statusCode: 200, resp: [:]]
        Map methodReturnValues = [
                "getOpus" : opus,
                "updateFlorulaList": null,
                "createOpus": wsResponse,
                "deleteOpus": wsResponse
        ]

        // Adding this interaction to make sure controller action is not called when authorization fails.
        numberOfCallsToFunction * pService."${functionCalled}"(*_) >> methodReturnValues[functionCalled]
        pService.getOpus(*_) >> opus
        pService.getProfile(*_) >> profile
        pService.deleteOpus(*_) >> wsResponse
        fCookieService.getFlorulaListIdForOpusId(*_) >> null
        wService.get(*_) >> wsResponse

        controller = new OpusController()
        controller.profileService = pService

        when:
        log.info("Controller - opus; Action - ${action}; User - ${type};")
        params.opusId = "allusers"
        params.profileId = "test"
        request.userPrincipal = users[type]
        request.json = profile as JSON

        withFilters(controller: "opus", action: action) {
            controller."${action}"()
        }

        then:
        response.status == responseCode

        where:
        action                    | type                        | responseCode | functionCalled | numberOfCallsToFunction
        // view opus home page
        "show"                    | "NOT_LOGGED_IN_USER"        | 200          | "getOpus"      | 2
        "show"                    | "LOGGED_IN_USER"            | 200          | "getOpus"      | 2
        "show"                    | "REVIEWER"                  | 200          | "getOpus"      | 2
        "show"                    | "EDITOR"                    | 200          | "getOpus"      | 2
        "show"                    | "AUTHOR"                    | 200          | "getOpus"      | 2
        "show"                    | "ADMIN"                     | 200          | "getOpus"      | 2
        "show"                    | "ALA_ADMIN"                 | 200          | "getOpus"      | 1
        // view search page
        "search"                  | "NOT_LOGGED_IN_USER"        | 200          | "getOpus"      | 2
        "search"                  | "LOGGED_IN_USER"            | 200          | "getOpus"      | 2
        "search"                  | "REVIEWER"                  | 200          | "getOpus"      | 2
        "search"                  | "EDITOR"                    | 200          | "getOpus"      | 2
        "search"                  | "AUTHOR"                    | 200          | "getOpus"      | 2
        "search"                  | "ADMIN"                     | 200          | "getOpus"      | 2
        "search"                  | "ALA_ADMIN"                 | 200          | "getOpus"      | 1
        // view browse page
        "browse"                  | "NOT_LOGGED_IN_USER"        | 200          | "getOpus"      | 2
        "browse"                  | "LOGGED_IN_USER"            | 200          | "getOpus"      | 2
        "browse"                  | "REVIEWER"                  | 200          | "getOpus"      | 2
        "browse"                  | "EDITOR"                    | 200          | "getOpus"      | 2
        "browse"                  | "AUTHOR"                    | 200          | "getOpus"      | 2
        "browse"                  | "ADMIN"                     | 200          | "getOpus"      | 2
        "browse"                  | "ALA_ADMIN"                 | 200          | "getOpus"      | 1
        // view filter page
        "filter"                  | "NOT_LOGGED_IN_USER"        | 200          | "getOpus"      | 2
        "filter"                  | "LOGGED_IN_USER"            | 200          | "getOpus"      | 2
        "filter"                  | "REVIEWER"                  | 200          | "getOpus"      | 2
        "filter"                  | "EDITOR"                    | 200          | "getOpus"      | 2
        "filter"                  | "AUTHOR"                    | 200          | "getOpus"      | 2
        "filter"                  | "ADMIN"                     | 200          | "getOpus"      | 2
        "filter"                  | "ALA_ADMIN"                 | 200          | "getOpus"      | 1
        // edit opus configuration
        "edit"                    | "NOT_LOGGED_IN_USER"        | 403          | "getOpus"      | 1
        "edit"                    | "LOGGED_IN_USER"            | 403          | "getOpus"      | 1
        "edit"                    | "REVIEWER"                  | 403          | "getOpus"      | 1
        "edit"                    | "EDITOR"                    | 403          | "getOpus"      | 1
        "edit"                    | "AUTHOR"                    | 403          | "getOpus"      | 1
        "edit"                    | "ADMIN"                     | 200          | "getOpus"      | 2
        "edit"                    | "ALA_ADMIN"                 | 200          | "getOpus"      | 1
        // view opus reports
        "reports"                 | "NOT_LOGGED_IN_USER"        | 403          | "getOpus"      | 1
        "reports"                 | "LOGGED_IN_USER"            | 403          | "getOpus"      | 1
        "reports"                 | "REVIEWER"                  | 403          | "getOpus"      | 1
        "reports"                 | "EDITOR"                    | 403          | "getOpus"      | 1
        "reports"                 | "AUTHOR"                    | 403          | "getOpus"      | 1
        "reports"                 | "ADMIN"                     | 200          | "getOpus"      | 2
        "reports"                 | "ALA_ADMIN"                 | 200          | "getOpus"      | 1
        // set filter on a page
        "updateFlorulaList"       | "NOT_LOGGED_IN_USER"        | 204          | "updateFlorulaList" | 1
        "updateFlorulaList"       | "LOGGED_IN_USER"            | 204          | "updateFlorulaList" | 1
        "updateFlorulaList"       | "REVIEWER"                  | 204          | "updateFlorulaList" | 1
        "updateFlorulaList"       | "EDITOR"                    | 204          | "updateFlorulaList" | 1
        "updateFlorulaList"       | "AUTHOR"                    | 204          | "updateFlorulaList" | 1
        "updateFlorulaList"       | "ADMIN"                     | 204          | "updateFlorulaList" | 1
        "updateFlorulaList"       | "ALA_ADMIN"                 | 204          | "updateFlorulaList" | 1
        // create opus
        "createOpus"              | "NOT_LOGGED_IN_USER"        | 403          | "createOpus"        | 0
        "createOpus"              | "LOGGED_IN_USER"            | 403          | "createOpus"        | 0
        "createOpus"              | "REVIEWER"                  | 403          | "createOpus"        | 0
        "createOpus"              | "EDITOR"                    | 403          | "createOpus"        | 0
        "createOpus"              | "AUTHOR"                    | 403          | "createOpus"        | 0
        "createOpus"              | "ADMIN"                     | 403          | "createOpus"        | 0
        "createOpus"              | "ALA_ADMIN"                 | 200          | "createOpus"        | 1
        // delete opus
        "deleteOpus"              | "NOT_LOGGED_IN_USER"        | 403          | "deleteOpus"        | 0
        "deleteOpus"              | "LOGGED_IN_USER"            | 403          | "deleteOpus"        | 0
        "deleteOpus"              | "REVIEWER"                  | 403          | "deleteOpus"        | 0
        "deleteOpus"              | "EDITOR"                    | 403          | "deleteOpus"        | 0
        "deleteOpus"              | "AUTHOR"                    | 403          | "deleteOpus"        | 0
        "deleteOpus"              | "ADMIN"                     | 403          | "deleteOpus"        | 0
        "deleteOpus"              | "ALA_ADMIN"                 | 200          | "deleteOpus"        | 1

    }

    def getTestCollectionAndProfile() {
        def opusId = "allusers"
        def profileName = "test"
        def opus = [uuid: opusId, authorities: [[userId: "PROFILE_ADMIN_USER", role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: "PROFILE_EDITOR_USER", role: Role.ROLE_PROFILE_EDITOR.toString()],
                                                [userId: "PROFILE_AUTHOR_USER", role: Role.ROLE_PROFILE_AUTHOR.toString()],
                                                [userId: "PROFILE_REVIEWER_USER", role: Role.ROLE_PROFILE_REVIEWER.toString()]]]
        def profile = [
                opus         : opus,
                profile      : [ name: profileName],
                logos        : opus.brandingConfig?.logos ?: DEFAULT_OPUS_LOGOS,
                bannerUrl    : opus.brandingConfig?.profileBannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                pageTitle    : opus.title ?: DEFAULT_OPUS_TITLE,
                archiveComment : true,
                profile: [ privateMode: false]
        ]

        [
                opus: opus,
                profile: profile,
                opusId: opusId,
                profileName: profileName
        ]
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

    @Secured(role = Role.ROLE_PROFILE_AUTHOR)
    def profileAuthorAction() {

    }

    @Secured(role = Role.ROLE_PROFILE_REVIEWER)
    def profileReviewerAction() {

    }

    @PrivateCollectionSecurityExempt
    def privateCollectionSecurityExemptAction() {

    }
}

class MockAuthService extends AuthService {
    @Override
    def getUserId() {
        "PROFILE_REVIEWER_USER"
    }

    @Override
    def getDisplayName() {
        "Fred Bloggs"
    }
}

class MockMapService extends MapService {
    @Override
    String getSnapshotImageUrlWithUUIDs(String contextPath, String opusUUID, String profileUUID) {
        ""
    }
}

class MockProfileService extends ProfileService {
    @Override
    def getOpus(String opusId) {
        def opus
        if (opusId == "admin1") {
            opus = [uuid: opusId, authorities: [[userId: "PROFILE_ADMIN_USER", role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 2, role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 3, role: Role.ROLE_PROFILE_AUTHOR.toString()],
                                                [userId: 4, role: Role.ROLE_PROFILE_AUTHOR.toString()]]]
        } else if (opusId == "editor1") {
            opus = [uuid: opusId, authorities: [[userId: 1, role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 2, role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 3, role: Role.ROLE_PROFILE_AUTHOR.toString()],
                                                [userId: "PROFILE_EDITOR_USER", role: Role.ROLE_PROFILE_EDITOR.toString()]]]
        } else if (opusId == "author1") {
            opus = [uuid: opusId, authorities: [[userId: 1, role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 2, role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 3, role: Role.ROLE_PROFILE_AUTHOR.toString()],
                                                [userId: "PROFILE_AUTHOR_USER", role: Role.ROLE_PROFILE_AUTHOR.toString()]]]
        } else if (opusId == "reviewer1") {
            opus = [uuid: opusId, authorities: [[userId: 1, role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 2, role: Role.ROLE_PROFILE_ADMIN.toString()],
                                                [userId: 3, role: Role.ROLE_PROFILE_AUTHOR.toString()],
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