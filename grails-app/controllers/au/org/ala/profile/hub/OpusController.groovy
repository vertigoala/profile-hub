package au.org.ala.profile.hub

import au.org.ala.profile.hub.util.HubConstants
import au.org.ala.profile.security.Secured
import au.org.ala.web.AuthService
import grails.converters.JSON
import groovy.json.JsonSlurper
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest

import static au.org.ala.profile.hub.util.HubConstants.*
import static au.org.ala.profile.security.Role.ROLE_ADMIN
import static au.org.ala.profile.security.Role.ROLE_PROFILE_ADMIN

class OpusController extends BaseController {

    AuthService authService
    CollectoryService collectoryService
    UserService userService
    ProfileService profileService
    KeybaseService keybaseService
    ImageService imageService

    def index() {
        render view: 'index', model: [
                logoUrl     : DEFAULT_OPUS_LOGO_URL,
                bannerUrl   : DEFAULT_OPUS_BANNER_URL,
                bannerHeight: DEFAULT_OPUS_BANNER_HEIGHT_PX,
                pageTitle   : DEFAULT_OPUS_TITLE,
                footerText  : ALA_FOOTER_TEXT,
                contact     : [email   : ALA_CONTACT_EMAIL,
                               facebook: ALA_CONTACT_FACEBOOK,
                               twitter : ALA_CONTACT_TWITTER]
        ]
    }

    def search() {
        render view: 'search', model: [
                logoUrl   : DEFAULT_OPUS_LOGO_URL,
                bannerUrl : DEFAULT_OPUS_BANNER_URL,
                bannerHeight: DEFAULT_OPUS_BANNER_HEIGHT_PX,
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
                bannerHeight: DEFAULT_OPUS_BANNER_HEIGHT_PX,
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
                    logoUrl     : opus.brandingConfig?.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                    bannerUrl   : opus.brandingConfig?.opusBannerUrl ?: opus.brandingConfig?.profileBannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                    bannerHeight: opus.brandingConfig?.opusBannerHeight ?: opus.brandingConfig?.profileBannerHeight ?: DEFAULT_OPUS_BANNER_HEIGHT_PX,
                    pageTitle   : opus.title ?: DEFAULT_OPUS_TITLE,
                    footerText  : opus.footerText,
                    contact     : opus.contact,
                    glossaryUrl : getGlossaryUrl(opus),
                    aboutPageUrl: getAboutUrl(opus),
                    currentUser : authService.getDisplayName()
            ])
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
                render(view: 'about', model: [
                        logoUrl   : opus.brandingConfig?.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                        bannerUrl : opus.brandingConfig?.opusBannerUrl ?: opus.brandingConfig?.profileBannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                        bannerHeight: opus.brandingConfig?.opusBannerHeight ?: opus.brandingConfig?.profileBannerHeight ?: DEFAULT_OPUS_BANNER_HEIGHT_PX,
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
                opusUrl             : "${grailsApplication.config.grails.serverURL}/opus/${params.opusId}",
                date                : new Date().format('dd MMMM yyyy - hh:mm'),
                year                : new Date().format('yyyy'),
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
                        logoUrl     : opus.brandingConfig?.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                        bannerUrl   : opus.brandingConfig?.opusBannerUrl ?: opus.brandingConfig?.profileBannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                        bannerHeight: opus.brandingConfig?.opusBannerHeight ?: opus.brandingConfig?.profileBannerHeight ?: DEFAULT_OPUS_BANNER_HEIGHT_PX,
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
                    logoUrl     : opus.brandingConfig?.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                    bannerUrl   : opus.brandingConfig?.opusBannerUrl ?: opus.brandingConfig?.profileBannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                    bannerHeight: opus.brandingConfig?.opusBannerHeight ?: opus.brandingConfig?.profileBannerHeight ?: DEFAULT_OPUS_BANNER_HEIGHT_PX,
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

            transferrable.to(imageFile)

            render ([imageUrl: "${request.contextPath}/opus/${opus.uuid}/image/${imageFile.getName()}"] as JSON)
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
}
