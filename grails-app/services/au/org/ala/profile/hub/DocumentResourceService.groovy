package au.org.ala.profile.hub

import au.org.ala.ws.service.WebService
import org.apache.http.entity.ContentType
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.web.multipart.MultipartFile

class DocumentResourceService {

    WebService webService
    GrailsApplication grailsApplication

    def get(String id) {
        def url = "${grailsApplication.config.profile.service.url}/document/${id}"
        return webService.get(url)
    }

    def delete(String id) {
        def url = "${grailsApplication.config.profile.service.url}/document/${id}"
        return webService.delete(url, [:], ContentType.TEXT_PLAIN)
    }

    def updateDocument(doc) {
        def url = "${grailsApplication.config.profile.service.url}/document/${doc.documentId?:''}"

        return webService.post(url, doc)
    }

    def updateDocument(Map doc, MultipartFile file) {
        def url = grailsApplication.config.profile.baseURL + "/document/${doc.documentId?:''}"
        def result = webService.postMultipart(url, [document:doc], [:], [file])
        return result
    }

    def Map search(Map params) {
        def url = "${grailsApplication.config.profile.service.url}/document/search"
        def resp = webService.post(url, params)
        if (resp && !resp.error) {
            return resp.resp
        }
        return resp
    }
}
