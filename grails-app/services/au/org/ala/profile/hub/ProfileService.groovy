package au.org.ala.profile.hub

import au.org.ala.profile.hub.util.HubConstants
import au.org.ala.web.AuthService

class ProfileService {

    def grailsApplication
    BieService bieService
    WebService webService
    AuthService authService

    def getOpus(String opusId = "") {
        webService.get("${grailsApplication.config.profile.service.url}/opus/${opusId}")?.resp
    }

    def updateOpus(String opusId, json) {
        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${opusId}", json)
    }

    def createOpus(json) {
        webService.doPut("${grailsApplication.config.profile.service.url}/opus/", json)
    }

    def deleteOpus(String opusId) {
        log.debug("Deleting opus ${opusId}")
        webService.doDelete("${grailsApplication.config.profile.service.url}/opus/${opusId}")
    }

    def getVocab(String vocabId = "") {
        webService.get("${grailsApplication.config.profile.service.url}/vocab/${vocabId}")
    }

    def createProfile(json) {
        webService.doPut("${grailsApplication.config.profile.service.url}/profile/", json)
    }

    def getProfile(String profileId) {
        log.debug("Loading profile " + profileId)

        Map result

        try {
            String encodedProfileId = URLEncoder.encode(profileId, "UTF-8")
            def profile = webService.get("${grailsApplication.config.profile.service.url}/profile/${encodedProfileId}")?.resp

            if (!profile) {
                return null
            }

            injectThumbnailUrls(profile)

            def opus = getOpus(profile.opusId)

            result = [
                    opus     : opus,
                    profile  : profile,
                    logoUrl  : opus.logoUrl ?: HubConstants.DEFAULT_OPUS_LOGO_URL,
                    bannerUrl: opus.bannerUrl ?: HubConstants.DEFAULT_OPUS_BANNER_URL,
                    pageTitle: opus.title ?: HubConstants.DEFAULT_OPUS_TITLE
            ]
        } catch (FileNotFoundException e) {
            log.error("Profile ${profileId} not found")
            result = null
        } catch (Exception e) {
            log.error("Failed to retrieve profile ${profileId}", e)
            result = [error: "Failed to retrieve profile ${profileId} due to ${e.getMessage()}"]
        }

        result
    }

    def deleteProfile(String profileId) {
        log.debug("Deleting profile ${profileId}")
        webService.doDelete("${grailsApplication.config.profile.service.url}/profile/${profileId}")
    }

    void injectThumbnailUrls(profile) {
        profile.bhl.each({
            String pageId = it.url.split("/").last()
            if (pageId =~ /\?#/) {
                pageId = pageId.split(/\?#/).first()
            }
            it.thumbnailUrl = "${grailsApplication.config.biodiv.library.thumb.url}${pageId}"
        })
        profile
    }

    def getClassification(String guid, String opusId) {
        log.debug("Retrieving classification for ${guid} in opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/classification?guid=${guid}&opusId=${opusId}")
    }

    def getSpeciesProfile(String guid) {
        log.debug("Retrieving species profile for ${guid}")

        bieService.getSpeciesProfile(guid)
    }

    def search(String opusId, String scientificName, boolean useWildcard) {
        log.debug("Searching for '${scientificName}' in opus ${opusId}")

        String searchTermEncoded = URLEncoder.encode(scientificName, "UTF-8")
        webService.get("${grailsApplication.config.profile.service.url}/profile/search?opusId=${opusId}&scientificName=${searchTermEncoded}&useWildcard=${useWildcard}")
    }

    def updateBHLLinks(String profileId, def links) {
        log.debug("Updating BHL links ${links} for profile ${profileId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/profile/bhl/${profileId}", [
                profileId      : profileId,
                links          : links,
                userId         : authService.getUserId(),
                userDisplayName: authService.userDetails().userDisplayName
        ])
    }

    def updateLinks(String profileId, def links) {
        log.debug("Updating links ${links} for profile ${profileId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/profile/links/${profileId}", [
                profileId      : profileId,
                links          : links,
                userId         : authService.getUserId(),
                userDisplayName: authService.userDetails().userDisplayName
        ])
    }

    def updateAttribute(String profileId, Map attribute) {
        log.debug("Updating attribute ${attribute.uuid} with title ${attribute.title} for profile ${profileId}")

        attribute.profileId = profileId
        attribute.userId = authService.getUserId()
        attribute.userDisplayName = authService.userDetails().userDisplayName

        webService.doPost("${grailsApplication.config.profile.service.url}/attribute/${attribute.uuid ?: ''}", attribute)
    }

    def deleteAttribute(String attributeId, String profileId) {
        log.debug("Deleting attribute ${attributeId} of profile ${profileId}")

        webService.doDelete("${grailsApplication.config.profile.service.url}/attribute/${attributeId}?profileId=${profileId}")
    }

    def getAuditHistory(String objectId, String userId) {
        log.debug("Retrieving audit history for ${objectId ?: userId}")

        webService.get("${grailsApplication.config.profile.service.url}/audit/${objectId ? 'object' : 'user'}/${objectId ?: userId}")
    }

    def updateVocabulary(String vocabId, vocab) {
        log.debug("Updating vocabulary ${vocabId} with data ${vocab}")

        webService.doPost("${grailsApplication.config.profile.service.url}/vocab/${vocabId}", vocab)
    }

    def findUsagesOfVocabTerm(String vocabId, String termName) {
        log.debug("Finding usages of term ${termName} from vocab ${vocabId}")

        webService.get("${grailsApplication.config.profile.service.url}/vocab/usages/find?vocabId=${vocabId}&term=${termName}")
    }

    def replaceUsagesOfVocabTerm(Map json) {
        log.debug("Replacing usages of vocab term(s): ${json}")

        webService.doPost("${grailsApplication.config.profile.service.url}/vocab/usages/replace", json)
    }
}
