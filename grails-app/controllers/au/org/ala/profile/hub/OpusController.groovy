package au.org.ala.profile.hub

import grails.converters.JSON

import static au.org.ala.profile.hub.util.HubConstants.*
import au.org.ala.profile.hub.util.JsonUtil
import au.org.ala.web.AuthService

class OpusController extends BaseController {

    AuthService authService
    CollectoryService collectoryService
    UserService userService
    ProfileService profileService

    JsonUtil jsonUtil = new JsonUtil()

    def index() {
        def opui = profileService.getOpus()

        render view: 'index', model: [
                opui         : opui ?: [],
                dataResources: collectoryService.getDataResources(),
                logoUrl      : DEFAULT_OPUS_LOGO_URL,
                bannerUrl    : DEFAULT_OPUS_BANNER_URL,
                pageTitle    : DEFAULT_OPUS_TITLE
        ]
    }

    def findUser() {
        def user = userService.findUser(params.userName)
        response.setContentType("application/json")
        render user
    }


    def edit() {
        def opus = jsonUtil.fromUrl("${grailsApplication.config.profile.service.url}/opus/${params.opusId}")
        def dataResource = jsonUtil.fromUrl("${grailsApplication.config.collectory.base.url}/ws/dataResource/${opus.dataResourceUid}")

        def vocab = null
        if (opus.attributeVocabUuid) {
            vocab = jsonUtil.fromUrl("${grailsApplication.config.profile.service.url}/vocab/${opus.attributeVocabUuid}")
        }

        render(view: 'edit', model: [
                opus         : opus,
                dataResource : dataResource,
                dataResources: collectoryService.getDataResources(),
                logoUrl      : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                bannerUrl    : opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                pageTitle    : opus.title ?: DEFAULT_OPUS_TITLE,
                vocab        : vocab?.name ?: DEFAULT_OPUS_VOCAB,
                currentUser  : authService.getDisplayName()
        ])
    }

    def show() {
        def opus = jsonUtil.fromUrl("${grailsApplication.config.profile.service.url}/opus/${params.opusId}")

        render view: 'show', model: [
                opus         : opus,
                logoUrl      : opus.logoUrl ?: DEFAULT_OPUS_LOGO_URL,
                bannerUrl    : opus.bannerUrl ?: DEFAULT_OPUS_BANNER_URL,
                pageTitle    : opus.title ?: DEFAULT_OPUS_TITLE,
        ]
    }

    def getJson() {
        if (!params.opusId) {
            badRequest()
        } else {
            def opus = profileService.getOpus(params.opusId as String)

            if (!opus) {
                notFound()
            } else {
                response.setContentType(CONTEXT_TYPE_JSON)
                render opus as JSON
            }
        }
    }

    def searchPanel = {
        render template: "search"
    }

    def opusSummaryPanel = {
        render template: "opusSummary"
    }
}
