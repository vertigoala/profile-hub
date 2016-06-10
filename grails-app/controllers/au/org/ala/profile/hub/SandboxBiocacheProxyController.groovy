package au.org.ala.profile.hub

import au.org.ala.ws.service.WebService

/**
 * When a collection is configured to use private data, all requests to the biocache are changed to [contextRoot/...],
 * which are configured to hit this controller. This controller then proxies all requests to the Profiles Sandbox
 * biocache-service instance, which is not publically accessible.
 */
class SandboxBiocacheProxyController {

    def grailsApplication
    WebService webService

    def proxy() {
        String requestPath = request.forwardURI.substring("${request.contextPath}/ws".length() + 1)
        String queryString = request.queryString
        String baseUrl = "${grailsApplication.config.sandbox.base.url}/biocache-service/"

        webService.proxyGetRequest(response, "${baseUrl}${requestPath}${queryString ? "?" : ""}${queryString ?: ""}", true, true)
    }

}
