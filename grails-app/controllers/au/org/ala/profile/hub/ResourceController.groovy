package au.org.ala.profile.hub

import au.org.ala.web.AuthService
import au.org.ala.web.CASRoles
import grails.converters.JSON
import org.apache.commons.io.FilenameUtils
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

import static org.apache.http.HttpStatus.SC_BAD_REQUEST

class ResourceController {

    static final String ADMIN_ROLE = 'ROLE_MDBA_ADMIN'
    final String MDA_DOCUMENT_ROLE = 'mdba'

    GrailsApplication grailsApplication
    DocumentResourceService  documentResourceService
    AuthService authService

    def list() {
        Map searchParams = [role:MDA_DOCUMENT_ROLE]
        Map result = documentResourceService.search(searchParams)
        [documents:modelAsJavascript(result.documents), admin:isUserAdmin()]
    }

    /**
     * Check user is an admin
     * @return user admin status
     */
    boolean isUserAdmin(){
        authService.userInRole(ADMIN_ROLE) || authService.userInRole(CASRoles.ROLE_ADMIN)
    }

    String modelAsJavascript(def model) {

        if (!(model instanceof JSONObject) && !(model instanceof JSONArray)) {
            model = model as JSON

        }
        def json = (model?:[:] as JSON)
        def modelJson = json.toString()
        modelJson.encodeAsJavaScript()
    }

    /**
     * Proxies to the ecodata document controller.
     * @param id the id of the document to update (if not supplied, a create operation will be assumed).
     * @return the result of the update.
     */
    def documentUpdate(String id) {

        log.debug("documentUpdate for ID: ${id}")

        if (request.respondsTo('getFile')) {
            def f = request.getFile('files')
            def originalFilename = f.getOriginalFilename()
            if(originalFilename){
                def extension = FilenameUtils.getExtension(originalFilename)?.toLowerCase()
                if (extension && !grailsApplication.config.upload.extensions.blacklist.contains(extension)){

                    Map document = JSON.parse(params.document)
                    document.role = MDA_DOCUMENT_ROLE

                    def result = documentResourceService .updateDocument(document, f)

                    // This is returned to the browswer as a text response due to workaround the warning
                    // displayed by IE8/9 when JSON is returned from an iframe submit.
                    response.setContentType('text/plain;charset=UTF8')
                    render (result.resp as JSON).toString();
                } else {
                    response.setStatus(SC_BAD_REQUEST)
                    //flag error for extension
                    def error = [error: "Files with the extension '.${extension}' are not permitted.",
                                 statusCode: "400",
                                 detail: "Files with the extension ${extension} are not permitted."] as JSON
                    response.setContentType('text/plain;charset=UTF8')
                    render error.toString()
                }
            } else {
                //flag error for extension
                response.setStatus(SC_BAD_REQUEST)
                def error = [error: "Unable to retrieve the file name.",
                             statusCode: "400",
                             detail: "Unable to retrieve the file name."] as JSON
                response.setContentType('text/plain;charset=UTF8')
                render error.toString()
            }
        } else {
            // This is returned to the browswer as a text response due to workaround the warning
            // displayed by IE8/9 when JSON is returned from an iframe submit.
            Map document = JSON.parse(params.document)
            document.role = MDA_DOCUMENT_ROLE
            def result = documentResourceService.updateDocument(document)

            response.setContentType('text/plain;charset=UTF8')
            def resultAsText = (result as JSON).toString()
            render resultAsText
        }
    }

    /**
     * Proxies to the eco data document controller to delete the document with the supplied id.
     * @param id the id of the document to delete.
     * @return the result of the deletion.
     */
    def documentDelete(String id) {
        def result = documentResourceService .delete(id)
        render status: result.statusCode
    }
}
