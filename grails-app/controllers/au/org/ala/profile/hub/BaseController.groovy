package au.org.ala.profile.hub

import au.org.ala.ws.controller.BasicWSController

class BaseController extends BasicWSController {

    def handle (resp) {
        handleWSResponse resp
    }

    def enabled(feature) {
        return !grailsApplication.config.feature[feature] || grailsApplication.config.feature[feature].toBoolean()
    }

    private getSearchUrl(opus) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/search"
    }

    private getBrowseUrl(opus) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/browse"
    }

    private getIdentifyUrl(opus) {
        if(opus.keybaseProjectId != null && opus.keybaseProjectId != ""){
            "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/identify"
        }
    }

    private getDocumentsUrl(opus) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/documents"
    }

    private getReportsUrl(opus) {
        if(params.isOpusAdmin || params.isAlaAdmin){
            "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/reports"
        }
    }

    private getGlossaryUrl(opus) {
        opus.glossaryUuid ? "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/glossary" : ""
    }

    private getAboutUrl(opus) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/about"
    }

    private getAboutUrl(opus, profile) {
        "${request.contextPath}/opus/${opus.shortName ? opus.shortName : opus.uuid}/about#?profile=${profile.scientificName}"
    }

    private getProfileUrl(opus, profile) {
        if(opus && profile){
            "${createLink(uri: "/opus/${opus.uuid}/profile/${profile.uuid}", absolute: true)}"
        }
    }
}
