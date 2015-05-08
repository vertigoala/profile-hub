package au.org.ala.profile.hub

import static au.org.ala.profile.security.Role.*
import au.org.ala.profile.security.Secured

import static au.org.ala.profile.hub.util.HubConstants.*
import grails.converters.JSON
import au.org.ala.web.AuthService

class OpusController extends BaseController {

    AuthService authService
    CollectoryService collectoryService
    UserService userService
    ProfileService profileService

    def index() {
        render view: 'index', model: [
                logoUrl  : DEFAULT_OPUS_LOGO_URL,
                bannerUrl: DEFAULT_OPUS_BANNER_URL,
                pageTitle: DEFAULT_OPUS_TITLE
        ]
    }

    @Secured(role = ROLE_ADMIN, opusSpecific = false)
    def create() {
        render view: "edit", model: [
                logoUrl    : DEFAULT_OPUS_LOGO_URL,
                bannerUrl  : DEFAULT_OPUS_BANNER_URL,
                pageTitle  : DEFAULT_OPUS_TITLE,
                currentUser: authService.getDisplayName()
        ]
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def edit() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound()
        } else {
            render(view: 'edit', model: [
                    logoUrl    : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                    bannerUrl  : opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                    pageTitle  : opus.title ?: DEFAULT_OPUS_TITLE,
                    glossaryUrl: getGlossaryUrl(opus),
                    currentUser: authService.getDisplayName()
            ])
        }
    }

    private getGlossaryUrl(opus) {
        opus.glossaryUuid ? "${request.contextPath}/opus/${opus.uuid}/glossary" : ""
    }

    def show() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound()
        } else {
            render view: 'show', model: [
                    logoUrl    : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                    bannerUrl  : opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                    pageTitle  : opus.title ?: DEFAULT_OPUS_TITLE,
                    glossaryUrl: getGlossaryUrl(opus)
            ]
        }
    }

    def list() {
        render profileService.getOpus() as JSON
    }

    def getJson() {
        if (!params.opusId) {
            badRequest()
        } else {
            def opus = profileService.getOpus(params.opusId as String)

            if (!opus) {
                notFound()
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render opus as JSON
            }
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def updateUsers() {
        def jsonRequest = request.getJSON();

        if (!params.opusId) {
            badRequest()
        } else {
            def response = profileService.updateOpusUsers(params.opusId, jsonRequest)

            handle response
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def updateOpus() {
        def jsonRequest = request.getJSON();

        if (!params.opusId || !jsonRequest) {
            badRequest()
        } else {
            def response = profileService.updateOpus(params.opusId, jsonRequest)

            handle response
        }
    }

    @Secured(role = ROLE_ADMIN, opusSpecific = false)
    def createOpus() {
        def jsonRequest = request.getJSON();

        if (!jsonRequest) {
            badRequest()
        } else {
            def response = profileService.createOpus(jsonRequest)

            handle response
        }
    }

    @Secured(role = ROLE_ADMIN, opusSpecific = false)
    def deleteOpus() {
        if (!params.opusId) {
            badRequest "opusId is a required parameter"
        } else {
            def response = profileService.deleteOpus(params.opusId as String)

            handle response
        }
    }

    def searchPanel = {
        render template: "search"
    }

    def browsePanel = {
        render template: "browse"
    }

    def opusSummaryPanel = {
        render template: "opusSummary"
    }

    def editAccessControlPanel = {
        render template: "editAccessControl"
    }

    def editStylingPanel = {
        render template: "editStyling"
    }

    def editOpusDetailsPanel = {
        render template: "editOpusDetails"
    }

    def editMapConfigPanel = {
        render template: "editMapConfig"
    }

    def editImageSourcesPanel = {
        render template: "editImageSources"
    }

    def editRecordSourcesPanel = {
        render template: "editRecordSources"
    }

    def editSupportingOpusPanel = {
        render template: "editSupportingOpuses"
    }

    def editVocabPanel = {
        render template: "editVocab"
    }

    def taxaUploadPanel = {
        render template: "taxaUpload"
    }

    def occurrenceUploadPanel = {
        render template: "occurrenceUpload"
    }

    def phyloUploadPanel = {
        render template: "phyloUpload"
    }

    def keyUploadPanel = {
        render template: "keyUpload"
    }

    def editGlossaryPanel = {
        render template: "editGlossary"
    }

    def editApprovedListsPanel = {
        render template: "editApprovedLists"
    }

    def editAuthorshipPanel = {
        render template: "editAuthorship"
    }
}
