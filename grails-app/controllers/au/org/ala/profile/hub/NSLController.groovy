package au.org.ala.profile.hub

import grails.converters.JSON

class NSLController extends BaseController {
    NslService nslService

    def nameDetails() {
        if (!params.nslNameIdentifier) {
            badRequest "nslNameIdentifier is a required parameter"
        } else {
            def response = nslService.getNameDetails(params.nslNameIdentifier)

            handle response
        }
    }

    def listConcepts() {
        if (!params.nslNameIdentifier) {
            badRequest "nslNameIdentifier is a required parameter"
        } else {
            def concepts = nslService.listConcepts(params.nslNameIdentifier)

            render concepts as JSON
        }
    }
}
