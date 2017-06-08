package au.org.ala.profile.hub

import au.org.ala.ws.controller.BasicWSController
import static au.org.ala.profile.hub.Utils.enc

class BaseController extends BasicWSController {

    def handle (resp) {
        handleWSResponse resp
    }

    def enabled(feature) {
        return !grailsApplication.config.feature[feature] || grailsApplication.config.feature[feature].toBoolean()
    }

    protected getSearchUrl(opus) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/search"
    }

    protected getBrowseUrl(opus) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/browse"
    }

    protected getFilterUrl(opus) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/filter"
    }

    protected getIdentifyUrl(opus) {
        if(opus.keybaseProjectId != null && opus.keybaseProjectId != ""){
            "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/identify"
        }
    }

    protected getDocumentsUrl(opus) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/documents"
    }

    protected getReportsUrl(opus) {
        if(params.isOpusAdmin || params.isAlaAdmin){
            "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/reports"
        }
    }

    protected getGlossaryUrl(opus) {
        opus.glossaryUuid ? "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/glossary" : ""
    }

    protected getAboutUrl(opus) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/about"
    }

    protected getAboutUrl(opus, profile) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/about#?profile=${profile.scientificName}"
    }

    protected getOpusUrl(opus) {
        if(opus){
            createLink(uri: "/opus/${opus.shortName ?: opus.uuid}", absolute: true)
        }
    }


    protected getProfileUrl(opus, profile) {
        if(opus && profile){
            "${createLink(uri: "/opus/${opus.shortName ?: opus.uuid}/profile/${enc(profile.scientificName)}", absolute: true)}"
        }
    }
}
