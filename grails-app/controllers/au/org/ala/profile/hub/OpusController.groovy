package au.org.ala.profile.hub

import grails.converters.JSON

import static au.org.ala.profile.hub.util.HubConstants.*
import au.org.ala.web.AuthService

import static org.apache.http.HttpStatus.SC_OK

class OpusController extends BaseController {

    AuthService authService
    CollectoryService collectoryService
    UserService userService
    ProfileService profileService

    def index() {
        def opui = profileService.getOpus()

        render view: 'index', model: [
                opui         : opui ?: [],
                dataResources: collectoryService.getDataResources(),
                logoUrl      : DEFAULT_OPUS_LOGO_URL,
                bannerUrl    : DEFAULT_OPUS_BANNER_URL,
                pageTitle    : DEFAULT_OPUS_TITLE
        ]
    }

    def edit() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound()
        } else {
            render(view: 'edit', model: [
                    opus       : opus,
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
                    opus     : opus,
                    logoUrl  : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                    bannerUrl: opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                    pageTitle: opus.title ?: DEFAULT_OPUS_TITLE,
            ]
        }
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

            def resp = userService.findUser(jsonRequest.userName)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                response.sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render resp.resp as JSON
            }
        }
    }

    def updateOpus() {
        def jsonRequest = request.getJSON();

        if (!params.opusId || !jsonRequest) {
            badRequest()
        } else {
            def resp = profileService.updateOpus(params.opusId, jsonRequest)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                response.sendError(resp.statusCode, resp.error ?: "");
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render resp.resp as JSON
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
