package au.org.ala.profile.hub

import au.org.ala.ws.service.WebService
import org.apache.http.entity.ContentType
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.web.multipart.MultipartFile

import static au.org.ala.profile.hub.Utils.enc

class DocumentResourceService {

    WebService webService
    GrailsApplication grailsApplication

    def get(String documentId) {
        def url = "${grailsApplication.config.profile.service.url}/document/get/${documentId}"
        return webService.get(url)
    }

    def delete(String opusId, String profileId, String documentId) {
        def url = "${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/document/${documentId}"
        return webService.delete(url, [:], ContentType.TEXT_PLAIN)
    }

    def updateDocument(String opusId, String profileId, doc) {

        def url ="${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/document/${doc.documentId ?:''}"

//        def url = "${grailsApplication.config.profile.service.url}/document/${doc.documentId ?:'' + }"

        log.debug("update document url: ${url}")
        return webService.post(url, doc)
    }

    def updateDocument(Map doc, MultipartFile file) {
        def url = grailsApplication.config.profile.baseURL + "/document/${doc.documentId?:''}"
        def result = webService.postMultipart(url, [document:doc], [:], [file])
        return result
    }

    def Map list(String opusId, String profileId) {
        def url ="${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/document/list"

        def resp = webService.get(url)
        if (resp && !resp.error) {
            return resp.resp
        }

        log.debug("resp ${resp}")
        return resp
    }

    def Map search(Map params) {
        def url = "${grailsApplication.config.profile.service.url}/document/search"
        def resp = webService.post(url, params)
        if (resp && !resp.error) {
            return resp.resp
        }

        log.debug("resp ${resp}")
        return resp
    }
}
