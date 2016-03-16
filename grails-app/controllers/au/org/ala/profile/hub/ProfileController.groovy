package au.org.ala.profile.hub

import au.org.ala.profile.analytics.Analytics
import au.org.ala.profile.security.Secured
import au.org.ala.profile.security.PrivateCollectionSecurityExempt
import au.org.ala.web.AuthService
import grails.converters.JSON
import groovy.json.JsonSlurper
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest

import static au.org.ala.profile.security.Role.ROLE_PROFILE_EDITOR

class ProfileController extends BaseController {

    AuthService authService
    ProfileService profileService
    BiocacheService biocacheService
    ExportService exportService
    ImageService imageService

    def index() {}

    @Secured(role = ROLE_PROFILE_EDITOR)
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
                          aboutPageUrl: getAboutUrl(model.opus, model.profile),
                          footerText  : model.opus.footerText,
                          contact     : model.opus.contact,
                          displayMap  : profileService.hasMatchedName(model.profile)]
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
                          aboutPageUrl: getAboutUrl(profile.opus, profile.profile),
                          footerText  : profile.opus.footerText,
                          contact     : profile.opus.contact,
                          displayMap  : profileService.hasMatchedName(model.profile)]
                render view: "show", model: model
            }
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
    def createProfile() {
        def jsonRequest = request.getJSON();

        if (!jsonRequest) {
            badRequest()
        } else {
            def response = profileService.createProfile(params.opusId as String, jsonRequest)

            handle response
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
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

    @Secured(role = ROLE_PROFILE_EDITOR)
    def renameProfile() {
        def json = request.getJSON()
        if (!params.opusId || !params.profileId || !json) {
            badRequest()
        } else {
            def response = profileService.renameProfile(params.opusId as String, params.profileId as String, json)

            handle response
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
    def toggleDraftMode() {
        if (!params.profileId) {
            badRequest()
        } else {
            if (params.snapshot == 'true' && enabled("publications")) {
                savePublication()
            }

            // if we're already in draft mode and are publishing the changes, then we also need to publish any staged images
            def profile = profileService.getProfile(params.opusId, params.profileId, true)
            if (profile.profile.privateMode) {
                imageService.publishStagedImages(params.opusId, params.profileId)
            }

            def response = profileService.toggleDraftMode(params.opusId as String, params.profileId as String)

            handle response
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
    def publishPrivateImage() {
        if (!params.opusId || !params.profileId || !params.imageId) {
            badRequest()
        } else {
            def response = imageService.publishPrivateImage(params.opusId, params.profileId, params.imageId)

            handle response
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
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

    @Secured(role = ROLE_PROFILE_EDITOR)
    def deleteProfile() {
        if (!params.profileId || !params.opusId) {
            badRequest "profileId and opusId are required parameters"
        } else {
            def response = profileService.deleteProfile(params.opusId as String, params.profileId as String)

            handle response
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
    def archiveProfile() {
        def json = request.getJSON()
        if (!params.profileId || !params.opusId || !json?.archiveComment) {
            badRequest "profileId, opusId and archiveComment are required parameters"
        } else {

            // create a final snapshot version of the profile before archiving it, if the publications feature is enabled
            if (enabled("publications")) {
                savePublication()
            }

            def response = profileService.archiveProfile(params.opusId as String, params.profileId as String, json.archiveComment as String)

            handle response
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
    def restoreArchivedProfile() {
        def json = request.getJSON()

        if (!params.profileId || !params.opusId) {
            badRequest "profileId and opusId are required parameters"
        } else {
            def response = profileService.restoreArchivedProfile(params.opusId as String, params.profileId as String, json?.newName ?: null)

            handle response
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
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

    @Secured(role = ROLE_PROFILE_EDITOR)
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

    @Secured(role = ROLE_PROFILE_EDITOR)
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

    @Secured(role = ROLE_PROFILE_EDITOR)
    def deleteAttribute() {
        if (!params.attributeId || !params.profileId) {
            badRequest "attributeId and profileId are required parameters"
        } else {
            def response = profileService.deleteAttribute(params.opusId as String, params.attributeId, params.profileId)

            handle response
        }
    }

    def getPrimaryImage() {
        if (!params.opusId || !params.profileId) {
            badRequest "opusId and profileId are required parameters"
        } else {
            boolean latest = params.isOpusReviewer || params.isOpusEditor || params.isOpusAdmin

            def model = profileService.getProfile(params.opusId, params.profileId, latest)

            String primaryImageId = model.profile.primaryImage

            String searchIdentifier = model.profile.guid ? "lsid:" + model.profile.guid : model.profile.scientificName
            List images = imageService.retrieveImages(params.opusId, params.profileId, latest, model.opus.imageSources.toArray().join(","), searchIdentifier)?.resp

            Map primaryImage = images.find { it.imageId == primaryImageId } ?: images[0] ?: [:]

            render primaryImage as JSON
        }
    }

    def retrieveImages() {
        if (!params.opusId || !params.profileId || !params.imageSources) {
            badRequest "opusId, profileId and imageSources are required parameters"
        } else {
            boolean latest = params.isOpusReviewer || params.isOpusEditor || params.isOpusAdmin
            boolean readonlyView = params.getBoolean('readonlyView', true)

            def response = imageService.retrieveImages(params.opusId, params.profileId, latest, params.imageSources, params.searchIdentifier, false, readonlyView)

            handle response
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
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

    @Secured(role = ROLE_PROFILE_EDITOR)
    def deleteLocalImage() {
        if (!params.opusId || !params.profileId || !params.imageId || !params.type) {
            badRequest "opusId, profileId, imageId and type are required parameters"
        } else {
            try {
                ImageType imageType = params.type as ImageType

                boolean deleted = false

                if (imageType == ImageType.STAGED) {
                    deleted = imageService.deleteStagedImage(params.opusId, params.profileId, params.imageId)
                } else if (imageType == ImageType.PRIVATE) {
                    deleted = imageService.deletePrivateImage(params.opusId, params.profileId, params.imageId)
                }

                render([success: deleted] as JSON)
            } catch (IllegalArgumentException e) {
                log.warn(e)
                badRequest "Invalid image type ${params.type}"
            }
        }
    }

    def getLocalImage() {
        if (!params.type) {
            badRequest "type is a required parameter"
        } else {
            try {
                ImageType type = params.type as ImageType
                //NB this imageId param already has the file extension on it, really the file name on disk
                if (type == ImageType.STAGED) {
                    downloadFile("${grailsApplication.config.image.staging.dir}/", params.opusId, params.profileId, params.imageId)
                } else if (type == ImageType.PRIVATE) {
                    downloadFile("${grailsApplication.config.image.private.dir}/", params.opusId, params.profileId, params.imageId)
                }
            } catch (IllegalArgumentException e) {
                log.warn(e)
                badRequest "Invalid image type ${params.type}"
            }
        }
    }

    @PrivateCollectionSecurityExempt
    def downloadTempFile() {
        downloadFile("${grailsApplication.config.temp.file.location}", params.fileId)
    }

    //In the sense that the private or staged images are fetched from disk for display on the profile page
    //note we are supporting 2 possible file locations for backwards compatibility
    private downloadFile(String path, String collectionId, String profileId, String fileName) {
        if (!fileName) {
            badRequest "fileId is a required parameter"
        } else {
            String imageId = fileName.substring(0, fileName.size() - 4)
            String thumbnailName = makeThumbnailName(fileName)
            File file = new File("${path}/${collectionId}/${profileId}/${imageId}/${imageId}_thumbnails/${thumbnailName}")
            if (!file.exists()) {  //use the image if there is no thumbnail
                 file = new File("${path}/${collectionId}/${profileId}/${imageId}/${fileName}")
            }
            if (!file.exists()) {  //support for version 1 file locations
                file = new File("${path}/${profileId}/${fileName}")
            }
            if (!file.exists()) {
                notFound "The requested file could not be found"
            } else {
                response.setHeader("Content-disposition", "attachment;filename=${fileName}")
                response.setContentType(getContentType(file))
                response.outputStream << file.newInputStream()
            }
        }
    }

    private downloadFile(String path, String filename) {
        if (!filename) {
            badRequest "fileId is a required parameter"
        } else {
            File file = new File("${path}/${filename}")

            if (!file.exists()) {
                notFound "The requested file could not be found"
            } else {
                response.setHeader("Content-disposition", "attachment;filename=${filename}")
                response.setContentType(getContentType(file))
                response.outputStream << file.newInputStream()
            }
        }
    }

    private getContentType(File file) {
        String extension = file.getName().substring(file.getName().lastIndexOf("."))

        List images = ["jpg", "jpeg", "gif", "tiff", "png", "bmp"]
        if (images.contains(extension)) {
            "image/*"
        } else if (extension == "pdf") {
            "application/pdf"
        } else {
            ""
        }
    }

    private String makeThumbnailName(String fileName)  {
        String extension = fileName.substring(fileName.lastIndexOf("."))
        String imageId = fileName.substring(0, fileName.size() - 4)
        String thumbnailName = "${imageId}_thumbnail${extension}"
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

    @Analytics
    def proxyPublicationDownload() {
        final pubId = params.publicationId as String
        if (!pubId) {
            badRequest "Publication Id must be provided"
        } else {
            final opusId = params.opusId as String
            final profileId = params.profileId as String
            log.info("Proxying publication download opus $opusId, $profileId, $pubId")
            profileService.proxyGetPublicationFile(response, opusId, profileId, pubId)
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
                    model.aboutPageUrl = getAboutUrl(profile.opus, profile)
                    model.footerText = profile.opus.footerText
                    model.contact = profile.opus.contact
                    model.publications = pubJson.publications

                    render view: "publication", model: model;
                }
            }
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
    def savePublication() {
        if (!enabled("publications")) {
            badRequest "The publications feature has been disabled"
        } else if (!params.profileId || !params.opusId) {
            badRequest "profileId and opusId are required parameters"
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
                    images      : true,
                    status      : true,
                    key         : true,
                    printVersion: true
            ]

            byte[] pdf = exportService.createPdf(pdfOptions)

            def response = profileService.savePublication(params.opusId as String, params.profileId as String, pdf)

            handle response
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
    def updateAuthorship() {
        def json = request.getJSON()

        if (!params.profileId || !json) {
            badRequest "profileId and a json body are required parameters"
        } else {
            def response = profileService.updateAuthorship(params.opusId as String, params.profileId as String, json)

            handle response
        }
    }

    def getFeatureLists() {
        if (!params.profileId || !params.opusId) {
            badRequest "profile id and opus id must be provided";
        } else {
            def response = profileService.getFeatureLists(params.opusId, params.profileId);
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

    def getAttachmentMetadata() {
        if (!params.opusId || !params.profileId) {
            badRequest "opusId and profileId are required parameters"
        } else {
            boolean latest = params.isOpusReviewer || params.isOpusEditor || params.isOpusAdmin

            def result = profileService.getAttachmentMetadata(params.opusId, params.profileId, params.attachmentId ?: null, latest)

            handle result
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
    def saveAttachment() {
        if (!params.opusId || !params.profileId || !(request instanceof MultipartHttpServletRequest) || !request.getParameter("data")) {
            badRequest "opusId and profile are required parameters, a JSON data paramaeter must be provided, and the request must be a multipart request"
        } else if (request instanceof DefaultMultipartHttpServletRequest) {
            if (request.fileNames && request.getFile(request.fileNames[0]).contentType != "application/pdf") {
                badRequest "Invalid file type - must be one of [PDF]"
            } else {
                Map attachment = new JsonSlurper().parseText(request.getParameter("data"))

                if (!attachment.uuid) {
                    attachment.filename = request.getFile("file").originalFilename
                }

                def result = profileService.saveAttachment(params.opusId, params.profileId, attachment, request)

                handle result
            }
        } else {
            badRequest "Request is not multipart"
        }
    }

    @Secured(role = ROLE_PROFILE_EDITOR)
    def deleteAttachment() {
        if (!params.opusId || !params.profileId || !params.attachmentId) {
            badRequest "opusId, profileId and attachmentId are required parameters"
        } else {
            def result = profileService.deleteAttachment(params.opusId, params.profileId, params.attachmentId)

            handle result
        }
    }

    private getGlossaryUrl(opus) {
        opus?.glossaryUuid ? "${request.contextPath}/opus/${opus.uuid}/glossary" : ""
    }

    private getAboutUrl(opus, profile) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/about#?profile=${profile.scientificName}"
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
