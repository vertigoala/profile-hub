package au.org.ala.profile.hub

import au.org.ala.web.AuthService
import grails.converters.JSON
import org.apache.http.HttpStatus

class ProfileController extends BaseController {

    AuthService authService
    ProfileService profileService
    BiocacheService biocacheService

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
            response.setContentType("application/json")
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

            if (resp.statusCode != HttpStatus.SC_OK) {
                response.status = resp.statusCode
                response.sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType("application/json")
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

            if (resp.statusCode != HttpStatus.SC_OK) {
                response.status = resp.statusCode
                response.sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType("application/json")
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

            if (resp.statusCode != HttpStatus.SC_OK) {
                response.status = resp.statusCode
                response.sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType("application/json")
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

            if (resp.statusCode != HttpStatus.SC_OK) {
                response.status = resp.statusCode
                response.sendError(resp.statusCode, resp.error ?: "")
            } else {
                response.setContentType("application/json")
                def model = ["success": resp.statusCode == HttpStatus.SC_OK]
                render model as JSON
            }
        }
    }

    def retrieveImages() {
        if (!params.imageSources || !params.searchIdentifier) {
            badRequest()
        } else {
            def resp = biocacheService.retrieveImages(params.searchIdentifier, params.imageSources)

            if (resp.statusCode != HttpStatus.SC_OK) {
                response.status = resp.statusCode
                sendError(resp.statusCode, resp.error)
            } else {
                response.setContentType("application/json")
                render resp.resp as JSON
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
    def classificationPanel = {
        render template: "classification"
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
}
