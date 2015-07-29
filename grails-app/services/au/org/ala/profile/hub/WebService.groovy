package au.org.ala.profile.hub

import grails.converters.JSON
import groovyx.net.http.HTTPBuilder
import org.apache.http.HttpStatus
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.ByteArrayBody
import org.apache.http.entity.mime.content.StringBody
import org.springframework.http.MediaType

import static groovyx.net.http.Method.POST

//TODO We should be using an existing REST client like Groovy Http Builder instead of this service -> https://github.com/jgritman/httpbuilder
class WebService {
    static final String CHAR_ENCODING= "utf-8"

    static final DEFAULT_TIMEOUT_MILLIS = 600000; // five minutes
    // TODO refactor this class, it's really ugly
    static transactional = false

    def grailsApplication
    UserService userService

    def getApiKey(String url) {
        String apiKey = null

        // all requests to the profile service should have the api key
        if (url.startsWith("${grailsApplication.config.profile.service.url}")) {
            apiKey = grailsApplication.config.profile.service.apiKey
        }

        apiKey
    }

    def get(String url, boolean includeUserId, boolean json = true) {
        log.debug("Fetching data from ${url}")
        def conn = null
        try {
            conn = configureConnection(url, includeUserId)
            String resp = responseText(conn)

            Map result
            if (json) {
                result = [resp: JSON.parse(resp ?: "{}"), statusCode: HttpStatus.SC_OK]
            } else {
                result = [resp: resp, statusCode: HttpStatus.SC_OK]
            }
        } catch (SocketTimeoutException e) {
            def error = [error: "Timed out calling web service. URL= ${url}.", statusCode: HttpStatus.SC_GATEWAY_TIMEOUT]
            log.error error
            return error
        } catch (Exception e) {
            def error = [error     : "Failed calling web service. ${e.getClass()} ${e.getMessage()} URL= ${url}.",
                         statusCode: conn?.responseCode ?: HttpStatus.SC_INTERNAL_SERVER_ERROR,
                         detail    : conn?.errorStream?.text]
            log.error error, e
            return error
        }
    }

    private int getReadTimeout() {
        (grailsApplication.config.webservice.readTimeout ?: DEFAULT_TIMEOUT_MILLIS) as int
    }

    private int getConnectTimeout() {
        (grailsApplication.config.webservice.connectTimeout ?: DEFAULT_TIMEOUT_MILLIS) as int
    }

    private URLConnection configureConnection(String url, boolean includeUserId, Integer timeout = null) {
        URLConnection conn = new URL(url).openConnection()

        def readTimeout = timeout ?: getReadTimeout()
        conn.setConnectTimeout(getConnectTimeout())
        conn.setReadTimeout(readTimeout)
        def user = userService.getUser()
        if (includeUserId && user) {
            conn.setRequestProperty(grailsApplication.config.app.http.header.userId, user.userId)
        }

        String apiKey = getApiKey(url)
        if (apiKey) {
            conn.setRequestProperty("apiKey", apiKey)
        }

        conn
    }

    def get(String url, json = true) {
        return get(url, true, json)
    }


    /**
     * Reads the response from a URLConnection taking into account the character encoding.
     * @param urlConnection the URLConnection to read the response from.
     * @return the contents of the response, as a String.
     */
    def responseText(urlConnection) {

        def charset = 'UTF-8' // default
        def contentType = urlConnection.getContentType()
        if (contentType) {
            def mediaType = MediaType.parseMediaType(contentType)
            charset = (mediaType.charSet) ? mediaType.charSet.toString() : 'UTF-8'
        }
        return urlConnection.content.getText(charset)
    }

    def doPut(String url, Map data) {
        send(url, data, "PUT")
    }

    def doPost(String url, Map data) {
        send(url, data, "POST")
    }

    def postMultipart(String url, Map data, List files) {
        HTTPBuilder http = new HTTPBuilder(url)

        http.request(POST) { multipartRequest ->
            MultipartEntityBuilder entityBuilder = new MultipartEntityBuilder()
            entityBuilder.addPart("data", new StringBody((data as JSON) as String))
            files.eachWithIndex { it, index ->
                entityBuilder.addPart("file${index}", new ByteArrayBody(it, "file${index}"))
            }
            multipartRequest.entity = entityBuilder.build()

            def user = userService.getUser()
            if (user) {
                headers."${grailsApplication.config.app.http.header.userId}" = user.userId as String
                headers.Cookie = "ALA-Auth=${URLEncoder.encode(user.userName, CHAR_ENCODING)}"
            }

            String apiKey = getApiKey(url)
            if (apiKey) {
                headers.apiKey = apiKey
            }

            def result = null
            response.success = { resp, rData ->
                result = [resp: rData as JSON, statusCode: HttpStatus.SC_OK]
            }

            return result
        }
    }

    def send = {String url, Map postBody, String method ->
        URLConnection conn = null
        def response = [:]
        OutputStreamWriter writer = null
        try {
            conn = new URL(url).openConnection()
            conn.setDoOutput(true)
            conn.setRequestMethod(method)
            conn.setRequestProperty("Content-Type", "application/json;charset=${CHAR_ENCODING}");

            def user = userService.getUser()
            if (user) {
                conn.setRequestProperty(grailsApplication.config.app.http.header.userId as String, user.userId as String)
                conn.setRequestProperty("Cookie", "ALA-Auth=${URLEncoder.encode(user.userName, CHAR_ENCODING)}")
            }

            String apiKey = getApiKey(url)
            if (apiKey) {
                conn.setRequestProperty("apiKey", apiKey)
            }

            writer = new OutputStreamWriter(conn.getOutputStream(), CHAR_ENCODING)
            writer.write((postBody as JSON).toString())
            writer.flush()
            def resp = conn.inputStream.text

            response = [resp:JSON.parse(resp ?: "{}"), statusCode: HttpStatus.SC_OK]
            log.debug("Response from POST = ${response}")
            // fail over to empty json object if empty response string otherwise JSON.parse fails
        } catch (FileNotFoundException e) {
            response = [error: "Not Found: URL= ${url}.",
                        statusCode: conn?.responseCode ?: HttpStatus.SC_NOT_FOUND]
            log.error(response, e)
        } catch (SocketTimeoutException e) {
            response = [error: "Timed out calling web service. URL= ${url}.",
                        statusCode: conn?.responseCode ?: HttpStatus.SC_GATEWAY_TIMEOUT]
            log.error(response, e)
        } catch (Exception e) {
            response = [error     : "Failed calling web service. ${e.getMessage()} URL= ${url}.",
                        statusCode: conn?.responseCode ?: HttpStatus.SC_INTERNAL_SERVER_ERROR,
                        detail    : conn?.errorStream?.text]
            log.error(response, e)
        } finally {
            if (writer) {
                writer.close()
            }
        }

        response
    }

    def doDelete(String url) {
        Map response
        URLConnection conn = null
        try {
            conn = new URL(url).openConnection()
            conn.setRequestMethod("DELETE")

            def user = userService.getUser()
            if (user) {
                conn.setRequestProperty(grailsApplication.config.app.http.header.userId, user.userId)
            }
            String apiKey = getApiKey(url)
            if (apiKey) {
                conn.setRequestProperty("apiKey", apiKey)
            }

            int responseCode = conn.getResponseCode()
            if (responseCode == HttpStatus.SC_OK || responseCode == HttpStatus.SC_NO_CONTENT) {
                response = [resp: [success: true], statusCode: responseCode]
            } else {
                response = [error: "Delete Failed",
                           statusCode: responseCode]
                log.error("Delete failed with response code ${responseCode}")
            }
        } catch (SocketTimeoutException e) {
            response = [error: "Timed out calling web service. URL= ${url}.",
                        statusCode: conn?.responseCode ?: HttpStatus.SC_GATEWAY_TIMEOUT]
            log.error(response, e)
        } catch (Exception e) {
            response = [error     : "Failed calling web service. ${e.getMessage()} URL= ${url}.",
                        statusCode: conn?.responseCode ?: HttpStatus.SC_INTERNAL_SERVER_ERROR,
                        detail    : conn?.errorStream?.text]
            log.error(response, e)
        } finally {

        }
        response
    }

    private getConnection(String url, Map properties, String method, int connectTimeout = -1, int readTimeout = -1) {

    }

}