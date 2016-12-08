package au.org.ala.profile.hub

import org.apache.http.entity.ContentType
import org.springframework.web.multipart.MultipartFile

import static au.org.ala.profile.hub.Utils.enc
import au.org.ala.profile.hub.util.ReportType
import au.org.ala.web.AuthService
import au.org.ala.ws.service.WebService
import org.apache.commons.lang.BooleanUtils
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest

import javax.servlet.http.HttpServletResponse

import static au.org.ala.profile.hub.util.HubConstants.*

class ProfileService {

    def grailsApplication
    BieService bieService
    WebService webService
    AuthService authService
    UtilService utilService

    def getOpus(String opusId = "") {
        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}")?.resp
    }

    def updateOpus(String opusId, Map json) {
        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}", json)
    }

    def updateSupportingCollections(String opusId, Map json) {
        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/supportingCollections/update", json)
    }

    def respondToSupportingCollectionRequest(String opusId, String requestingOpusId, String action) {
        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/supportingCollections/respond/${requestingOpusId}/${action}", [:])
    }

    def updateOpusUsers(String opusId, Map json) {
        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/updateUsers", json)
    }

    def getUserDetails(String opusId) {
        webService.get("${grailsApplication.config.profile.service.url}/user/details?opusId=${enc(opusId)}")?.resp
    }

    def createOpus(json) {
        webService.put("${grailsApplication.config.profile.service.url}/opus/", json)
    }

    def deleteOpus(String opusId) {
        log.debug("Deleting opus ${opusId}")
        webService.delete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}")
    }

    def getOpusAboutContent(String opusId) {
        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/about")
    }

    def generateAccessTokenForOpus(String opusId) {
        webService.put("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/access/token", [:])
    }

    def revokeAccessTokenForOpus(String opusId) {
        webService.delete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/access/token")
    }

    def updateOpusAboutContent(String opusId, String aboutHtml, String citationHtml) {
        webService.put("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/about", [opusId: opusId, aboutHtml: aboutHtml, citationHtml: citationHtml])
    }

    def getVocab(String opusId, String vocabId = "") {
        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/vocab/${enc(vocabId)}")
    }

    def createProfile(String opusId, json) {
        webService.put("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/", json)
    }

    def duplicateProfile(String opusId, String profileId, Map json) {
        webService.put("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${profileId}/duplicate", json)
    }

    def updateProfile(String opusId, String profileId, json, boolean latest = false) {
        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}?latest=${latest}", json)
    }

    def toggleDraftMode(String opusId, String profileId, boolean publish = false) {
        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/toggleDraftMode?publish=${publish}", null)
    }

    def discardDraftChanges(String opusId, String profileId) {
        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/discardDraftChanges", null)
    }

    def getListMetadata(String drid) {
        webService.get("${grailsApplication.config.lists.base.url}/ws/speciesList/${drid}")?.resp
    }

    def getListItems(String drid) {
        webService.get("${grailsApplication.config.lists.base.url}/ws/speciesListItems/${drid}?includeKVP=true")?.resp
    }

    def getPublications(String pubId) {
        webService.get("${grailsApplication.config.profile.service.url}/publication/${enc(pubId)}")?.resp
    }

    def getProfile(String opusId, String profileId, boolean latest = false, Boolean fullClassification = false) {
        log.debug("Loading profile " + profileId)

        Map result

        try {
            String encodedProfileId = URLEncoder.encode(profileId, "UTF-8")
            def profile = webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${encodedProfileId}?latest=${latest}&fullClassification=${fullClassification}")?.resp

            if (!profile) {
                return null
            }

            injectThumbnailUrls(profile)

            def opus = getOpus(opusId)

            result = [
                    opus         : opus,
                    profile      : profile,
                    logoUrl      : opus.brandingConfig?.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                    bannerUrl    : opus.brandingConfig?.profileBannerUrl ?: opus.brandingConfig?.opusBannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                    bannerHeight: opus.brandingConfig?.profileBannerHeight ?: opus.brandingConfig?.opusBannerHeight ?: DEFAULT_OPUS_BANNER_HEIGHT_PX,
                    pageTitle    : opus.title ?: DEFAULT_OPUS_TITLE
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

    def renameProfile(String opusId, String profileId, Map json) {
        log.debug("Renaming profile ${profileId}")

        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/rename", json)
    }

    def deleteProfile(String opusId, String profileId) {
        log.debug("Deleting profile ${profileId}")
        webService.delete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}")
    }

    def archiveProfile(String opusId, String profileId, String archiveComment) {
        log.debug("Archiving profile ${profileId}")

        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/archive/${enc(profileId)}", [archiveComment: archiveComment])
    }

    def restoreArchivedProfile(String opusId, String profileId, String newName = null) {
        log.debug("Restoring archived profile ${profileId}")

        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/restore/${enc(profileId)}", [newName: newName])
    }

    def saveAttachment(String opusId, String profileId, Map metadata, DefaultMultipartHttpServletRequest request) {
        List files = request.getFileNames().collect { request.getFile(it) }

        if (profileId) {
            webService.postMultipart("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/attachment?latest=true", [data: metadata], null, files)
        } else {
            webService.postMultipart("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/attachment", [data: metadata], null, files)
        }
    }

    def getAttachmentMetadata(String opusId, String profileId = null, String attachmentId = null, boolean latest = false) {
        if (profileId) {
            webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/attachment/${enc(attachmentId)}?latest=${latest}")
        } else {
            webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/attachment/${enc(attachmentId)}")
        }
    }

    def deleteAttachment(String opusId, String profileId, String attachmentId) {
        if (profileId) {
            webService.delete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/attachment/${enc(attachmentId)}?latest=true")
        } else {
            webService.delete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/attachment/${enc(attachmentId)}")
        }
    }

    def getImageMetadata(String imageId) {
        webService.get("${grailsApplication.config.profile.service.url}/image/${imageId}")
    }

    void injectThumbnailUrls(profile) {
        profile.bhl.each {
            if (it) {
                String pageId = it.url.split("/").last()
                if (pageId =~ /\?#/) {
                    pageId = pageId.split(/\?#/).first()
                }
                it.thumbnailUrl = "${grailsApplication.config.biodiv.library.thumb.url}${pageId}"
            }
        }

        profile
    }

    def recordStagedImage(String opusId, String profileId, Map metadata) {
        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/recordStagedImage", metadata)
    }

    def recordPrivateImage(String opusId, String profileId, Map metadata) {
        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/recordPrivateImage", metadata)
    }

    def getPublications(String opusId, String profileId) {
        log.debug("Retrieving publications for ${profileId}")

        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/publication")
    }

    def savePublication(String opusId, String profileId, file) {
        log.debug("Saving publication for profile ${profileId}")

        webService.postMultipart("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/publication", [:], null, [file])
    }

    def proxyGetPublicationFile(HttpServletResponse response, String opusId, String profileId, String publicationId) {
        log.debug("Proxying publication $publicationId")

        webService.proxyGetRequest(response, "${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/publication/${enc(publicationId)}/file")
    }

    def deletePublication(String opusId, String profileId, String publicationId) {
        log.debug("Deleting publication ${publicationId}")

        webService.delete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/publication/${enc(publicationId)}/delete")
    }

    def getClassification(String opusId, String profileId, String guid) {
        log.debug("Retrieving classification for ${guid} in opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/classification?guid=${enc(guid)}&opusId=${enc(opusId)}")
    }

    def getSpeciesProfile(String guid) {
        log.debug("Retrieving species profile for ${guid}")

        bieService.getSpeciesProfile(guid)
    }

    def search(String opusId, String term, List params) {
        log.debug("Searching for '${term}' in opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/search?opusId=${enc(opusId)}&term=${enc(term)}${params.join("")}")
    }

    def findByScientificName(String opusId, String scientificName, String max, String sortBy, boolean useWildcard) {
        log.debug("Searching for '${scientificName}' in opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/search/scientificName?opusId=${enc(opusId)}&scientificName=${enc(scientificName)}&max=${max ?: ""}&sortBy=${sortBy}&useWildcard=${useWildcard}")
    }

    def findByNameAndTaxonLevel(String opusId, String taxon, String scientificName, String max, String offset, String sortBy, boolean countChildren = false, boolean immediateChildrenOnly = false) {
        log.debug("Searching for '${scientificName}' in taxon ${taxon}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/search/taxon/name?opusId=${enc(opusId)}&scientificName=${enc(scientificName)}&taxon=${enc(taxon)}&max=${max}&offset=${offset}&sortBy=${sortBy}&countChildren=${countChildren}&immediateChildrenOnly=${immediateChildrenOnly}")
    }

    def countByNameAndTaxonLevel(String opusId, String taxon, String scientificName, boolean immediateChildrenOnly = false) {
        log.debug("Counting for '${scientificName}' in taxon ${taxon}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/search/taxon/name/total?opusId=${enc(opusId)}&scientificName=${enc(scientificName)}&taxon=${enc(taxon)}&immediateChildrenOnly=${immediateChildrenOnly}")
    }

    def groupByTaxonLevel(String opusId, String taxon, String max, String offset, String filter = null) {
        log.debug("Searching for '${taxon}' level")

        webService.get("${grailsApplication.config.profile.service.url}/profile/search/taxon/level?opusId=${enc(opusId)}&taxon=${enc(taxon)}&max=${max}&offset=${offset}${filter ? "&filter=${enc(filter)}" : ""}")
    }

    def getTaxonLevels(String opusId) {
        log.debug("Getting taxon levels for opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/profile/search/taxon/levels?opusId=${enc(opusId)}")
    }

    def getImmediateChildren(String opusId, String rank, String name, String max, String offset, String filter) {
        log.debug("Searching for children of '${rank} ${name}'")

        webService.get("${grailsApplication.config.profile.service.url}/profile/search/children?opusId=${enc(opusId)}&rank=${enc(rank)}&name=${enc(name)}&max=${max}&offset=${offset}&filter=${enc(filter) ?: ""}")
    }

    def updateBHLLinks(String opusId, String profileId, def links) {
        log.debug("Updating BHL links ${links} for profile ${profileId}")

        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/bhl", [
                profileId      : profileId,
                links          : links,
                userId         : authService.getUserId(),
                userDisplayName: authService.userDetails().userDisplayName
        ])
    }

    def updateLinks(String opusId, String profileId, def links) {
        log.debug("Updating links ${links} for profile ${profileId}")

        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/links", [
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

        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/attribute/${enc(attribute.uuid ?: '')}", attribute)
    }

    def deleteAttribute(String opusId, String attributeId, String profileId) {
        log.debug("Deleting attribute ${attributeId} of profile ${profileId}")

        webService.delete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/attribute/${enc(attributeId)}?profileId=${enc(profileId)}")
    }

    def getAuditHistory(String objectId, String userId, Integer offset = 0, Integer max = 100) {
        log.debug("Retrieving audit history for ${objectId ?: userId}")

        webService.get("${grailsApplication.config.profile.service.url}/audit/${objectId ? 'object' : 'user'}/${enc(objectId ?: userId)}?offset=${offset}&max=${max}")
    }

    def updateVocabulary(String opusId, String vocabId, vocab) {
        log.debug("Updating vocabulary ${vocabId} with data ${vocab}")

        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/vocab/${enc(vocabId)}", vocab)
    }

    def findUsagesOfVocabTerm(String opusId, String vocabId, String termName) {
        log.debug("Finding usages of term ${termName} from vocab ${vocabId}")

        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/vocab/usages/find?vocabId=${enc(vocabId)}&term=${URLEncoder.encode(termName, "UTF-8")}")
    }

    def replaceUsagesOfVocabTerm(String opusId, Map json) {
        log.debug("Replacing usages of vocab term(s): ${json}")

        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/vocab/usages/replace", json)
    }

    def getGlossary(String opusId) {
        log.debug("Fetching glossary for opus ${opusId}")

        webService.get("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/glossary/${enc(opusId)}")
    }

    def uploadGlossary(String opusId, String glossaryId, List items) {
        log.debug("Uploading glossary items for opus ${opusId}")

        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/glossary", [opusId: opusId, glossaryId: glossaryId, items: items])
    }

    def updateGlossaryItem(String opusId, String glossaryItemId, Map data) {
        log.debug("Updating glossary item ${glossaryItemId}")

        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/glossary/item/${enc(glossaryItemId)}", data)
    }

    def createGlossaryItem(String opusId, Map data) {
        log.debug("Creating glossary item for opus ${opusId}")

        webService.put("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/glossary/item", data)
    }

    def deleteGlossaryItem(String opusId, String glossaryItemId) {
        log.debug("Deleting glossary item ${glossaryItemId}")

        webService.delete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/glossary/item/${enc(glossaryItemId)}")
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

        webService.put("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/comment/", json)
    }

    def updateComment(String opusId, String profileId, String commentId, Map json) {
        log.debug("Updating comment ${commentId} for profile ${profileId}")

        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/comment/${enc(commentId)}", json)
    }

    def deleteComment(String opusId, String profileId, String commentId) {
        log.debug("Deleting comment ${commentId}")

        webService.delete("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/comment/${enc(commentId)}")
    }

    def updateAuthorship(String opusId, String profileId, Map json) {
        log.debug("Updating authorship for profile ${profileId}")

        webService.post("${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/authorship", json)
    }

    def checkName(String opusId, String scientificName) {
        log.debug("Checking name ${scientificName}")

        webService.get("${grailsApplication.config.profile.service.url}/checkName?opusId=${enc(opusId)}&scientificName=${enc(scientificName)}")
    }

    def loadReport(String opusId, String reportId, String pageSize, String offset,
                   Map dates, boolean countOnly) {
        ReportType report = ReportType.byId(reportId)
        String urlPrefix = "${grailsApplication.config.profile.service.url}/report/${report.id}"
        Map range
        def resp
        switch (report) {
            case ReportType.MISMATCHED_NAME:
                resp = webService.get("${urlPrefix}?opusId=${enc(opusId)}&offset=${offset}&max=${pageSize}")
                break;
            case ReportType.DRAFT_PROFILE:
                resp = webService.get("${urlPrefix}?opusId=${enc(opusId)}")
                break;
            case ReportType.ARCHIVED_PROFILE:
                resp = webService.get("${urlPrefix}?opusId=${enc(opusId)}")
                break;
            case ReportType.RECENT_CHANGE:
                range = utilService.getDateRange(dates.period, dates.from, dates.to);
                String url = "${urlPrefix}?opusId=${enc(opusId)}&to=${enc(range['to'])}&from=${enc(range['from'])}&offset=${offset}&max=${pageSize}"

                url += "&countOnly=" + BooleanUtils.toString(countOnly, "true", "false");

                resp = webService.get(url)
                break;
            case ReportType.RECENT_COMMENTS:
                range = utilService.getDateRange(dates.period, dates.from, dates.to)
                String url = "${urlPrefix}?opusId=${enc(opusId)}&to=${enc(range['to'])}&from=${enc(range['from'])}&offset=${offset}&max=${pageSize}"
                url += "&countOnly=" + BooleanUtils.toString(countOnly, "true", "false")
                resp = webService.get(url)
                break
        }

        resp
    }

    def getStatistics(String opusId) {
        def urlPrefix = "${grailsApplication.config.profile.service.url}/statistics"

        webService.get("${urlPrefix}?opusId=${enc(opusId)}")
    }

    def getFeatureLists(String opusId, String profileId) {
        Map model = getProfile(opusId, profileId);
        Map opus = model.opus;
        Map profile = model.profile;

        List result = []
        opus.featureLists?.each { listId ->
            Map list = [:]
            list.metadata = getListMetadata(listId)
            list.items = getProfileKVP(profile.scientificName, listId)
            result << list
        }

        result
    }

    Map getNextPendingPDFJob() {
        webService.get("${grailsApplication.config.profile.service.url}/job/pdf/next", [:], ContentType.APPLICATION_JSON, true, false)
    }

    void createPDFJob(Map params, boolean latest) {
        params.latest = latest;
        webService.put("${grailsApplication.config.profile.service.url}/job/pdf/", [params: params])
    }

    void updatePDFJob(String jobId, Map params) {
        webService.post("${grailsApplication.config.profile.service.url}/job/pdf/${jobId}", params)
    }

    Map getTags() {
        webService.get("${grailsApplication.config.profile.service.url}/tags/")
    }

    private def getProfileKVP(String profileId, String drid) {
        List result = []
        def list = getListItems(drid);
        if (list) {
            list.each {
                if (it.name.toLowerCase() == profileId.toLowerCase()) {
                    result.addAll(it.kvpValues)
                }
            }
        }
        result
    }

    boolean hasMatchedName(Map profile) {
        profile.guid as Boolean
    }

    def deleteDocument(String opusId, String profileId, String documentId) {
        def url = "${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/document/${documentId}"
        webService.delete(url, [:], ContentType.TEXT_PLAIN)
    }

    def updateDocument(String opusId, String profileId, doc) {
        def url ="${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/document/${doc.documentId ?:''}"
        webService.post(url, doc)
    }

    def Map listDocuments(String opusId, String profileId, boolean edit) {
        def url ="${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/document/list?editMode=${edit}"
        def resp = webService.get(url)
        if (resp && !resp.error) {
            return resp.resp
        }
        resp
    }

    def setPrimaryMultimedia(String opusId, String profileId, json) {
        // mmm boilerplate.
        def url = "${grailsApplication.config.profile.service.url}/opus/${enc(opusId)}/profile/${enc(profileId)}/primaryMultimedia"
        return webService.post(url, json)
    }
}
