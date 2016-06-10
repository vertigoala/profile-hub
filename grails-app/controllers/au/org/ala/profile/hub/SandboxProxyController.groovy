package au.org.ala.profile.hub

import groovy.json.JsonSlurper

import static au.org.ala.profile.hub.Utils.enc
import au.org.ala.web.AuthService
import au.org.ala.ws.service.WebService
import org.apache.http.entity.ContentType

import static au.org.ala.profile.hub.Utils.isHttpSuccess

class SandboxProxyController extends BaseController {
    WebService webService
    AuthService authService
    ProfileService profileService

    def parseColumns() {
        webService.proxyPostRequest(response, "${grailsApplication.config.sandbox.base.url}/datacheck/dataCheck/parseColumns", request.reader.text)
    }

    def parseColumnsWithFirstLineInfo() {
        webService.proxyPostRequest(response, "${grailsApplication.config.sandbox.base.url}/datacheck/dataCheck/parseColumnsWithFirstLineInfo", request.parameterMap, ContentType.APPLICATION_FORM_URLENCODED)
    }

    def processData() {
        webService.proxyPostRequest(response, "${grailsApplication.config.sandbox.base.url}/datacheck/dataCheck/processData", request.parameterMap, ContentType.APPLICATION_FORM_URLENCODED)
    }

    def upload() {
        String csvData = request.getParameter("rawData").trim()
        Map data = [
                headers            : request.getParameter("headers").trim(),
                csvData            : csvData,
                separator          : getSeparatorName(csvData),
                datasetName        : request.getParameter("datasetName").trim(),
                customIndexedFields: request.getParameter("customIndexedFields").trim(),
                firstLineIsData    : request.getParameter("firstLineIsData"),
                alaId              : authService.getUserId()
        ]

        Map opus = profileService.getOpus(params.opusId)

        Map result = webService.post("${grailsApplication.config.sandbox.base.url}/biocache-service/upload/", data, [:], ContentType.APPLICATION_FORM_URLENCODED, true, true, [Accept: ContentType.APPLICATION_JSON.toString()])

        webService.get("${grailsApplication.config.sandbox.base.url}/biocache-service/cache/refresh")

        // the biocache-service upload operation returns a json string in the format {uid: 'drId'}, which is jammed into
        // the resp map returned by the WebService class, as the KEY (with a value of null).
        if (isHttpSuccess(result.statusCode as int) && result?.resp?.keySet()) {
            String dataResourceId = new JsonSlurper().parseText(result.resp.keySet()[0]).uid

            if (!opus.dataResourceConfig.privateRecordSources) {
                opus.dataResourceConfig.privateRecordSources = []
            }
            opus.dataResourceConfig.privateRecordSources << dataResourceId
            profileService.updateOpus(opus.uuid, opus)

            response.contentType = ContentType.APPLICATION_JSON.toString()
            render result.resp.keySet()[0]
        } else {
            handle result
        }
    }

    def uploadStatus() {
        webService.proxyGetRequest(response, "${grailsApplication.config.sandbox.base.url}/datacheck/dataCheck/uploadStatus?uid=${enc(params.uid)}&random=${enc(params.random)}")
    }

    def previewUpload() {
        render view: "data/uploadData", mode: [fn: params.fn, id: params.id, userId: authService.getUserId()]
    }

    private static String getSeparatorName(String raw) {
        int tabs = raw.count("\t")
        int commas = raw.count(",")

        tabs > commas ? "TAB" : "COMMA"
    }
}
