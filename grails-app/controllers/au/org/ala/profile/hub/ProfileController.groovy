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
            def resp = profileService.updateBHLLinks(jsonRequest.profileId as String, jsonRequest.links)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                response.sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render resp.resp as JSON
            }
        }
    }

    def updateLinks() {
        log.debug "Updating attributing....."

        def jsonRequest = request.getJSON()
        if (!jsonRequest || !jsonRequest.profileId || !jsonRequest.links) {
            badRequest()
        } else {

            //TODO check user in ROLE.....
            def resp = profileService.updateLinks(jsonRequest.profileId as String, jsonRequest.links)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                response.sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render resp.resp as JSON
            }
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
            def resp = biocacheService.retrieveImages(params.searchIdentifier, params.imageSources)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                sendError(resp.statusCode, resp.error)
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render resp.resp as JSON
            }
        }
    }

    def retrieveLists() {
        if (!params.guid) {
            badRequest()
        } else {
            def resp = speciesListService.getListsForGuid(params.guid)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                sendError(resp.statusCode, resp.error)
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render resp.resp as JSON
            }
        }
    }

    def retrieveClassifications() {
        if (!params.guid) {
            badRequest()
        } else {
            def resp = profileService.getClassification(params.guid)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                sendError(resp.statusCode, resp.error)
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render resp.resp as JSON
            }
        }
    }

    def retrieveSpeciesProfile() {
        if (!params.guid) {
            badRequest()
        } else {
            def resp = profileService.getSpeciesProfile(params.guid)

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode
                sendError(resp.statusCode, resp.error)
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render resp.resp as JSON
            }
        }
    }

    def search() {
        if (!params.opusId || !params.scientificName) {
            badRequest()
        } else {
            def resp = profileService.search(params.opusId, params.scientificName);

            if (resp.statusCode != SC_OK) {
                response.status = resp.statusCode;
                sendError(resp.statusCode, resp.error);
            } else {
                response.setContentType(CONTEXT_TYPE_JSON);
                render resp.resp as JSON;
            }
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
