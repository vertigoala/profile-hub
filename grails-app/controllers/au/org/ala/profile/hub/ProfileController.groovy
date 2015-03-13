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
            badRequest()
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
            badRequest()
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

    def getJson() {
        if (!params.profileId) {
            badRequest()
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
        if (!jsonRequest || !jsonRequest.has("attributeId") || !jsonRequest.profileId) {
            badRequest()
        } else {
            //TODO check user in ROLE.....
            def resp = profileService.updateAttribute(jsonRequest.profileId, jsonRequest.attributeId, jsonRequest.title, jsonRequest.text)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                response.sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render resp.resp as JSON
            }
        }
    }

    def deleteAttribute() {
        if (!params.attributeId || !params.profileId) {
            badRequest()
        } else {
            //TODO check user in ROLE.....
            def resp = profileService.deleteAttribute(params.attributeId, params.profileId)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                response.sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                def model = ["success": resp.statusCode == SC_OK]
                render model as JSON
            }
        }
    }

    def retrieveImages() {
        if (!params.imageSources || !params.searchIdentifier) {
            badRequest()
        } else {
            def response = biocacheService.retrieveImages(params.searchIdentifier, params.imageSources)

            handle response
        }
    }

    def retrieveLists() {
        if (!params.guid) {
            badRequest()
        } else {
            def response = speciesListService.getListsForGuid(params.guid)

            handle response
        }
    }

    def retrieveClassifications() {
        if (!params.guid) {
            badRequest()
        } else {
            def response = profileService.getClassification(params.guid)

            handle response
        }
    }

    def retrieveSpeciesProfile() {
        if (!params.guid) {
            badRequest()
        } else {
            def response = profileService.getSpeciesProfile(params.guid)

            handle response
        }
    }

    def search() {
        if (!params.opusId || !params.scientificName) {
            badRequest()
        } else {
            def response = profileService.search(params.opusId, params.scientificName);

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
