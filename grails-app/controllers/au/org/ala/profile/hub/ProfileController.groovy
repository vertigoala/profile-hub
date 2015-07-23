package au.org.ala.profile.hub

import au.org.ala.profile.security.Role
import au.org.ala.profile.security.Secured
import au.org.ala.web.AuthService
import grails.converters.JSON
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest

class ProfileController extends BaseController {

    AuthService authService
    ProfileService profileService
    BiocacheService biocacheService
    ExportService exportService
    ImageService imageService

    def index() {}

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def edit() {
        if (!params.opusId || !params.profileId) {
            badRequest "opusId and profileId are required parameters"
        } else {
            boolean latest = params.isOpusReviewer || params.isOpusEditor || params.isOpusAdmin
            def model = profileService.getProfile(params.opusId as String, params.profileId as String, latest)

            if (!model || !model.profile) {
                notFound()
            } else if (model.profile.archivedDate) {
                // archived profiles cannot be edited by anyone
                notAuthorised()
            } else {
                model << [edit        : true,
                          currentUser : authService.getDisplayName(),
                          glossaryUrl : getGlossaryUrl(model.opus),
                          aboutPageUrl: getAboutUrl(model.opus),
                          footerText  : model.opus.footerText,
                          contact     : model.opus.contact]
                render view: "show", model: model
            }
        }
    }

    def show() {
        if (!params.profileId) {
            badRequest "profileId is a required parameter"
        } else {
            boolean latest = params.isOpusReviewer || params.isOpusEditor || params.isOpusAdmin
            def profile = profileService.getProfile(params.opusId as String, params.profileId as String, latest)

            if (!profile) {
                notFound()
            } else {
                Map model = profile
                model << [edit        : false,
                          glossaryUrl : getGlossaryUrl(profile.opus),
                          aboutPageUrl: getAboutUrl(profile.opus),
                          footerText  : profile.opus.footerText,
                          contact     : profile.opus.contact]
                render view: "show", model: model
            }
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def createProfile() {
        def jsonRequest = request.getJSON();

        if (!jsonRequest) {
            badRequest()
        } else {
            def response = profileService.createProfile(params.opusId as String, jsonRequest)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def updateProfile() {
        def json = request.getJSON()

        if (!json || !params.profileId) {
            badRequest()
        } else {
            boolean latest = params.isOpusReviewer || params.isOpusEditor || params.isOpusAdmin
            def response = profileService.updateProfile(params.opusId as String, params.profileId as String, json, latest)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def renameProfile() {
        def json = request.getJSON()
        if (!params.opusId || !params.profileId || !json) {
            badRequest()
        } else {
            def response = profileService.renameProfile(params.opusId as String, params.profileId as String, json)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def toggleDraftMode() {
        if (!params.profileId) {
            badRequest()
        } else {
            if (params.snapshot == 'true') {
                savePublication()
            }

            // if we're already in draft mode and are publishing the changes, then we also need to publish any staged images
            def profile = profileService.getProfile(params.opusId, params.profileId, true)
            if (profile.profile.privateMode) {
                imageService.publishImages(params.opusId, params.profileId)
            }

            def response = profileService.toggleDraftMode(params.opusId as String, params.profileId as String)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def discardDraftChanges() {
        if (!params.profileId) {
            badRequest()
        } else {
            def response = profileService.discardDraftChanges(params.opusId as String, params.profileId as String)

            handle response
        }
    }

    def getJson() {
        if (!params.profileId) {
            badRequest "profileId is a required parameter"
        } else {
            response.setContentType(CONTEXT_TYPE_JSON)
            boolean latest = params.isOpusReviewer || params.isOpusEditor || params.isOpusAdmin
            def profile = profileService.getProfile(params.opusId as String, params.profileId as String, latest)

            if (!profile) {
                notFound()
            } else {
                render profile as JSON
            }
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def deleteProfile() {
        if (!params.profileId || !params.opusId) {
            badRequest "profileId and opusId are required parameters"
        } else {
            def response = profileService.deleteProfile(params.opusId as String, params.profileId as String)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def archiveProfile() {
        def json = request.getJSON()
        if (!params.profileId || !params.opusId || !json?.archiveComment) {
            badRequest "profileId, opusId and archiveComment are required parameters"
        } else {

            // create a final snapshot version of the profile before archiving it
//            savePublication()

            def response = profileService.archiveProfile(params.opusId as String, params.profileId as String, json.archiveComment as String)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def restoreArchivedProfile() {
        def json = request.getJSON()

        if (!params.profileId || !params.opusId) {
            badRequest "profileId and opusId are required parameters"
        } else {
            def response = profileService.restoreArchivedProfile(params.opusId as String, params.profileId as String, json?.newName ?: null)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def updateBHLLinks() {
        def jsonRequest = request.getJSON()

        if (!jsonRequest || !params.profileId) {
            badRequest()
        } else {
            log.debug "Updating bhl links....."
            def response = profileService.updateBHLLinks(params.opusId as String, params.profileId as String, jsonRequest.links)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def updateLinks() {
        def jsonRequest = request.getJSON()

        if (!jsonRequest || !params.profileId) {
            badRequest()
        } else {
            log.debug "Updating links....."

            def response = profileService.updateLinks(params.opusId as String, params.profileId as String, jsonRequest.links)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def updateAttribute() {
        log.debug "Updating attributing....."
        def jsonRequest = request.getJSON()

        // the attributeId may be blank (e.g. when creating a new attribute), but the request should still have it
        if (!jsonRequest || !jsonRequest.has("uuid") || !params.profileId) {
            badRequest()
        } else {
            def response = profileService.updateAttribute(params.opusId as String, params.profileId, jsonRequest)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def deleteAttribute() {
        if (!params.attributeId || !params.profileId) {
            badRequest "attributeId and profileId are required parameters"
        } else {
            def response = profileService.deleteAttribute(params.opusId as String, params.attributeId, params.profileId)

            handle response
        }
    }

    def retrieveImages() {
        if (!params.opusId || !params.profileId || !params.imageSources || !params.searchIdentifier) {
            badRequest "opusId, profileId, imageSources and searchIdentifier are required parameters"
        } else {
            boolean latest = params.isOpusReviewer || params.isOpusEditor || params.isOpusAdmin

            def response = imageService.retrieveImages(params.opusId, params.profileId, latest, params.imageSources, params.searchIdentifier)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def uploadImage() {
        if (!params.opusId || !params.profileId || !params.dataResourceId || !params.title) {
            badRequest "opusId, dataResourceId, title and profileId are mandatory fields"
        }

        if (request instanceof DefaultMultipartHttpServletRequest) {
            MultipartFile file = ((DefaultMultipartHttpServletRequest) request).getFile("file")

            List<Map> multimedia = [
                    [
                            creator         : params.creator ?: "",
                            rights          : params.rights ?: "",
                            rightsHolder    : params.rightsHolder ?: "",
                            licence         : params.licence ?: "",
                            title           : params.title ?: "",
                            description     : params.description ?: "",
                            dateCreated     : params.dateCreated ?: "",
                            originalFilename: file.originalFilename
                    ]
            ]
            Map metadata = [multimedia: multimedia]

            def response = imageService.uploadImage(params.opusId, params.profileId, request.getParameter("dataResourceId"), metadata, file)

            handle response
        } else {
            badRequest()
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def deleteStagedImage() {
        if (!params.opusId || !params.profileId || !params.imageId) {
            badRequest "opusId, profileId and imageId are required parameters"
        } else {
            boolean deleted = imageService.deleteStagedImage(params.opusId, params.profileId, params.imageId)

            render([success: deleted] as JSON)
        }
    }

    def getStagedImage() {
        downloadFile("${grailsApplication.config.image.staging.dir}/${params.profileId}", params.imageId, "image/*")
    }

    def downloadTempFile() {
        downloadFile("${grailsApplication.config.temp.file.location}", params.fileId, "application/pdf")
    }

    private downloadFile(String path, String filename, String contentType) {
        if (!filename) {
            badRequest "fileId is a required parameter"
        } else {
            File file = new File("${path}/${filename}")

            if (!file) {
                notFound "The requested file could not be found"
            } else {
                response.setHeader("Content-disposition", "attachment;filename=${filename}")
                response.outputStream << file.newInputStream()
            }
        }
    }

    def retrieveClassifications() {
        if (!params.profileId || !params.guid || !params.opusId) {
            badRequest "profileId, guid and opusId are required parameters"
        } else {
            def response = profileService.getClassification(params.opusId, params.profileId, params.guid)

            handle response
        }
    }

    def retrieveSpeciesProfile() {
        if (!params.guid) {
            badRequest "GUID is a required parameter"
        } else {
            def response = profileService.getSpeciesProfile(params.guid)

            handle response
        }
    }

    def retrievePublication() {
        if (!params.profileId) {
            badRequest "profileId is a required parameter"
        } else {
            def response = profileService.getPublications(params.opusId as String, params.profileId as String)

            handle response
        }
    }

    def getPublication() {
        def pubId = params.pubId;
        if (!pubId) {
            badRequest "Publication Id must be provided";
        } else {
            def pubJson = profileService.getPublications(pubId)?.resp;
            if (!pubJson) {
                notFound()
            } else {
                boolean latest = params.isOpusReviewer || params.isOpusEditor || params.isOpusAdmin;
                def profile = profileService.getProfile(pubJson.opusId, pubJson.profileId, latest);

                if (!profile) {
                    notFound()
                } else {
                    Map model = profile
                    model.edit = false
                    model.currentUser = authService.getDisplayName()
                    model.glossaryUrl = getGlossaryUrl(profile.opus)
                    model.aboutPageUrl = getAboutUrl(profile.opus)
                    model.footerText = profile.opus.footerText
                    model.contact = profile.opus.contact
                    model.publications = pubJson.publications

                    render view: "publication", model: model;
                }
            }
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def savePublication() {
        if (!params.profileId || !params.opusId) {
            badRequest "profileId and opudId are required parameters"
        } else {
            Map pdfOptions = [
                    profileId   : params.profileId,
                    opusId      : params.opusId,
                    attributes  : true,
                    map         : true,
                    nomenclature: true,
                    taxonomy    : true,
                    bibliography: true,
                    links       : true,
                    bhllinks    : true,
                    specimens   : true,
                    conservation: true,
                    images      : true
            ]

            byte[] pdf = exportService.createPdf(pdfOptions)

            def response = profileService.savePublication(params.opusId as String, params.profileId as String, pdf)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def updateAuthorship() {
        def json = request.getJSON()

        if (!params.profileId || !json) {
            badRequest "profileId and a json body are required parameters"
        } else {
            def response = profileService.updateAuthorship(params.opusId as String, params.profileId as String, json)

            handle response
        }
    }

    def getBioStatus() {
        if (!params.profileId || !params.opusId) {
            badRequest "profile id and opus id must be provided";
        } else {
            def response = profileService.getBioStatus(params.opusId, params.profileId);
            render response as JSON
        }
    }

    def checkName() {
        if (!params.opusId || !params.scientificName) {
            badRequest "opusId and scientificName are required parameters"
        } else {
            def response = profileService.checkName(params.opusId as String, params.scientificName as String)

            handle response
        }
    }

    def getPublicationJson() {
        if (!params.pubId) {
            badRequest "Publication Id must be provided";
        } else {
            def result = profileService.getPublications(params.pubId);
            handle result;
        }
    }

    private getGlossaryUrl(opus) {
        opus?.glossaryUuid ? "${request.contextPath}/opus/${opus.uuid}/glossary" : ""
    }

    private getAboutUrl(opus) {
        opus.hasAboutPage ? "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/about" : ""
    }

    def attributesPanel = {
        render template: "attributes"
    }

    def linksPanel = {
        render template: "links"
    }

    def bhlLinksPanel = {
        render template: "bhlLinks"
    }

    def taxonPanel = {
        render template: "taxon"
    }

    def imagesPanel = {
        render template: "images"
    }

    def mapPanel = {
        render template: "map"
    }

    def listsPanel = {
        render template: "lists"
    }

    def publicationsPanel = {
        render template: "publications"
    }

    def bibliographyPanel = {
        render template: "bibliography"
    }

    def specimenPanel = {
        render template: "specimens"
    }

    def commentsPanel = {
        render template: "comments"
    }

    def authorPanel = {
        render template: "authorship"
    }

    def editNamePanel = {
        render template: "editNamePanel"
    }

    def auditHistoryPanel = {
        render template: "profileAuditHistory"
    }

    def nomenclaturePanel = {
        render template: "nomenclaturePanel"
    }
}
