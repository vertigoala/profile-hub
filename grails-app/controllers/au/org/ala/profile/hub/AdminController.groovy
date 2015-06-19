package au.org.ala.profile.hub

import au.org.ala.profile.security.Role
import au.org.ala.profile.security.Secured
import grails.converters.JSON
import grails.util.Environment
import org.springframework.core.io.support.PathMatchingResourcePatternResolver

class AdminController extends BaseController {

    @Secured(role = Role.ROLE_ADMIN)
    def index() {
        render view: "admin.gsp"
    }

    @Secured(role = Role.ROLE_ADMIN)
    def postMessage() {
        if (request.getJSON().message) {
            servletContext.setAttribute("alaAdminMessage", request.getJSON().message)
            servletContext.setAttribute("alaAdminMessageTimestamp", new Date().format("dd/MM/yyyy HH:mm"))
        } else {
            servletContext.removeAttribute("alaAdminMessage")
            servletContext.removeAttribute("alaAdminMessageTimestamp")
        }
    }

    def getMessage() {
        render ([message: servletContext.getAttribute("alaAdminMessage"),
        timestamp: servletContext.getAttribute("alaAdminMessageTimestamp")] as JSON)
    }

    /**
     * Reload external config file
     */
    @Secured(role = Role.ROLE_ADMIN)
    def reloadConfig() {
        // reload system config
        def configLocation = "file:${grailsApplication.config.default_config}"
        def resolver = new PathMatchingResourcePatternResolver()
        def resource = resolver.getResource(configLocation)
        def stream = null

        try {
            stream = resource.getInputStream()
            ConfigSlurper configSlurper = new ConfigSlurper(Environment.current.name)
            if(resource.filename.endsWith('.groovy')) {
                def newConfig = configSlurper.parse(stream.text)
                grailsApplication.getConfig().merge(newConfig)
            }
            else if(resource.filename.endsWith('.properties')) {
                def props = new Properties()
                props.load(stream)
                def newConfig = configSlurper.parse(props)
                grailsApplication.getConfig().merge(newConfig)
            }

        }
        catch (FileNotFoundException fnf) {
            log.error "No external config to reload configuration. Looking for ${configLocation}", fnf
            flash.message = "No external config to reload configuration. Looking for ${configLocation}"
            redirect(action:'index')
        }
        catch (Exception gre) {
            log.error "Unable to reload configuration. Please correct problem and try again: ${gre}", gre
            flash.message =  "Unable to reload configuration - " + gre.getMessage()
            redirect(action:'index')
        }
        finally {
            stream?.close()
        }
    }
}
