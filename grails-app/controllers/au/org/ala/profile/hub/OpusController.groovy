package au.org.ala.profile.hub

import groovy.json.JsonSlurper

class OpusController {

    def index() {
        def js = new JsonSlurper()
        def opui = js.parseText(new URL("http://localhost:8081/profile-service/opus/" ).text)
        [opui: opui, dataResources: getDataResources()]
    }

    def edit(){
        def js = new JsonSlurper()
        def opus = js.parseText(new URL("http://localhost:8081/profile-service/opus/" + params.uuid ).text)
        def dataResource = js.parseText(new URL("http://collections.ala.org.au/ws/dataResource/" + opus.dataResourceUid).text)
        render(view:'show', model:[opus: opus, dataResource:dataResource, dataResources: getDataResources()])
    }

    def show(){
        def js = new JsonSlurper()
        def opus = js.parseText(new URL("http://localhost:8081/profile-service/opus/" + params.uuid ).text)
        def dataResource = js.parseText(new URL("http://collections.ala.org.au/ws/dataResource/" + opus.dataResourceUid).text)
        [opus: opus, dataResource:dataResource, dataResources: getDataResources()]
    }

    def getDataResources(){
        def dataResources = [:]
        def js = new JsonSlurper()
        js.parseText(new URL("http://collections.ala.org.au/ws/dataResource").text).each {
            dataResources.put(it.uid, it.name)
        }
        dataResources
    }
}
