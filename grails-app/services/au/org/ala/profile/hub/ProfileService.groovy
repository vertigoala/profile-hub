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

    def updateOpusUsers(String opusId, json) {
        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${opusId}/updateUsers", [authorities: json])
    }

    def createOpus(json) {
        webService.doPut("${grailsApplication.config.profile.service.url}/opus/", json)
    }

    def deleteOpus(String opusId) {
        log.debug("Deleting opus ${opusId}")
        webService.doDelete("${grailsApplication.config.profile.service.url}/opus/${opusId}")
    }

    def getVocab(String opusId, String vocabId = "") {
        webService.get("${grailsApplication.config.profile.service.url}/opus/${opusId}/vocab/${vocabId}")
    }

    def createProfile(json) {
        webService.doPut("${grailsApplication.config.profile.service.url}/profile/", json)
    }

    def updateProfile(String profileId, json) {
        webService.doPost("${grailsApplication.config.profile.service.url}/profile/${profileId}", json)
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

    def getPublications(String profileId) {
        log.debug("Retrieving publications for ${profileId}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/${profileId}/publication")
    }

    def savePublication(String profileId, publication, file) {
        log.debug("Saving publication for profile ${profileId}")

        webService.postMultipart("${grailsApplication.config.profile.service.url}/profile/${profileId}/publication", publication, [file])
    }

    def deletePublication(String profileId, String publicationId) {
        log.debug("Deleting publication ${publicationId}")

        webService.doDelete("${grailsApplication.config.profile.service.url}/profile/${profileId}/publication/${publicationId}/delete")
    }

    def getClassification(String opusId, String profileId, String guid) {
        log.debug("Retrieving classification for ${guid} in opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/${profileId}/classification?guid=${guid}&opusId=${opusId}")
    }

    def getSpeciesProfile(String guid) {
        log.debug("Retrieving species profile for ${guid}")

        bieService.getSpeciesProfile(guid)
    }

    def findByScientificName(String opusId, String scientificName, boolean useWildcard) {
        log.debug("Searching for '${scientificName}' in opus ${opusId}")

        String searchTermEncoded = URLEncoder.encode(scientificName, "UTF-8")
        webService.get("${grailsApplication.config.profile.service.url}/profile/search?opusId=${opusId}&scientificName=${searchTermEncoded}&useWildcard=${useWildcard}")
    }

    def findByNameAndTaxonLevel(String opusId, String taxon, String scientificName, String max, String offset, boolean wildcard) {
        log.debug("Searching for '${scientificName}' in taxon ${taxon}")

        String scientificNameEncoded = URLEncoder.encode(scientificName, "UTF-8")
        String taxonNameEncoded = URLEncoder.encode(taxon, "UTF-8")
        webService.get("${grailsApplication.config.profile.service.url}/profile/search/taxon/name?opusId=${opusId}&scientificName=${scientificNameEncoded}&taxon=${taxonNameEncoded}&useWildcard=${wildcard}&max=${max}&offset=${offset}")
    }

    def groupByTaxonLevel(String opusId, String taxon, String max, String offset) {
        log.debug("Searching for '${taxon}' level")

        String taxonNameEncoded = URLEncoder.encode(taxon, "UTF-8")
        webService.get("${grailsApplication.config.profile.service.url}/profile/search/taxon/level?opusId=${opusId}&taxon=${taxonNameEncoded}&max=${max}&offset=${offset}")
    }

    def getTaxonLevels(String opusId) {
        log.debug("Getting taxon levels for opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/search/taxon/levels?opusId=${opusId}")
    }

    def updateBHLLinks(String profileId, def links) {
        log.debug("Updating BHL links ${links} for profile ${profileId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/profile/${profileId}/bhl", [
                profileId      : profileId,
                links          : links,
                userId         : authService.getUserId(),
                userDisplayName: authService.userDetails().userDisplayName
        ])
    }

    def updateLinks(String profileId, def links) {
        log.debug("Updating links ${links} for profile ${profileId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/profile/${profileId}/links", [
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

        webService.doPost("${grailsApplication.config.profile.service.url}/profile/${profileId}/attribute/${attribute.uuid ?: ''}", attribute)
    }

    def deleteAttribute(String attributeId, String profileId) {
        log.debug("Deleting attribute ${attributeId} of profile ${profileId}")

        webService.doDelete("${grailsApplication.config.profile.service.url}/profile/${profileId}/attribute/${attributeId}?profileId=${profileId}")
    }

    def getAuditHistory(String objectId, String userId) {
        log.debug("Retrieving audit history for ${objectId ?: userId}")

        webService.get("${grailsApplication.config.profile.service.url}/audit/${objectId ? 'object' : 'user'}/${objectId ?: userId}")
    }

    def updateVocabulary(String opusId, String vocabId, vocab) {
        log.debug("Updating vocabulary ${vocabId} with data ${vocab}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${opusId}/vocab/${vocabId}", vocab)
    }

    def findUsagesOfVocabTerm(String opusId, String vocabId, String termName) {
        log.debug("Finding usages of term ${termName} from vocab ${vocabId}")

        webService.get("${grailsApplication.config.profile.service.url}/opus/${opusId}/vocab/usages/find?vocabId=${vocabId}&term=${URLEncoder.encode(termName, "UTF-8")}")
    }

    def replaceUsagesOfVocabTerm(String opusId, Map json) {
        log.debug("Replacing usages of vocab term(s): ${json}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${opusId}/vocab/usages/replace", json)
    }

    def getGlossary(String opusId) {
        log.debug("Fetching glossary for opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/opus/${opusId}/glossary/${opusId}")
    }

    def uploadGlossary(String opusId, String glossaryId, List items) {
        log.debug("Uploading glossary items for opus ${opusId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${opusId}/glossary", [opusId: opusId, glossaryId: glossaryId, items: items])
    }

    def updateGlossaryItem(String opusId, String glossaryItemId, Map data) {
        log.debug("Updating glossary item ${glossaryItemId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${opusId}/glossary/item/${glossaryItemId}", data)
    }

    def createGlossaryItem(String opusId, Map data) {
        log.debug("Creating glossary item for opus ${opusId}")

        webService.doPut("${grailsApplication.config.profile.service.url}/opus/${opusId}/glossary/item", data)
    }

    def deleteGlossaryItem(String opusId, String glossaryItemId) {
        log.debug("Deleting glossary item ${glossaryItemId}")

        webService.doDelete("${grailsApplication.config.profile.service.url}/opus/${opusId}/glossary/item/${glossaryItemId}")
    }

    def getComments(String profileId) {
        log.debug("Fetching comments for profile ${profileId}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/${profileId}/comment/")
    }

    def getComment(String profileId, String commentId) {
        log.debug("Fetching comment ${commentId} for profile ${profileId}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/${profileId}/comment/${commentId}")
    }

    def addComment(String profileId, Map json) {
        log.debug("Adding comment to profile ${profileId}")

        webService.doPut("${grailsApplication.config.profile.service.url}/profile/${profileId}/comment/", json)
    }

    def updateComment(String profileId, String commentId, Map json) {
        log.debug("Updateing comment ${commentId} for profile ${profileId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/profile/${profileId}/comment/${commentId}", json)
    }

    def deleteComment(String profileId, String commentId) {
        log.debug("Deleting comment ${commentId}")

        webService.doDelete("${grailsApplication.config.profile.service.url}/profile/${profileId}/comment/${commentId}")
    }

    def updateAuthorship(String profileId, Map json) {
        log.debug("Updating authorship for profile ${profileId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/profile/${profileId}/authorship", json)
    }
}
