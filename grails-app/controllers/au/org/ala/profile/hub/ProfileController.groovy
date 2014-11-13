package au.org.ala.profile.hub

import groovy.json.JsonSlurper

class ProfileController {

    def index() {


    }

    def show(){

        def js = new JsonSlurper()

        def profile = js.parseText(new URL("http://localhost:8081/profile-service/profile/Acacia+acuminata").text).profile

        def opus = js.parseText(new URL("http://localhost:8081/profile-service/opus/${profile.opusId}").text)

        def query = ""

        if(profile.guid){
            query = "lsid:" + profile.guid
        } else {
            query = profile.scientificName
        }

        opus



        //Record query

        "http://biocache.ala.org.au/occurrences/search?q=${query}&fq=data_resource_uid:"

        //WMS URL


//        profile.opus.


        def listsURL = "http://lists.ala.org.au/ws/species/urn:lsid:biodiversity.org.au:apni.taxon:302011"

        [
            profile: js.parseText(new URL("http://localhost:8081/profile-service/profile/Acacia+acuminata").text),
            lists: []
        ]
    }
}
