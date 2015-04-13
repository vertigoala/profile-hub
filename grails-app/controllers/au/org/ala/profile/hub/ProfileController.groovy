package au.org.ala.profile.hub

import au.org.ala.profile.security.Role
import au.org.ala.profile.security.Secured
import au.org.ala.web.AuthService
import grails.converters.JSON
import org.apache.commons.fileupload.FileItemIterator
import org.apache.commons.fileupload.FileItemStream
import org.apache.commons.fileupload.servlet.ServletFileUpload

class ProfileController extends BaseController {

    AuthService authService
    ProfileService profileService
    BiocacheService biocacheService
    SpeciesListService speciesListService

    def index() {}

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def edit() {
        if (!params.profileId) {
            badRequest "profileId is a required parameter"
        } else {
            def profile = profileService.getProfile(params.profileId as String)

            if (!profile) {
                notFound()
            } else {
                Map model = profile
                model << [edit: true, currentUser: authService.getDisplayName()]
                render view: "show", model: model
            }
        }
    }

    def show() {
        if (!params.profileId) {
            badRequest "profileId is a required parameter"
        } else {
            def profile = profileService.getProfile(params.profileId as String)

            if (!profile) {
                notFound()
            } else {
                Map model = profile
                model << [edit: false]
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
            def response = profileService.createProfile(jsonRequest)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def updateProfile() {
        def json = request.getJSON()

        if (!json || !params.profileId) {
            badRequest()
        } else {
            def response = profileService.updateProfile(params.profileId as String, json)

            handle response
        }
    }

    def getJson() {
        if (!params.profileId) {
            badRequest "profileId is a required parameter"
        } else {
            response.setContentType(CONTEXT_TYPE_JSON)
            def profile = profileService.getProfile(params.profileId as String)

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
            def response = profileService.deleteProfile(params.profileId as String)

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
            def response = profileService.updateBHLLinks(params.profileId as String, jsonRequest.links)

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

            def response = profileService.updateLinks(params.profileId as String, jsonRequest.links)

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
            def response = profileService.updateAttribute(params.profileId, jsonRequest)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_EDITOR)
    def deleteAttribute() {
        if (!params.attributeId || !params.profileId) {
            badRequest "attributeId and profileId are required parameters"
        } else {
            def response = profileService.deleteAttribute(params.attributeId, params.profileId)

            handle response
        }
    }

    def retrieveImages() {
        if (!params.imageSources || !params.searchIdentifier) {
            badRequest "Image sources and searchIdentifier are required parameters"
        } else {
            def response = biocacheService.retrieveImages(params.searchIdentifier, params.imageSources)

            handle response
        }
    }

    def retrieveClassifications() {
        if (!params.guid || !params.opusId) {
            badRequest "GUID and opusId are required parameters"
        } else {
            def response = profileService.getClassification(params.guid, params.opusId)

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

    def search() {
        if (!params.scientificName) {
            badRequest "scientificName is a required parameter. opusId and useWildcard are optional."
        } else {
            boolean wildcard = params.useWildcard ? params.useWildcard.toBoolean() : true
            def response = profileService.search(params.opusId, params.scientificName, wildcard);

            handle response
        }
    }

    def retrievePublication() {
        if (!params.profileId) {
            badRequest "profileId is a required parameter"
        } else {
            def response = profileService.getPublications(params.profileId as String)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_ADMIN)
    def savePublication() {
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iterator = upload.getItemIterator(request);

        byte[] file = null
        def publication = null

        while(iterator.hasNext()) {
            FileItemStream item = iterator.next();
            if (item.fieldName == "file") {
                file = item.openStream().bytes
            } else {
                publication = JSON.parse(item.openStream().text)
            }
        }

        // the publicationId may be blank (e.g. when creating a new publication), but the request should still have it
        if (!file || !publication || !params.profileId) {
            badRequest()
        } else {
            def response = profileService.savePublication(params.profileId, publication, file)

            handle response
        }
    }

    @Secured(role = Role.ROLE_PROFILE_ADMIN)
    def deletePublication() {
        if (!params.publicationId) {
            badRequest "publicationId is a required parameter"
        } else {
            def response = profileService.deletePublication(params.publicationId as String)

            handle response
        }
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
}
