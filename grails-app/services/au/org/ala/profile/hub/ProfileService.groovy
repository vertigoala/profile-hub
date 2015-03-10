package au.org.ala.profile.hub

import au.org.ala.web.AuthService

import static au.org.ala.profile.hub.util.HubConstants.*

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

    def getVocab(String vocabId = "") {
        webService.get("${grailsApplication.config.profile.service.url}/vocab/${vocabId}")?.resp
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
                    logoUrl  : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                    bannerUrl: opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                    pageTitle: opus.title ?: DEFAULT_OPUS_TITLE
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

    def getClassification(String guid) {
        log.debug("Retrieving classification for ${guid}")

        webService.get("${grailsApplication.config.profile.service.url}/classification?profileId=${guid}")
    }

    def getSpeciesProfile(String guid) {
        log.debug("Retrieving species profile for ${guid}")

        bieService.getSpeciesProfile(guid)
    }

    def search(String opusId, String scientificName) {
        log.debug("Searching for '${scientificName}' in opus ${opusId}")

        String searchTermEncoded = URLEncoder.encode(scientificName, "UTF-8")
        webService.get("${grailsApplication.config.profile.service.url}/profile/search?opusId=${opusId}&scientificName=${searchTermEncoded}")
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

    def updateAttribute(String profileId, String attributeId, String title, String text) {
        log.debug("Updating attribute ${attributeId} with title ${title} for profile ${profileId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/attribute/${attributeId ?: ''}", [
                title          : title,
                text           : text,
                profileId      : profileId,
                attributeId    : attributeId ?: '',
                userId         : authService.getUserId(),
                userDisplayName: authService.userDetails().userDisplayName
        ])
    }

    def deleteAttribute(String attributeId, String profileId) {
        log.debug("Deleting attribute ${attributeId} of profile ${profileId}")

        webService.doDelete("${grailsApplication.config.profile.service.url}/attribute/${attributeId}?profileId=${profileId}")
    }

    def getAuditHistory(String objectId, String userId) {
        log.debug("Retrieving audit history for ${objectId ?: userId}")

        webService.get("${grailsApplication.config.profile.service.url}/audit/${objectId ? 'object' : 'user'}/${objectId ?: userId}")?.resp
    }
}
