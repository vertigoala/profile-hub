package au.org.ala.profile.hub

import au.org.ala.ws.controller.BasicWSController
import static au.org.ala.profile.hub.Utils.enc
import static au.org.ala.profile.hub.Utils.encFragment
import static au.org.ala.profile.hub.Utils.encPath
import static au.org.ala.profile.hub.util.HubConstants.*

class BaseController extends BasicWSController {

    def handle (resp) {
        handleWSResponse resp
    }

    def enabled(feature) {
        return !grailsApplication.config.feature[feature] || grailsApplication.config.feature[feature].toBoolean()
    }

    protected getBannerItems(opus, doMainBanner = false) {
        def model = [
                opusLogoUrl   : opus.opusLayoutConfig?.opusLogoUrl ?: null,
                doMainBanner  : doMainBanner,
                overlayText   : opus.opusLayoutConfig?.bannerOverlayText,
                gradient      : opus.opusLayoutConfig?.gradient,
                gradientWidth : opus.opusLayoutConfig?.gradientWidth,
                duration      : opus.opusLayoutConfig?.duration
        ]

        if (doMainBanner && opus.opusLayoutConfig?.images) {
            model << [ banners: opus.opusLayoutConfig.images,
                       bannerHeight: '500' ]
        } else {
            model << [
                    banners: [
                            [ imageUrl : opus.brandingConfig?.opusBannerUrl ?:
                                    opus?.brandingConfig?.profileBannerUrl ?: DEFAULT_OPUS_BANNER_URL
                            ]
                    ],
                    bannerHeight: opus.brandingConfig?.opusBannerHeight ?:
                            opus.brandingConfig?.profileBannerHeight ?: DEFAULT_OPUS_BANNER_HEIGHT_PX
            ]
        }

        return model
    }

    protected getSearchUrl(opus) {
        "${request.contextPath}/opus/${encPath(opus.shortName ? opus.shortName : opus.uuid)}/search"
    }

    protected getBrowseUrl(opus) {
        "${request.contextPath}/opus/${encPath(opus.shortName ? opus.shortName : opus.uuid)}/browse"
    }

    protected getFilterUrl(opus) {
        "${request.contextPath}/opus/${encPath(opus.shortName ? opus.shortName : opus.uuid)}/filter"
    }

    protected getIdentifyUrl(opus) {
        if(opus.keybaseProjectId != null && opus.keybaseProjectId != ""){
            "${request.contextPath}/opus/${encPath(opus.shortName ? opus.shortName : opus.uuid)}/identify"
        }
    }

    protected getDocumentsUrl(opus) {
        "${request.contextPath}/opus/${encPath(opus.shortName ? opus.shortName : opus.uuid)}/documents"
    }

    protected getReportsUrl(opus) {
        if(params.isOpusAdmin || params.isAlaAdmin){
            "${request.contextPath}/opus/${encPath(opus.shortName ? opus.shortName : opus.uuid)}/reports"
        }
    }

    protected getGlossaryUrl(opus) {
        opus.glossaryUuid ? "${request.contextPath}/opus/${encPath(opus.shortName ? opus.shortName : opus.uuid)}/glossary" : ""
    }

    protected getAboutUrl(opus) {
        "${request.contextPath}/opus/${encPath(opus.shortName ? opus.shortName : opus.uuid)}/about"
    }

    protected getAboutUrl(opus, profile) {
        "${request.contextPath}/opus/${encPath(opus.shortName ? opus.shortName : opus.uuid)}/about#?profile=${encFragment(profile.scientificName)}"
    }

    protected getOpusUrl(opus) {
        if(opus){
            createLink(uri: "/opus/${encPath(opus.shortName ?: opus.uuid)}", absolute: true)
        }
    }

    protected getProfileUrl(opus, profile) {
        if(opus && profile){
            createLink(uri: "/opus/${encPath(opus.shortName ?: opus.uuid)}/profile/${encPath(profile.scientificName)}", absolute: true)
        }
    }
}
