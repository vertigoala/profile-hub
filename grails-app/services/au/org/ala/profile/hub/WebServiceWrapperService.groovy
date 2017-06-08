package au.org.ala.profile.hub

import org.apache.http.entity.ContentType
import org.codehaus.groovy.grails.web.util.WebUtils

/**
 * XXX Used to add florulaOverrideId to all GET requests if the cookie exists
 */
class WebServiceWrapperService {

    static transactional = false
    static final FLORULA_OVERRIDE_PARAM = 'florulaOverrideId'
    static final FLORULA_COOKIE = 'phf'

    def webService
    def authService

    Map get(String url, Map params = [:], ContentType contentType = ContentType.APPLICATION_JSON, boolean includeApiKey = true, boolean includeUser = true, Map customHeaders = [:]) {
        // only add it if there is no current user
        if (!authService.userId) {
            def florulaId = extractFlorulaId()
            if (florulaId) {
                params += [(FLORULA_OVERRIDE_PARAM): florulaId]
            }
        }
        return webService.get(url, params, contentType, includeApiKey, includeUser, customHeaders)
    }

    String extractFlorulaId() {
        def wr = WebUtils.retrieveGrailsWebRequest()
        wr.request.cookies.find { it.name == FLORULA_COOKIE }?.value
    }
}
