package au.org.ala.profile.hub

import grails.converters.JSON
import groovy.json.JsonSlurper

class ProfileController {

    WebService webService
    def authService

    def index() {}

    def edit(){
        def model = buildProfile(params.uuid)
        //need CAS check here
        model.put("edit", true)
        model.put("currentUser", authService.getDisplayName())
        render(view: "show", model: model)
    }

    def show(){
        buildProfile(params.uuid)
    }

    def updateBHLLinks() {
        def jsonSlurper = new JsonSlurper()
        def json = jsonSlurper.parse(request.getReader())
        println "Updating attributing....."
        //TODO check user in ROLE.....
        def resp = webService.doPost(grailsApplication.config.profile.service.url + "/profile/bhl/" + json.profileUuid, [
                profileUuid : json.profileUuid,
                links : json.links,
                userId : authService.getUserId(),
                userDisplayName:authService.getDisplayName()
        ])
        response.setContentType("application/json")
        response.setStatus(201)
        render resp.resp as JSON
    }

    def updateLinks() {
        def jsonSlurper = new JsonSlurper()
        def json = jsonSlurper.parse(request.getReader())
        println "Updating attributing....."
        //TODO check user in ROLE.....
        def resp = webService.doPost(grailsApplication.config.profile.service.url + "/profile/links/" + json.profileUuid, [
                profileUuid : json.profileUuid,
                links : json.links,
                userId : authService.getUserId(),
                userDisplayName:authService.getDisplayName()
        ])
        response.setContentType("application/json")
        response.setStatus(201)
        render resp.resp as JSON
    }

    def updateAttribute() {
        println "Updating attributing....."
        //TODO check user in ROLE.....
        def resp = webService.doPost(grailsApplication.config.profile.service.url + "/attribute/" + params.uuid?:'', [
                title : params.title,
                text : params.text,
                profileUuid : params.profileUuid,
                uuid : params.uuid?:'',
                userId : authService.getUserId(),
                userDisplayName:authService.getDisplayName()
        ])
        response.setContentType("application/json")
        response.setStatus(201)
        render resp.resp as JSON
    }

    def deleteAttribute(){
        //TODO check user in ROLE.....
        def resp = webService.doDelete(grailsApplication.config.profile.service.url + "/attribute/" + params.uuid +"?profileUuid=" + params.profileUuid)
        response.setContentType("application/json")
        response.setStatus(201)
        def model = ["success":true]
        render model as JSON
    }

    private def buildProfile(String uuid){

        println("getProfile " + uuid )

        def js = new JsonSlurper()

        def profile = js.parseText(new URL(grailsApplication.config.profile.service.url + "/profile/" + URLEncoder.encode(uuid, "UTF-8")).text)

        def opus = js.parseText(new URL(grailsApplication.config.profile.service.url + "/opus/${profile.opusId}").text)

        def query = ""

        if(profile.guid && profile.guid != "null"){
            query = "lsid:" + profile.guid
        } else {
            query = profile.scientificName
        }

        def occurrenceQuery = query

        if(opus.recordSources){
            occurrenceQuery = query + " AND (data_resource_uid:" + opus.recordSources.join(" OR data_resource_uid:") + ")"
        }

        def imagesQuery = query
        if(opus.imageSources){
            imagesQuery = query + " AND (data_resource_uid:" + opus.imageSources.join(" OR data_resource_uid:") + ")"
        }

        def classification = []
        if(profile.guid){
            try {
                classification = js.parseText(new URL(grailsApplication.config.profile.service.url + "/classification?guid=" + profile.guid).text)
            } catch (Exception e){
                println "Unable to load classification for " + profile.guid
            }
        }

        def speciesProfile
        if(profile.guid){
            try {
                speciesProfile = js.parseText(new URL("http://bie.ala.org.au/ws/species/" + profile.guid).text)
            } catch (Exception e){
                println "Unable to load profile for " + profile.guid
            }
        }

        //WMS URL
        def listsURL = "http://lists.ala.org.au/ws/species/${profile.guid}"
        [
            occurrenceQuery: occurrenceQuery,
            imagesQuery: imagesQuery,
            opus: opus,
            profile: profile,
            classification: classification,
            speciesProfile: speciesProfile,
            lists: [],
            logoUrl: opus.logoUrl?:'http://www.ala.org.au/wp-content/themes/ala2011/images/logo.png',
            bannerUrl: opus.bannerUrl?:'http://www.ala.org.au/wp-content/themes/ala2011/images/bg.jpg',
            pageTitle: opus.title?:'Profile collections',
            pageTitleLink: createLink(mapping: 'viewOpus', params: ['uuid': opus.uuid])
        ]
    }
}
