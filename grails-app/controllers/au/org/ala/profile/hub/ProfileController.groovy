package au.org.ala.profile.hub

import au.org.ala.web.AuthService
import grails.converters.JSON
import groovy.json.JsonSlurper
import org.apache.http.HttpStatus

class ProfileController {

    AuthService authService
    ProfileService profileService

    def index() {}

    def edit() {
        def model = profileService.getProfile(params.profileId)

        if (!model) {
            notFound
        } else {
            // TODO need CAS check here
            model.put("edit", true)
            model.put("currentUser", authService.getDisplayName())
            render view: "show", model: model
        }
    }

    def show() {
        def model = profileService.getProfile(params.profileId)

        if (!model) {
            notFound
        } else {
            model.put("edit", false)
            render view: "show", model: model
        }
    }

    def updateBHLLinks() {
        def jsonRequest = getJsonRequest()

        log.debug "Updating attributing....."
        //TODO check user in ROLE.....
        def resp = profileService.updateBHLLinks(jsonRequest.profileId as String, jsonRequest.links)

        response.setContentType("application/json")
        render resp.resp as JSON
    }

    def updateLinks() {
        log.debug "Updating attributing....."

        def jsonRequest = getJsonRequest()

        //TODO check user in ROLE.....
        def resp = profileService.updateLinks(jsonRequest.profileId as String, jsonRequest.links)

        response.setContentType("application/json")
        render resp.resp as JSON
    }

    def updateAttribute() {
        log.debug "Updating attributing....."

        //TODO check user in ROLE.....
        def resp = profileService.updateAttributes(params.profileId, params.attributeId, params.titel, params.text)

        response.setContentType("application/json")
        render resp.resp as JSON
    }

    def deleteAttribute() {
        //TODO check user in ROLE.....
        def resp = profileService.deleteAttribute(params.attributeId)

        response.setContentType("application/json")
        response.setStatus(resp.resultCode)

        def model = ["success": resultCode == HttpStatus.SC_OK]
        render model as JSON
    }

    private getJsonRequest() {
        JsonSlurper slurper = new JsonSlurper()
        slurper.parse(request.getReader())
    }

    private notFound = {
        response.status = HttpStatus.SC_NOT_FOUND
        response.sendError(HttpStatus.SC_NOT_FOUND)
    }
}
