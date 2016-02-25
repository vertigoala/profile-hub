package au.org.ala.profile.hub

import au.org.ala.profile.analytics.GoogleAnalyticsClient

import static grails.async.Promises.task

class AnalyticsService {

    static transactional = false

    def grailsApplication
    GoogleAnalyticsClient googleAnalyticsClient

    /**
     * Asynchronously Send an analytics event to the analytics end point.
     * @param hostname The hostname of the request
     * @param path The path of the request
     * @param cid The client id of the request
     */
    void pageView(String hostname, String path, String cid, String userIp, String userAgent, String referrer) {
        final String googleAnalyticsId = grailsApplication.config.googleAnalyticsId
        if (googleAnalyticsId) {
            task {
                final v = '1'
                final t = 'pageview'
                final data = [
                        dh : hostname,         // Document hostname.
                        dp : path,             // Page.
                        uip: userIp,           // User IP
                        ua : userAgent ?: '',  // User Agent
                        dr : referrer ?: '',   // Document referrer
                ]
                def call = googleAnalyticsClient.collect(v, googleAnalyticsId, cid, t, data)
                try {
                    def resp = call.execute()
                    if (!resp.success) {
                        log.warn("Analytics pageview for $cid with data: $data failed")
                    }
                } catch(e) {
                    log.error("Caught exception while sending pageview to analytics", e)
                }
            }
        }
    }

}
