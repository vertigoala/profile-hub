package au.org.ala.profile.hub

import grails.converters.JSON
import groovy.json.JsonSlurper

class ProfileController {

    WebService webService

    def index() {}

    def edit(){
        def model = buildProfile(params.uuid)
        //need CAS check here
        model.put("edit", true)
        render(view: "show", model: model)
    }

    def show(){
        buildProfile(params.uuid)
    }

    def updateAttribute() {
        println "Updating attributing....."
        //TODO check user in ROLE.....
        def resp = webService.doPost("http://localhost:8081/profile-service/attribute/" + params.uuid?:'', [
                userId: "to-be-added",
                title : params.title,
                text : params.text,
                profileUuid : params.profileUuid,
                uuid : params.uuid?:'',
        ])
        response.setContentType("application/json")
        response.setStatus(201)
        render resp.resp as JSON
    }

    def deleteAttribute(){
        //TODO check user in ROLE.....
        def resp = webService.doDelete("http://localhost:8081/profile-service/attribute/" + params.uuid +"?profileUuid=" + params.profileUuid)
        response.setContentType("application/json")
        response.setStatus(201)
//        render resp as JSON
        def model = ["success":true]
        render model as JSON
    }

    private def buildProfile(String uuid){

        println("getProfile " + uuid )

        def js = new JsonSlurper()

        def profile = js.parseText(new URL("http://localhost:8081/profile-service/profile/" + URLEncoder.encode(uuid, "UTF-8")).text)

        def opus = js.parseText(new URL("http://localhost:8081/profile-service/opus/${profile.opusId}").text)

        def query = ""

        if(profile.guid){
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
        def availableProfiles = []
        if(profile.guid){
            classification = js.parseText(new URL("http://bie.ala.org.au/ws/classification/" + profile.guid).text)
        }

        def speciesProfile
        if(profile.guid){
            speciesProfile = js.parseText(new URL("http://bie.ala.org.au/ws/species/" + profile.guid).text)
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
