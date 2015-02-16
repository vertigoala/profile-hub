package au.org.ala.profile.hub

import au.org.ala.profile.hub.util.JsonUtil
import au.org.ala.web.AuthService

import javax.annotation.PostConstruct

import static au.org.ala.profile.hub.util.HubConstants.*

class ProfileService {

    def grailsApplication
    BieService bieService
    WebService webService
    AuthService authService

    String profileServiceUrl

    JsonUtil jsonUtil = new JsonUtil()

    ProfileService() {
    }

    @PostConstruct
    def init() {
        profileServiceUrl = grailsApplication.config.profile.service.url
    }

    def getOpus(String opusId = "") {
        jsonUtil.fromUrl("${profileServiceUrl}/opus/${opusId}")
    }

    def getProfile(String profileId) {
        log.debug("Loading profile " + profileId)

        String encodedProfileId = URLEncoder.encode(profileId, "UTF-8")

        def profile = jsonUtil.fromUrl("${profileServiceUrl}/profile/${encodedProfileId}")

        if (!profile) {
            return null
        }

        def opus = getOpus(profile.opusId)

        def query
        if (profile.lsid && profile.lsid != "null") {
            query = "lsid:" + profile.lsid
        } else {
            query = profile.scientificName
        }

        def occurrenceQuery = query

        if (opus.recordSources) {
            occurrenceQuery = query + " AND (data_resource_uid:" + opus.recordSources.join(" OR data_resource_uid:") + ")"
        }

        def imagesQuery = query
        if (opus.imageSources) {
            imagesQuery = query + " AND (data_resource_uid:" + opus.imageSources.join(" OR data_resource_uid:") + ")"
        }

        def classification = getClassification(profile.profileId)

        def speciesProfile = bieService.getSpeciesProfile(profile.profileId)

        [
                occurrenceQuery: occurrenceQuery,
                imagesQuery    : imagesQuery,
                opus           : opus,
                profile        : profile,
                classification : classification,
                speciesProfile : speciesProfile,
                lists          : [],
                logoUrl        : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                bannerUrl      : opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                pageTitle      : opus.title ?: DEFAULT_OPUS_TITLE
        ]
    }

    def getClassification(String profileId) {
        def classification = []

        if (profileId) {
            try {
                classification = jsonUtil.fromUrl("${profileServiceUrl}/classification?profileId=${profileId}")
            } catch (Exception e) {
                log.warn("Unable to load classification for ${profileId}", e)
            }
        }

        classification
    }

    def updateBHLLinks(String profileId, def links) {
        log.debug("Updating BHL links ${links} for profile ${profileId}")

        webService.doPost("${profileServiceUrl}/profile/bhl/${profileId}", [
                profileId      : profileId,
                links          : links,
                userId         : authService.getUserId(),
                userDisplayName: authService.userDetails().userDisplayName
        ])
    }

    def updateLinks(String profileId, def links) {
        log.debug("Updating links ${links} for profile ${profileId}")

        webService.doPost("${profileServiceUrl}/profile/links/${profileId}", [
                profileId      : profileId,
                links          : links,
                userId         : authService.getUserId(),
                userDisplayName: authService.userDetails().userDisplayName
        ])
    }

    def updateAttributes(String profileId, String attributeId, String title, String text) {
        log.debug("Updating attributes ${attributeId} with title ${title} for profile ${profileId}")

        webService.doPost("${profileServiceUrl}/attribute/${attributeId ?: ''}", [
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

        webService.doDelete("${grailsApplication.config.profile.service.url}/attribute/${attributeId}?profileUuid=${profileId}")
    }
}
