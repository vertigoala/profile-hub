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
            String serverPort = "";
            if(!((request.scheme in ['http', 'https']) && (request.serverPort in [80, 443]))){
                serverPort =  ":" + request.serverPort
            }

            "${request.scheme}://${request.serverName}${serverPort}${request.contextPath}/opus/${opus.uuid}/profile/${profile.uuid}"
        }
    }
}
