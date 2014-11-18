package au.org.ala.profile.hub

import groovy.json.JsonSlurper

class OpusController {

    def index() {
        def js = new JsonSlurper()
        def opui = js.parseText(new URL("http://localhost:8081/profile-service/opus/" ).text)
        [
         opui: opui,
         dataResources: getDataResources(),
         logoUrl: 'http://www.ala.org.au/wp-content/themes/ala2011/images/logo.png',
         bannerUrl: 'http://www.ala.org.au/wp-content/themes/ala2011/images/bg.jpg',
         pageTitle: 'Profile collections'
        ]
    }

    def edit(){
        def js = new JsonSlurper()
        def opus = js.parseText(new URL("http://localhost:8081/profile-service/opus/" + params.uuid ).text)
        def dataResource = js.parseText(new URL("http://collections.ala.org.au/ws/dataResource/" + opus.dataResourceUid).text)
        render(view:'show', model:[
                opus: opus,
                dataResource:dataResource,
                dataResources: getDataResources(),
                logoUrl: opus.logoUrl?:'http://www.ala.org.au/wp-content/themes/ala2011/images/logo.png',
                bannerUrl: opus.bannerUrl?:'http://www.ala.org.au/wp-content/themes/ala2011/images/bg.jpg',
                pageTitle: opus.title?:'Profile collections'
        ])
    }

    def show(){
        def js = new JsonSlurper()
        def opus = js.parseText(new URL("http://localhost:8081/profile-service/opus/" + params.uuid ).text)
        def dataResource = js.parseText(new URL("http://collections.ala.org.au/ws/dataResource/" + opus.dataResourceUid).text)
        [opus: opus, dataResource:dataResource, dataResources: getDataResources(),
         logoUrl: opus.logoUrl?:'http://www.ala.org.au/wp-content/themes/ala2011/images/logo.png',
         bannerUrl: opus.bannerUrl?:'http://www.ala.org.au/wp-content/themes/ala2011/images/bg.jpg',
         pageTitle: opus.title?:'Profile collections'
        ]
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
