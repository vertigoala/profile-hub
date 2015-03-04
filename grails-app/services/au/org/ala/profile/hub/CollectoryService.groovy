package au.org.ala.profile.hub

class CollectoryService {

    def grailsApplication
    WebService webService

    Map<String, String> getDataResources() {
        def dataResources = [:]

        try {
            def resources = webService.get("${grailsApplication.config.collectory.base.url}ws/dataResource")
            resources?.resp.each {
                dataResources.put(it.uid, it.name)
            }
        } catch (Exception e) {
            e.printStackTrace()
            dataResources = null
            log.error("Unable to retrieve data resources", e)
        }

        dataResources
    }

    def getDataResource(dataResourceUid) {
        webService.get("${grailsApplication.config.collectory.base.url}ws/dataResource/${dataResourceUid}")
    }
}
