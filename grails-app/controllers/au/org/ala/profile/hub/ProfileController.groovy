package au.org.ala.profile.hub

import au.org.ala.web.AuthService
import grails.converters.JSON
import static org.apache.http.HttpStatus.*

class ProfileController extends BaseController {

    AuthService authService
    ProfileService profileService
    BiocacheService biocacheService
    SpeciesListService speciesListService

    def index() {}

    def edit() {
        if (!params.profileId) {
            badRequest "profileId is a required parameter"
        } else {
            def profile = profileService.getProfile(params.profileId as String)

            if (!profile) {
                notFound()
            } else {
                // TODO need CAS check here
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

    def createProfile() {
        def jsonRequest = request.getJSON();

        if (!jsonRequest) {
            badRequest()
        } else {
            def response = profileService.createProfile(jsonRequest)

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

    def deleteProfile() {
        if (!params.profileId || !params.opusId) {
            badRequest "profileId and opusId are required parameters"
        } else {
            def resp = profileService.deleteProfile(params.profileId as String)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                redirect(controller: "opus", action: "show", params: [opusId: params.opusId])
            }
        }
    }

    def updateBHLLinks() {
        def jsonRequest = request.getJSON()

        if (!jsonRequest || !jsonRequest.profileId || !jsonRequest.links) {
            badRequest()
        } else {
            log.debug "Updating attributing....."
            //TODO check user in ROLE.....
            def response = profileService.updateBHLLinks(jsonRequest.profileId as String, jsonRequest.links)

            handle response
        }
    }

    def updateLinks() {
        log.debug "Updating attributing....."

        def jsonRequest = request.getJSON()
        if (!jsonRequest || !jsonRequest.profileId || !jsonRequest.links) {
            badRequest()
        } else {

            //TODO check user in ROLE.....
            def response = profileService.updateLinks(jsonRequest.profileId as String, jsonRequest.links)

            handle response
        }
    }

    def updateAttribute() {
        log.debug "Updating attributing....."
        def jsonRequest = request.getJSON()

        // the attributeId may be blank (e.g. when creating a new attribute), but the request should still have it
        if (!jsonRequest || !jsonRequest.has("uuid") || !params.profileId) {
            badRequest()
        } else {
            //TODO check user in ROLE.....
            def response = profileService.updateAttribute(params.profileId, jsonRequest)

            handle response
        }
    }

    def deleteAttribute() {
        if (!params.attributeId || !params.profileId) {
            badRequest "attributeId and profileId are required parameters"
        } else {
            //TODO check user in ROLE.....
            def resp = profileService.deleteAttribute(params.attributeId, params.profileId)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render([success: true] as JSON)
            }
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

    def retrieveLists() {
        if (!params.guid) {
            badRequest "GUID is a required parameter"
        } else {
            def response = speciesListService.getListsForGuid(params.guid)

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
}
