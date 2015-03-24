package au.org.ala.profile.hub

import static au.org.ala.profile.hub.util.HubConstants.*
import grails.converters.JSON
import au.org.ala.web.AuthService

import static org.apache.http.HttpStatus.SC_OK

class OpusController extends BaseController {

    AuthService authService
    CollectoryService collectoryService
    UserService userService
    ProfileService profileService

    def index() {
        render view: 'index', model: [
                logoUrl      : DEFAULT_OPUS_LOGO_URL,
                bannerUrl    : DEFAULT_OPUS_BANNER_URL,
                pageTitle    : DEFAULT_OPUS_TITLE,
                isAdmin      : true // TODO determine ADMIN group membership
        ]
    }

    def create() {
        render view: "edit", model: [
                logoUrl    : DEFAULT_OPUS_LOGO_URL,
                bannerUrl  : DEFAULT_OPUS_BANNER_URL,
                pageTitle  : DEFAULT_OPUS_TITLE,
                currentUser: authService.getDisplayName()
        ]
    }

    def edit() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound()
        } else {
            render(view: 'edit', model: [
                    logoUrl    : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                    bannerUrl  : opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                    pageTitle  : opus.title ?: DEFAULT_OPUS_TITLE,
                    currentUser: authService.getDisplayName()
            ])
        }
    }

    def show() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound()
        } else {
            render view: 'show', model: [
                    logoUrl  : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                    bannerUrl: opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                    pageTitle: opus.title ?: DEFAULT_OPUS_TITLE,
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

    def findUser() {
        def jsonRequest = request.getJSON()

        if (!jsonRequest || !jsonRequest.userName) {
            badRequest()
        } else {
            log.debug "Searching for user ${jsonRequest.userName}....."

            def response = userService.findUser(jsonRequest.userName)

            handle response
        }
    }

    def updateOpus() {
        def jsonRequest = request.getJSON();

        if (!params.opusId || !jsonRequest) {
            badRequest()
        } else {
            def response = profileService.updateOpus(params.opusId, jsonRequest)

            handle response
        }
    }

    def createOpus() {
        def jsonRequest = request.getJSON();

        if (!jsonRequest) {
            badRequest()
        } else {
            def response = profileService.createOpus(jsonRequest)

            handle response
        }
    }

    def deleteOpus() {
        if (!params.opusId) {
            badRequest "opusId is a required parameter"
        } else {
            def resp = profileService.deleteOpus(params.opusId as String)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render resp.success as JSON
            }
        }
    }

    def searchPanel = {
        render template: "search"
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
}
