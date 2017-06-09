package au.org.ala.profile.hub

import au.org.ala.profile.security.Secured
import au.org.ala.web.AuthService
import au.org.ala.ws.service.WebService
import grails.converters.JSON
import groovy.json.JsonSlurper
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest

import javax.servlet.http.Cookie

import static au.org.ala.profile.hub.Utils.enc
import static au.org.ala.profile.hub.Utils.encPath
import static au.org.ala.profile.hub.WebServiceWrapperService.FLORULA_COOKIE
import static au.org.ala.profile.hub.util.HubConstants.*
import static au.org.ala.profile.security.Role.ROLE_ADMIN
import static au.org.ala.profile.security.Role.ROLE_PROFILE_ADMIN
import static javax.servlet.http.HttpServletResponse.SC_BAD_GATEWAY

class OpusController extends BaseController {

    AuthService authService
    CollectoryService collectoryService
    UserService userService
    ProfileService profileService
    KeybaseService keybaseService
    WebService webService

    def index() {
        render view: 'index', model: [
                logos     : DEFAULT_OPUS_LOGOS,
                bannerUrl   : DEFAULT_OPUS_BANNER_URL,
                bannerHeight: DEFAULT_OPUS_BANNER_HEIGHT_PX,
                footerText  : ALA_FOOTER_TEXT,
                contact     : [email   : ALA_CONTACT_EMAIL,
                               facebook: ALA_CONTACT_FACEBOOK,
                               twitter : ALA_CONTACT_TWITTER]
        ]
    }

    def search() {
        if (params.opusId == null) {
            render view: 'search', model: [
                    logos     : DEFAULT_OPUS_LOGOS,
                    bannerUrl   : DEFAULT_OPUS_BANNER_URL,
                    bannerHeight: DEFAULT_OPUS_BANNER_HEIGHT_PX,
                    footerText  : ALA_FOOTER_TEXT,
                    contact     : [email   : ALA_CONTACT_EMAIL,
                                   facebook: ALA_CONTACT_FACEBOOK,
                                   twitter : ALA_CONTACT_TWITTER]
            ]
        } else {
            def opus = profileService.getOpus(params.opusId as String)
            def model = commonViewModelParams(opus, 'search')
            render(view: 'search', model: model)
        }
    }

    def browse() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound()
        } else {
            def model = commonViewModelParams(opus, 'browse')
            render(view: 'browse', model: model)
        }
    }

    def identify() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound()
        } else {
            def model = commonViewModelParams(opus, 'identify')
            render(view: 'identify', model: model)
        }
    }

    def filter() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound("Opus ${params.opusId} not found")
        } else {
            def model = commonViewModelParams(opus, 'filter')
            def listType = grailsApplication.config.lists.masterlist.type ?: 'PROFILE'
            def lists = webService.get(grailsApplication.config.lists.base.url + '/ws/speciesList', [ 'listType': 'eq:' + listType , max: -1, user: authService.userId ])
            if (!lists || !(lists.statusCode in 200..299)) {
                response.sendError(SC_BAD_GATEWAY, "lists service unavailable")
            } else {
                render(view: 'filter', model: model + [lists: lists.resp.lists])
            }
        }
    }

    def documents() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound()
        } else {
            def model = commonViewModelParams(opus, 'documents')
            render(view: 'documents', model: model)
        }
    }

    def reports() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound()
        } else if(!params.isOpusEditor){
            notAuthorised()
        } else {
            def model = commonViewModelParams(opus, 'reports')
            render(view: 'reports', model: model)
        }
    }

    @Secured(role = ROLE_ADMIN, opusSpecific = false)
    def create() {
        render view: "edit", model: [
                logos     : DEFAULT_OPUS_LOGOS,
                bannerUrl   : DEFAULT_OPUS_BANNER_URL,
                bannerHeight: DEFAULT_OPUS_BANNER_HEIGHT_PX,
                currentUser : authService.getDisplayName()
        ]
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def edit() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound()
        } else {
            def model = commonViewModelParams(opus)
            render(view: 'edit', model: model)
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def generateAccessToken() {
        if (!params.opusId) {
            badRequest()
        } else {
            def opus = profileService.getOpus(params.opusId as String)

            if (!opus) {
                notFound()
            } else {
                def response = profileService.generateAccessTokenForOpus(params.opusId as String)

                handle response
            }
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def revokeAccessToken() {
        if (!params.opusId) {
            badRequest()
        } else {
            def opus = profileService.getOpus(params.opusId as String)

            if (!opus) {
                notFound()
            } else {
                def response = profileService.revokeAccessTokenForOpus(params.opusId as String)

                handle response
            }
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
                def model = commonViewModelParams(opus, 'about', "About ${opus.title}" ?: DEFAULT_OPUS_TITLE)
                render(view: 'about', model: model)
            }
        }
    }

    def getAboutHtml() {
        def response = profileService.getOpusAboutContent(params.opusId as String)
        response?.resp?.opus << [
                opusUrl             : "${grailsApplication.config.grails.serverURL}/opus/${params.opusId}",
                date                : new Date().format('dd MMMM yyyy - hh:mm'),
                year                : new Date().format('yyyy'),
                genericCopyrightHtml: GENERIC_COPYRIGHT_TEXT
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
                def model = commonViewModelParams(opus, '', opus.title ?: DEFAULT_OPUS_TITLE)
                render view: 'shareRequest', model: model
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

    def show() {
        def opus = profileService.getOpus(params.opusId as String)

        if (!opus) {
            notFound()
        } else {
            def model = commonViewModelParams(opus, 'opus')
            render view: 'show', model: model
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
                response.setContentType(CONTENT_TYPE_JSON)
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

    def getAttachmentMetadata() {
        if (!params.opusId) {
            badRequest "opusId is a required parameter"
        } else {
            def result = profileService.getAttachmentMetadata(params.opusId, null, params.attachmentId ?: null)

            handle result
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def saveAttachment() {
        if (!params.opusId || !(request instanceof MultipartHttpServletRequest) || !request.getParameter("data")) {
            badRequest "opusId is a required parameter, a JSON data paramaeter must be provided, and the request must be a multipart request"
        } else if (request instanceof DefaultMultipartHttpServletRequest) {
            if (request.fileNames && request.getFile(request.fileNames[0]).contentType != "application/pdf") {
                badRequest "Invalid file type - must be one of [PDF]"
            } else {
                Map attachment = new JsonSlurper().parseText(request.getParameter("data"))

                if (!attachment.uuid && !attachment.url) {
                    attachment.filename = request.getFile("file").originalFilename
                }

                def result = profileService.saveAttachment(params.opusId, null, attachment, request)

                handle result
            }
        } else {
            badRequest "Request is not multipart"
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def deleteAttachment() {
        if (!params.opusId || !params.attachmentId) {
            badRequest "opusId and attachmentId are required parameters"
        } else {
            def result = profileService.deleteAttachment(params.opusId, null, params.attachmentId)

            handle result
        }
    }

    def downloadImage() {
        if (!params.opusId || !params.filename) {
            badRequest "opusId and filename are required parameters"
        } else {
            Map opus = profileService.getOpus(params.opusId)

            if (opus) {
                File file = new File("${grailsApplication.config.image.private.dir}/${opus.uuid}/${params.filename}")
                if (file.exists()) {
                    response.setHeader("Content-disposition", "attachment;filename=${params.fileName}")
                    response.setContentType(Utils.getContentType(file))
                    file.withInputStream { response.outputStream << it }
                } else {
                    notFound "No matching file could be found"
                }
            } else {
                notFound "No collection was found for id ${params.opus}"
            }
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def deleteImage() {
        if (!params.opusId || !params.filename) {
            badRequest "opusId and filename are required parameters"
        } else {
            Map opus = profileService.getOpus(params.opusId)

            if (opus) {
                File file = new File("${grailsApplication.config.image.private.dir}/${opus.uuid}/${params.filename}")
                if (file.exists()) {
                    boolean success = file.delete()

                    render([success: success] as JSON)
                } else {
                    notFound "No matching file could be found"
                }
            } else {
                notFound "No collection was found for id ${params.opus}"
            }
        }
    }

    /**
     * Allows uploading branding images for the collection. A purpose must be assigned to each image, and there can only
     * be 1 image for each purpose.
     * @return JSON object containing [imageUrl: ...]
     */
    @Secured(role = ROLE_PROFILE_ADMIN)
    def uploadImage() {
        if (!params.opusId || !params.purpose) {
            badRequest "opusId and purpose are mandatory fields"
        } else if (request instanceof DefaultMultipartHttpServletRequest) {
            MultipartFile file = ((DefaultMultipartHttpServletRequest) request).getFile("file")
            uploadTransferrable(new MultipartFileTransferrableAdapter(multipartFile: file))
        } else if (params.url) {
            final url = new UrlTransferrableAdapter(url: params.url.toURL())
            url.withCloseable { uploadTransferrable(url) }
        } else {
            badRequest "file or url is required"
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def updateAdditionalStatuses() {
        if (!params.opusId) {
            badRequest("opusId is mandatory")
        } else {
            profileService.updateOpusAdditionalStatuses(params.opusId, request.JSON)
            response.sendError(204)
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def updateMasterList() {
        if (!params.opusId) {
            badRequest("opusId is mandatory")
        } else {
            profileService.updateOpusMasterList(params.opusId, request.JSON)
            response.sendError(204)
        }
    }

    @Secured(role = ROLE_PROFILE_ADMIN)
    def syncMasterList() {
        if (!params.opusId) {
            badRequest("opusId is mandatory")
        } else {
            def regen = params.boolean('regenerateStubs', false)
            profileService.syncOpusMasterList(params.opusId, regen)
            response.sendError(204)
        }
    }

    def getMasterListKeybaseItems() {
        if (!params.opusId) {
            badRequest "opusId is mandatory"
        } else {
            def list = profileService.getMasterListKeybaseItems(params.opusId)?.resp
            if (list == null) {
                notFound "Ain't no master list"
            } else {
                respond list
            }
        }
    }

    def updateFlorulaList() {
        def opusId = params.opusId
        String florulaListId = params.florulaListId
        if (!opusId) {
            badRequest "opusId is mandatory"
        } else {
            def userId = authService.userId
            if (userId) {
                profileService.updateFlorulaList(opusId, florulaListId)
            } else {
                Cookie cookie = request.cookies.find { it.name == FLORULA_COOKIE }
                if (!cookie) {
                    cookie = new Cookie(FLORULA_COOKIE, florulaListId)
                }
                cookie.httpOnly = false
                cookie.maxAge = -1
                cookie.secure = false
                cookie.setPath(createLink( uri: "/opus/${encPath(opusId)}" ).toString())
                cookie.setDomain(request.serverName)
                cookie.setValue(florulaListId)
                response.addCookie(cookie)
            }
            flash.message = "Filter ${florulaListId ? 'enabled' : 'removed'}"
            redirect(uri: "/opus/${encPath(opusId)}/filter")
        }
    }

    def getTags() {
        Map response = profileService.getTags()

        handle response
    }

    def getStyleSheet() {
        if(!params.opusId){
            badRequest("opusId is mandatory")
        } else {
            Map map = profileService.getStyleSheet(params.opusId)
            render text: map.css, contentType: 'text/css';
        }
    }

    private def uploadTransferrable(Transferrable transferrable) {

        Map opus = profileService.getOpus(params.opusId)

        if (opus) {
            File imageDir = new File("${grailsApplication.config.image.private.dir}/${opus.uuid}")
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }

            File imageFile = new File(imageDir, "${params.purpose}${transferrable.fileExtension}")
            if (imageFile.exists()) {
                log.warn("${imageFile.getAbsolutePath()} already exists. Overwriting.")
                imageFile.delete()
            }

            transferrable.transferTo(imageFile)

            render([imageUrl: "${request.contextPath}/opus/${opus.uuid}/image/${imageFile.getName()}"] as JSON)
        } else {
            notFound "No collection was found for id ${params.opus}"
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

    def editLandingPagePanel = {
        render template: "editLandingPage"
    }

    def editTheme = {
        render template: "editTheme"
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

    def editGlossaryPanel = {
        render template: "editGlossary"
    }

    def editApprovedListsPanel = {
        render template: "editApprovedLists"
    }

    def editFeatureListPanel = {
        render template: "editFeatureLists"
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

    private def commonViewModelParams(def opus, String pageName = '', String pageTitle = '',
                                      def doMainBanner = false) {
        def florulaListId = authService.userId ? opus.florulaListId : request.cookies.find { it.name == FLORULA_COOKIE }?.value
        def model = [
                opus            : opus,
                logos           : opus.brandingConfig?.logos ?: DEFAULT_OPUS_LOGOS,
                bannerUrl       : opus.brandingConfig?.opusBannerUrl ?: opus.brandingConfig?.profileBannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                bannerHeight    : opus.brandingConfig?.opusBannerHeight ?: opus.brandingConfig?.profileBannerHeight ?: DEFAULT_OPUS_BANNER_HEIGHT_PX,
                footerText      : opus.footerText,
                contact         : opus.contact,
                bannerItems     : getBannerItems(opus, doMainBanner),
                opusUrl         : getOpusUrl(opus),
                searchUrl       : getSearchUrl(opus),
                browseUrl       : getBrowseUrl(opus),
                filterUrl       : getFilterUrl(opus),
                identifyUrl     : getIdentifyUrl(opus),
                documentsUrl    : getDocumentsUrl(opus),
                reportsUrl      : getReportsUrl(opus),
                glossaryUrl     : getGlossaryUrl(opus),
                aboutPageUrl    : getAboutUrl(opus),
                currentUser     : authService.getDisplayName(),
                opusTitle       : opus.title,
                hasFilter       : florulaListId,
                florulaListId   : florulaListId
        ]

        if (pageName) model += [pageName: pageName]
        if (pageTitle) model += [pageTitle: pageTitle]

        return model
    }
}
