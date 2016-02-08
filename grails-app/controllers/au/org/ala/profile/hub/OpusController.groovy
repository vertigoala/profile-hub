package au.org.ala.profile.hub

import au.org.ala.profile.hub.util.HubConstants
import au.org.ala.profile.security.Secured
import au.org.ala.web.AuthService
import grails.converters.JSON

import static au.org.ala.profile.hub.util.HubConstants.*
import static au.org.ala.profile.security.Role.ROLE_ADMIN
import static au.org.ala.profile.security.Role.ROLE_PROFILE_ADMIN

class OpusController extends BaseController {

    AuthService authService
    CollectoryService collectoryService
    UserService userService
    ProfileService profileService
    KeybaseService keybaseService

    def index() {
        render view: 'index', model: [
                logoUrl   : DEFAULT_OPUS_LOGO_URL,
                bannerUrl : DEFAULT_OPUS_BANNER_URL,
                pageTitle : DEFAULT_OPUS_TITLE,
                footerText: ALA_FOOTER_TEXT,
                contact   : [email   : ALA_CONTACT_EMAIL,
                             facebook: ALA_CONTACT_FACEBOOK,
                             twitter : ALA_CONTACT_TWITTER]
        ]
    }

    def search() {
        render view: 'search', model: [
                logoUrl   : DEFAULT_OPUS_LOGO_URL,
                bannerUrl : DEFAULT_OPUS_BANNER_URL,
                pageTitle : DEFAULT_OPUS_TITLE,
                footerText: ALA_FOOTER_TEXT,
                contact   : [email   : ALA_CONTACT_EMAIL,
                             facebook: ALA_CONTACT_FACEBOOK,
                             twitter : ALA_CONTACT_TWITTER]
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
                    logoUrl     : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                    bannerUrl   : opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                    pageTitle   : opus.title ?: DEFAULT_OPUS_TITLE,
                    footerText  : opus.footerText,
                    contact     : opus.contact,
                    glossaryUrl : getGlossaryUrl(opus),
                    aboutPageUrl: getAboutUrl(opus),
                    currentUser : authService.getDisplayName()
            ])
        }
    }

    def about() {
        if (!params.opusId) {
            badRequest()
        } else {
            def opus = profileService.getOpus(params.opusId as String)

            if (!opus) {
                notFound()
            } else {
                render(view: 'about', model: [
                        logoUrl   : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                        bannerUrl : opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                        footerText: opus.footerText,
                        contact   : opus.contact,
                        opusTitle : opus.title,
                        pageTitle : "About ${opus.title}" ?: DEFAULT_OPUS_TITLE
                ])
            }
        }
    }

    def getAboutHtml() {
        def response = profileService.getOpusAboutContent(params.opusId as String)
        response?.resp?.opus << [
                opusUrl: "${grailsApplication.config.grails.serverURL}/opus/${params.opusId}",
                date: new Date().format('dd MMMM yyyy - hh:mm'),
                year: new Date().format('yyyy'),
                genericCopyrightHtml: HubConstants.GENERIC_COPYRIGHT_TEXT
        ]

        handle response
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def updateAbout() {
        def json = request.getJSON()
        if (!params.opusId || !json || !json.containsKey("aboutHtml") || !json.containsKey("citationHtml")) {
            badRequest()
        } else {
            def response = profileService.updateOpusAboutContent(params.opusId as String, json.aboutHtml as String, json.citationHtml as String)

            handle response
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def getSupportingCollectionRequest() {
        if (!params.opusId || !params.requestingOpusId) {
            badRequest()
        } else {
            def opus = profileService.getOpus(params.opusId as String)

            if (!opus) {
                notFound()
            } else {
                render view: 'shareRequest', model: [
                        logoUrl     : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                        bannerUrl   : opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                        pageTitle   : opus.title ?: DEFAULT_OPUS_TITLE,
                        footerText  : opus.footerText,
                        contact     : opus.contact,
                        glossaryUrl : getGlossaryUrl(opus),
                        aboutPageUrl: getAboutUrl(opus)
                ]
            }
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def updateSupportingCollections() {
        def json = request.getJSON()
        if (!params.opusId || !json) {
            badRequest()
        } else {
            profileService.updateSupportingCollections(params.opusId as String, json)

            render([success: true] as JSON)
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def respondToSupportingCollectionRequest() {
        if (!params.opusId || !params.requestingOpusId || !params.requestAction) {
            badRequest()
        } else {
            profileService.respondToSupportingCollectionRequest(params.opusId, params.requestingOpusId, params.requestAction)

            render([success: true] as JSON)
        }
    }

    private getGlossaryUrl(opus) {
        opus.glossaryUuid ? "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/glossary" : ""
    }

    private getAboutUrl(opus) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/about"
    }

    def show() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound()
        } else {
            render view: 'show', model: [
                    logoUrl     : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                    bannerUrl   : opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                    pageTitle   : opus.title ?: DEFAULT_OPUS_TITLE,
                    footerText  : opus.footerText,
                    contact     : opus.contact,
                    glossaryUrl : getGlossaryUrl(opus),
                    aboutPageUrl: getAboutUrl(opus)
            ]
        }
    }

    def list() {
        List opuses = profileService.getOpus()

        // remove all private collections if there is no logged in user, or if the user is not registered with the collection
        List filtered = params.isALAAdmin ? opuses : opuses.findAll {
            !it.privateCollection || it.authorities.find { auth -> auth.userId == authService.getUserId() }
        }

        render filtered as JSON
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

    def retrieveKeybaseProjects() {
        def response = keybaseService.retrieveAllProjects()

        handle response
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

    def editBioStatusPanel = {
        render template: "editBioStatus"
    }

    def editAuthorshipPanel = {
        render template: "editAuthorship"
    }

    def editKeyConfigPanel = {
        render template: "editKeyConfig"
    }

    def editAboutPanel = {
        render template: "editAboutPage"
    }

    def reportPanel = {
        render template: "report"
    }
}
