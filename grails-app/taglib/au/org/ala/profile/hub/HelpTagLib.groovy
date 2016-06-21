package au.org.ala.profile.hub

import groovy.json.JsonSlurper

class HelpTagLib {
    static namespace = "p"

    static final String HELP_URLS = "help_urls"

    def help = { attrs ->
        Map helpUrls = servletContext.getAttribute(HELP_URLS)
        if (!helpUrls) {
            helpUrls = loadHelpUrls()
        }

        String urlSuffix = helpUrls?.urls?.get(attrs["help-id"])
        if (urlSuffix) {
            boolean floatRight = attrs.containsKey("float") ? attrs.float.toBoolean() : true
            boolean show = attrs.containsKey("show") ? attrs.show.toBoolean() : true
            out << render(template:"/common/helpIcon", model: [baseUrl: helpUrls?.baseUrl, suffix: urlSuffix, floatRight: floatRight, show: show])
        }
    }

    private Map loadHelpUrls() {
        log.debug("Loading help url mappings...")
        String helpFile = "${grailsApplication.config.help.mapping.file}"

        File file = new File(helpFile)

        Map json = null

        if (file.exists()) {
            json = new JsonSlurper().parse(file)
            servletContext.setAttribute(HELP_URLS, json)
        } else {
            log.error("Failed to read help url mappings from ${helpFile}")
        }

        json
    }
}
