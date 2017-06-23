package au.org.ala.profile.hub

import static au.org.ala.profile.hub.util.HubConstants.DEFAULT_OPUS_BANNER_URL
import static au.org.ala.profile.hub.util.HubConstants.DEFAULT_OPUS_LOGOS

class OpusBaseController extends BaseController {

    protected def commonViewModelParams(def opus, String pageName = '', String pageTitle = '',
                                        def doMainBanner = false) {
        def florulaListId = authService.userId ? opus.florulaListId : florulaCookieService.getFlorulaListIdForOpusId(request, opus.uuid)
        def model = [
                opus            : opus,
                logos           : opus.brandingConfig?.logos ?: DEFAULT_OPUS_LOGOS,
                bannerUrl       : opus.brandingConfig?.opusBannerUrl ?: opus.brandingConfig?.profileBannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                footerText      : opus.footerText,
                contact         : opus.contact,
                bannerItems     : getBannerItems(opus, doMainBanner, false),
                opusUrl         : getOpusUrl(opus),
                searchUrl       : getSearchUrl(opus),
                browseUrl       : getBrowseUrl(opus),
                filterUrl       : getFilterUrl(opus),
                identifyUrl     : getIdentifyUrl(opus),
                documentsUrl    : getDocumentsUrl(opus),
                reportsUrl      : getReportsUrl(opus),
                glossaryUrl     : getGlossaryUrl(opus),
                aboutPageUrl    : getAboutUrl(opus),
                currentUser     : authService.getDisplayName(),
                opusTitle       : opus.title,
                hasFilter       : florulaListId,
                florulaListId   : florulaListId
        ]

        if (pageName) model += [pageName: pageName, helpLink: getHelpLink(pageName, opus)]
        if (pageTitle) model += [pageTitle: pageTitle]

        return model
    }

    private String getHelpLink(String pageName, Map opus){
        switch (pageName){
            case 'about':
                return opus?.help?.aboutLink
            case 'glossary':
                return opus?.help?.glossaryLink
            case 'search':
                return opus?.help?.searchLink
            case 'browse':
                return opus?.help?.browseLink
            case 'identify':
                return opus?.help?.identifyLink
            case 'filter':
                return opus?.help?.filterLink
            case 'documents':
                return opus?.help?.documentsLink
            case 'reports':
                return opus?.help?.reportsLink
            case 'opus':
                return opus?.help?.opusLink
        }
    }

}
