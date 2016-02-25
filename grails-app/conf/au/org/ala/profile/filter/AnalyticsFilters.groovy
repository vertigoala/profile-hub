package au.org.ala.profile.filter

import javax.servlet.http.Cookie

import au.org.ala.profile.analytics.Analytics

class AnalyticsFilters {

    def analyticsService

    def filters = {
        analytics(controller: '*', action: '*') {
            before = {

            }
            after = { Map model ->
                try {
                    if (response.getStatus() in 200..299) { // only care about successful HTTP responses
                        final artefact = grailsApplication.getArtefactByLogicalPropertyName('Controller', controllerName)
                        final annotation = artefact.clazz.with {
                            (it.methods.find { it.name == actionName }?.getAnnotation(Analytics) ?: it.declaredFields.find { it.name == actionName }?.getAnnotation(Analytics)) ?: it.getAnnotation(Analytics)
                        }
                        if (annotation) {
                            final cid = extractCidFromGACookie(request.cookies)
                            final path = URLDecoder.decode(request.forwardURI, 'utf-8')
                            log.debug("Sending pageview to analytics for ${request.serverName}, $path, $cid, $request.remoteAddr")
                            analyticsService.pageView(request.serverName, path, cid, request.remoteAddr, request.getHeader("user-agent"), request.getHeader("referer"))
                        }
                    }
                } catch (e) {
                    log.error("Caught exception in analytics filter", e)
                }

                true
            }
            afterView = { Exception e ->

            }
        }
    }

    /**
     * 1. Under Measurement Protocol it's recommended that you use UUID v4 format to produce standard, unique client
     * IDs. However, it also supports the old X.Y 32-bit IDs used in the previous generation of GA.

     * 2. Universal Analytics analytics.js creates a cookie called _ga. There's just one cookie, not several as there used
     * to be in ye olde GA. The cookie contains four values: 1.2.299259584.1357814039257.
     * (There are no longer two underscores as there were on old GA cookies).
     *
     * 1 = The cookie format version
     * 2 = The domain depth on which the cookie is written
     * 299259584.1357814039257 = The client id (cid)
     *
     * Using this CID will ensure server side analytics events match to the same user as the client side events.
     *
     * Source: https://plus.google.com/110147996971766876369/posts/Mz1ksPoBGHx
     *
     * If the CID can't be extracted from the cookie, a random UUID is used instead.  A CID may not be available in
     * the case that a download or PDF link has been given directly to a user.
     * TODO use a persistent UUID per user.
     *
     * @param cookie
     * @return
     */
    private String extractCidFromGACookie(Cookie[] cookies) {
        def cookie = cookies.find { it.name == '_ga' }
        def value = null
        if (cookie) {
            def splits = cookie.value.split('\\.')
            if (splits.length > 1 && splits[0] == 'GA1' && splits.length == 4) {
                value = splits[2] + '.' + splits[3]
            } else {
                log.warn("Got Google Analytics cookie but not of known version: ${cookie.value}")
            }
        }

        if (!value) {
            value = UUID.randomUUID().toString()
        }

        return value
    }
}
