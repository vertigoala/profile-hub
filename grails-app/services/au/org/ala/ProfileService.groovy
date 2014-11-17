package au.org.ala

import grails.transaction.Transactional
import groovy.json.JsonSlurper

@Transactional
class ProfileService {

    def serviceMethod() {}

    def getProfile(String uuid){

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

        if(opus.recordSources){
            query = query + " AND (data_resource_uid:" + opus.recordSources.join(" OR ") + ")"
        }

        def imagesQuery = query + "&fq=multimedia:Image"

        //WMS URL
        def listsURL = "http://lists.ala.org.au/ws/species/${profile.guid}"
        [
            occurrenceQuery: query,
            imagesQuery: imagesQuery,
            opus: opus,
            profile: profile,
            lists: []
        ]
    }
}
