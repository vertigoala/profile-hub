package au.org.ala.profile.hub

import au.org.ala.profile.hub.util.HubConstants
import au.org.ala.profile.hub.util.ReportType
import au.org.ala.web.AuthService

class ProfileService {

    def grailsApplication
    BieService bieService
    WebService webService
    AuthService authService
    KeybaseService keybaseService
    UtilService utilService

    def getOpus(String opusId = "") {
        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}")?.resp
    }

    def updateOpus(String opusId, json) {
        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}", json)
    }

    def updateOpusUsers(String opusId, json) {
        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/updateUsers", [authorities: json])
    }

    def createOpus(json) {
        webService.doPut("${grailsApplication.config.profile.service.url}/opus/", json)
    }

    def deleteOpus(String opusId) {
        log.debug("Deleting opus ${opusId}")
        webService.doDelete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}")
    }

    def getOpusAboutPage(String opusId) {
        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/about")
    }

    def updateOpusAboutPage(String opusId, String html) {
        webService.doPut("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/about", [opusId: opusId, aboutHtml: html])
    }

    def getVocab(String opusId, String vocabId = "") {
        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/vocab/${enc(vocabId)}")
    }

    def createProfile(String opusId, json) {
        webService.doPut("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/", json)
    }

    def updateProfile(String opusId, String profileId, json, boolean latest = false) {
        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}?latest=${latest}", json)
    }

    def toggleDraftMode(String opusId, String profileId) {
        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/toggleDraftMode", null)
    }

    def discardDraftChanges(String opusId, String profileId) {
        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/discardDraftChanges", null)
    }

    def getList(String drid) {
        webService.get("${grailsApplication.config.lists.base.url}/ws/speciesListItems/${drid}?includeKVP=true")?.resp
    }

    def getPublications(String pubId) {
        webService.get("${grailsApplication.config.profile.service.url}/publication/${enc(pubId)}")?.resp
    }

    def getProfile(String opusId, String profileId, boolean latest = false) {
        log.debug("Loading profile " + profileId)

        Map result

        try {
            String encodedProfileId = URLEncoder.encode(profileId, "UTF-8")
            def profile = webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${encodedProfileId}?latest=${latest}")?.resp

            if (!profile) {
                return null
            }

            injectThumbnailUrls(profile)

            def opus = getOpus(opusId)

            result = [
                    opus     : opus,
                    profile  : profile,
                    logoUrl  : opus.logoUrl ?: HubConstants.DEFAULT_OPUS_LOGO_URL,
                    bannerUrl: opus.bannerUrl ?: HubConstants.DEFAULT_OPUS_BANNER_URL,
                    pageTitle: opus.title ?: HubConstants.DEFAULT_OPUS_TITLE
            ]

            profile.keybaseKey = keybaseService.findKeyForTaxon(profile.classification, opus.keybaseProjectId)

        } catch (FileNotFoundException e) {
            log.error("Profile ${profileId} not found")
            result = null
        } catch (Exception e) {
            log.error("Failed to retrieve profile ${profileId}", e)
            result = [error: "Failed to retrieve profile ${profileId} due to ${e.getMessage()}"]
        }

        result
    }

    def renameProfile(String opusId, String profileId, Map json) {
        log.debug("Renaming profile ${profileId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/rename", json)
    }

    def deleteProfile(String opusId, String profileId) {
        log.debug("Deleting profile ${profileId}")
        webService.doDelete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}")
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

    def recordStagedImage(String opusId, String profileId, Map metadata) {
        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/recordStagedImage", metadata)
    }

    def getPublications(String opusId, String profileId) {
        log.debug("Retrieving publications for ${profileId}")

        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/publication")
    }

    def savePublication(String opusId, String profileId, file) {
        log.debug("Saving publication for profile ${profileId}")

        webService.postMultipart("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/publication", [:], [file])
    }

    def deletePublication(String opusId, String profileId, String publicationId) {
        log.debug("Deleting publication ${publicationId}")

        webService.doDelete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/publication/${enc(publicationId)}/delete")
    }

    def getClassification(String opusId, String profileId, String guid) {
        log.debug("Retrieving classification for ${guid} in opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/classification?guid=${enc(guid)}&opusId=${enc(opusId)}")
    }

    def getSpeciesProfile(String guid) {
        log.debug("Retrieving species profile for ${guid}")

        bieService.getSpeciesProfile(guid)
    }

    def findByScientificName(String opusId, String scientificName, String max, boolean useWildcard) {
        log.debug("Searching for '${scientificName}' in opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/search?opusId=${enc(opusId)}&scientificName=${enc(scientificName)}&max=${max ?: ""}&useWildcard=${useWildcard}")
    }

    def findByNameAndTaxonLevel(String opusId, String taxon, String scientificName, String max, String offset, boolean wildcard) {
        log.debug("Searching for '${scientificName}' in taxon ${taxon}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/search/taxon/name?opusId=${enc(opusId)}&scientificName=${enc(scientificName)}&taxon=${enc(taxon)}&useWildcard=${wildcard}&max=${max}&offset=${offset}")
    }

    def groupByTaxonLevel(String opusId, String taxon, String max, String offset) {
        log.debug("Searching for '${taxon}' level")

        webService.get("${grailsApplication.config.profile.service.url}/profile/search/taxon/level?opusId=${enc(opusId)}&taxon=${enc(taxon)}&max=${max}&offset=${offset}")
    }

    def getTaxonLevels(String opusId) {
        log.debug("Getting taxon levels for opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/search/taxon/levels?opusId=${enc(opusId)}")
    }

    def updateBHLLinks(String opusId, String profileId, def links) {
        log.debug("Updating BHL links ${links} for profile ${profileId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/bhl", [
                profileId      : profileId,
                links          : links,
                userId         : authService.getUserId(),
                userDisplayName: authService.userDetails().userDisplayName
        ])
    }

    def updateLinks(String opusId, String profileId, def links) {
        log.debug("Updating links ${links} for profile ${profileId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/links", [
                profileId      : profileId,
                links          : links,
                userId         : authService.getUserId(),
                userDisplayName: authService.userDetails().userDisplayName
        ])
    }

    def updateAttribute(String opusId, String profileId, Map attribute) {
        log.debug("Updating attribute ${attribute.uuid} with title ${attribute.title} for profile ${profileId}")

        attribute.profileId = profileId
        attribute.userId = authService.getUserId()
        attribute.userDisplayName = authService.userDetails().userDisplayName

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/attribute/${enc(attribute.uuid ?: '')}", attribute)
    }

    def deleteAttribute(String opusId, String attributeId, String profileId) {
        log.debug("Deleting attribute ${attributeId} of profile ${profileId}")

        webService.doDelete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/attribute/${enc(attributeId)}?profileId=${enc(profileId)}")
    }

    def getAuditHistory(String objectId, String userId) {
        log.debug("Retrieving audit history for ${objectId ?: userId}")

        webService.get("${grailsApplication.config.profile.service.url}/audit/${objectId ? 'object' : 'user'}/${enc(objectId ?: userId)}")
    }

    def updateVocabulary(String opusId, String vocabId, vocab) {
        log.debug("Updating vocabulary ${vocabId} with data ${vocab}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/vocab/${enc(vocabId)}", vocab)
    }

    def findUsagesOfVocabTerm(String opusId, String vocabId, String termName) {
        log.debug("Finding usages of term ${termName} from vocab ${vocabId}")

        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/vocab/usages/find?vocabId=${enc(vocabId)}&term=${URLEncoder.encode(termName, "UTF-8")}")
    }

    def replaceUsagesOfVocabTerm(String opusId, Map json) {
        log.debug("Replacing usages of vocab term(s): ${json}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/vocab/usages/replace", json)
    }

    def getGlossary(String opusId) {
        log.debug("Fetching glossary for opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/glossary/${enc(opusId)}")
    }

    def uploadGlossary(String opusId, String glossaryId, List items) {
        log.debug("Uploading glossary items for opus ${opusId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/glossary", [opusId: opusId, glossaryId: glossaryId, items: items])
    }

    def updateGlossaryItem(String opusId, String glossaryItemId, Map data) {
        log.debug("Updating glossary item ${glossaryItemId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/glossary/item/${enc(glossaryItemId)}", data)
    }

    def createGlossaryItem(String opusId, Map data) {
        log.debug("Creating glossary item for opus ${opusId}")

        webService.doPut("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/glossary/item", data)
    }

    def deleteGlossaryItem(String opusId, String glossaryItemId) {
        log.debug("Deleting glossary item ${glossaryItemId}")

        webService.doDelete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/glossary/item/${enc(glossaryItemId)}")
    }

    def getComments(String opusId, String profileId) {
        log.debug("Fetching comments for profile ${profileId}")

        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/comment/")
    }

    def getComment(String opusId, String profileId, String commentId) {
        log.debug("Fetching comment ${commentId} for profile ${profileId}")

        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/comment/${enc(commentId)}")
    }

    def addComment(String opusId, String profileId, Map json) {
        log.debug("Adding comment to profile ${profileId}")

        webService.doPut("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/comment/", json)
    }

    def updateComment(String opusId, String profileId, String commentId, Map json) {
        log.debug("Updateing comment ${commentId} for profile ${profileId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/comment/${enc(commentId)}", json)
    }

    def deleteComment(String opusId, String profileId, String commentId) {
        log.debug("Deleting comment ${commentId}")

        webService.doDelete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/comment/${enc(commentId)}")
    }

    def updateAuthorship(String opusId, String profileId, Map json) {
        log.debug("Updating authorship for profile ${profileId}")

        webService.doPost("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/authorship", json)
    }

    def checkName(String opusId, String scientificName) {
        log.debug("Checking name ${scientificName}")

        webService.get("${grailsApplication.config.profile.service.url}/checkName?opusId=${enc(opusId)}&scientificName=${enc(scientificName)}")
    }

    def loadReport(String opusId, String reportId, String pageSize, String offset, Map dates) {
        ReportType report = ReportType.byId(reportId)
        Map range
        def resp
        switch (report) {
            case ReportType.MISMATCHED_NAME:
                resp = webService.get("${grailsApplication.config.profile.service.url}/report/mismatchedNames?opusId=${enc(opusId)}&offset=${offset}&max=${pageSize}")
                break;
            case ReportType.DRAFT_PROFILE:
                resp = webService.get("${grailsApplication.config.profile.service.url}/report/draftProfiles?opusId=${enc(opusId)}")
                break;
            case ReportType.MOST_RECENT_CHANGE:
                range = utilService.getDateRange(dates.period, dates.from, dates.to);
                resp = webService.get("${grailsApplication.config.profile.service.url}/report/mostRecentChange?opusId=${enc(opusId)}&to=${enc(range['to'])}&from=${enc(range['from'])}&offset=${offset}&max=${pageSize}")
                break;
        }

        resp
    }

    def getBioStatus(String opusId, String profileId) {
        def model = getProfile(opusId, profileId);
        def opus = model.opus;
        def profile = model.profile;
        List result = []
        if (opus.bioStatusLists?.size()) {
            opus.bioStatusLists.each({
                result.addAll(getProfileKVP(profile.scientificName, it));
            })
        }

        result
    }

    def getProfileKVP(String profileId, String drid) {
        List result = []
        def list = getList(drid);
        if (list) {
            list.each({
                if (it.name.toLowerCase() == profileId.toLowerCase()) {
                    result.addAll(it.kvpValues);
                }
            })
        }

        result
    }

    def getPublicationJson(String pubId){
        getPublications(pubId)
    }

    def enc(String value) {
        URLEncoder.encode(value, "UTF-8")
    }
}
