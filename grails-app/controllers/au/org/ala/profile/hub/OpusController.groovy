package au.org.ala.profile.hub

import groovy.json.JsonSlurper

class OpusController {

    def index() {
        def js = new JsonSlurper()
        def opui = js.parseText(new URL(grailsApplication.config.profile.service.url + "/opus/" ).text)
        [
         opui: opui,
         dataResources: getDataResources(),
         logoUrl: 'http://www.ala.org.au/wp-content/themes/ala2011/images/logo.png',
         bannerUrl: 'http://images.ala.org.au/store/7/4/4/e/a08a52f2-7bbe-40d9-8f1a-fe8acb28e447/original',
         pageTitle: 'Profile collections',

        ]
    }

    def edit(){
        def js = new JsonSlurper()
        def opus = js.parseText(new URL(grailsApplication.config.profile.service.url + "/opus/" + params.uuid ).text)
        def dataResource = js.parseText(new URL("http://collections.ala.org.au/ws/dataResource/" + opus.dataResourceUid).text)
        def vocab
        if(opus.attributeVocabUuid){
            vocab = js.parseText(new URL(grailsApplication.config.profile.service.url + "/vocab/" + opus.attributeVocabUuid ).text)
        }
        render(view:'edit', model:[
                opus: opus,
                dataResource:dataResource,
                dataResources: getDataResources(),
                logoUrl: opus.logoUrl?:'http://www.ala.org.au/wp-content/themes/ala2011/images/logo.png',
                bannerUrl: opus.bannerUrl?:'http://images.ala.org.au/store/7/4/4/e/a08a52f2-7bbe-40d9-8f1a-fe8acb28e447/original',
                pageTitle: opus.title?:'Profile collections',
                vocab: vocab?.name?:'Not specified'
        ])
    }

    def show(){
        def js = new JsonSlurper()
        def opus = js.parseText(new URL(grailsApplication.config.profile.service.url + "/opus/" + params.uuid ).text)
        def vocab
        if(opus.attributeVocabUuid){
            vocab = js.parseText(new URL(grailsApplication.config.profile.service.url + "/vocab/" + opus.attributeVocabUuid ).text)
        }

        def dataResource = js.parseText(new URL("http://collections.ala.org.au/ws/dataResource/" + opus.dataResourceUid).text)
        [
         opus: opus,
         dataResource:dataResource,
         dataResources: getDataResources(),
         logoUrl: opus.logoUrl?:'http://www.ala.org.au/wp-content/themes/ala2011/images/logo.png',
         bannerUrl: opus.bannerUrl?:'http://www.ala.org.au/wp-content/themes/ala2011/images/bg.jpg',
         pageTitle: opus.title?:'Profile collections',
         vocab: vocab?.name?:'Not specified'
        ]
    }

    def getDataResources(){
        def dataResources = [:]
        def js = new JsonSlurper()
        try {
            js.parseText(new URL("http://collections.ala.org.au/ws/dataResource").text).each {
                dataResources.put(it.uid, it.name)
            }
        } catch(Exception e){
            log.error(e.getMessage(), e)
        }
        dataResources
    }
}
