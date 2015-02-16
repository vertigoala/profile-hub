package au.org.ala.profile.hub

import au.org.ala.profile.hub.util.JsonUtil

class CollectoryService {

    def grailsApplication

    JsonUtil jsonUtil = new JsonUtil()

    Map<String, String> getDataResources() {
        def dataResources = [:]

        try {
            jsonUtil.fromUrl("${grailsApplication.config.collectory.service.url}/dataResource").each {
                dataResources.put(it.uid, it.name)
            }
        } catch (Exception e) {
            e.printStackTrace()
            dataResources = null
            log.error("Unable to retrieve data resources", e)
        }

        dataResources
    }
}
