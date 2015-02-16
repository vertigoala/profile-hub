package au.org.ala.profile.hub.util

import groovy.json.JsonSlurper
import org.slf4j.LoggerFactory

class JsonUtil {

    private static final log = LoggerFactory.getLogger(this)

    def fromUrl(String url = "") {
        log.debug("Fetching JSON from URL ${url}...")

        assert url?.trim(), "url is a mandatory parameter"

        String text = new URL(url).text;

        def json = null
        if (text) {
            try {
                json = new JsonSlurper().parseText(text)
            } catch (Exception e) {
                log.warn("Unable to parse JSON from URL text from ${url}")
                log.debug("Text retrieved was: ${text}")
            }
        }

        json
    }
}
