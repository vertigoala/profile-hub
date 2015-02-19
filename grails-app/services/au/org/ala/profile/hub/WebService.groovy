package au.org.ala.profile.hub

import grails.converters.JSON
import org.apache.http.HttpHeaders
import org.apache.http.HttpStatus
import org.codehaus.groovy.grails.web.converters.exceptions.ConverterException
import org.springframework.http.MediaType

import javax.servlet.http.HttpServletResponse

class WebService {
    // TODO refactor this class, it's really ugly
    static transactional = false

    def grailsApplication
    UserService userService

    def get(String url, boolean includeUserId) {
        def conn = null
        try {
            conn = configureConnection(url, includeUserId)
            return responseText(conn)
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

    private int defaultReadTimeout() {
        grailsApplication.config.webservice.readTimeout as int
    }

    private int defaultConnectTimeout() {
        grailsApplication.config.webservice.connectTimeout as int
    }

    private URLConnection configureConnection(String url, boolean includeUserId, Integer timeout = null) {
        URLConnection conn = new URL(url).openConnection()

        def readTimeout = timeout ?: defaultReadTimeout()
        conn.setConnectTimeout(grailsApplication.config.webservice.connectTimeout as int)
        conn.setReadTimeout(readTimeout)
        def user = userService.getUser()
        if (includeUserId && user) {
            conn.setRequestProperty(grailsApplication.config.app.http.header.userId, user.userId)
        }
        conn
    }

    /**
     * Proxies a request URL but doesn't assume the response is text based. (Used for proxying requests to
     * ecodata for excel-based reports)
     */
    def proxyGetRequest(HttpServletResponse response, String url, boolean includeUserId = true, boolean includeApiKey = false, Integer timeout = null) {

        HttpURLConnection conn = configureConnection(url, includeUserId)
        def readTimeout = timeout ?: defaultTimeout()
        conn.setConnectTimeout(grailsApplication.config.webservice.connectTimeout as int)
        conn.setReadTimeout(readTimeout)

        if (includeApiKey) {
            conn.setRequestProperty("Authorization", grailsApplication.config.api_key);
        }

        def headers = [HttpHeaders.CONTENT_DISPOSITION, HttpHeaders.TRANSFER_ENCODING]
        response.setContentType(conn.getContentType())
        response.setContentLength(conn.getContentLength())

        headers.each { header ->
            response.setHeader(header, conn.getHeaderField(header))
        }
        response.status = conn.responseCode
        response.outputStream << conn.inputStream

    }

    def get(String url) {
        return get(url, true)
    }


    def getJson(String url, Integer timeout = null) {
        def conn = null
        try {
            conn = configureConnection(url, true, timeout)
            def json = responseText(conn)
            return JSON.parse(json)
        } catch (ConverterException e) {
            def error = ['error': "Failed to parse json. ${e.getClass()} ${e.getMessage()} URL= ${url}."]
            log.error error
            return error
        } catch (SocketTimeoutException e) {
            def error = [error: "Timed out getting json. URL= ${url}.",
                    statusCode: HttpStatus.SC_GATEWAY_TIMEOUT]
            println error
            return error
        } catch (ConnectException ce) {
            log.info "Exception class = ${ce.getClass().name} - ${ce.getMessage()}"
            def error = [error: "ecodata service not available. URL= ${url}.", statusCode: HttpStatus.SC_INTERNAL_SERVER_ERROR]
            println error
            return error
        } catch (Exception e) {
            log.info "Exception class = ${e.getClass().name} - ${e.getMessage()}"
            def error = [error     : "Failed to get json from web service. ${e.getClass()} ${e.getMessage()} URL= ${url}.",
                         statusCode: conn?.responseCode ?: HttpStatus.SC_INTERNAL_SERVER_ERROR,
                         detail    : conn?.errorStream?.text]
            log.error error
            return error
        }
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

    def doPostWithParams(String url, Map params) {
        def conn = null
        def charEncoding = 'utf-8'
        OutputStreamWriter writer = null
        try {
            String query = ""
            boolean first = true
            for (String name : params.keySet()) {
                query += first ? "?" : "&"
                first = false
                query += name.encodeAsURL() + "=" + params.get(name).encodeAsURL()
            }
            conn = new URL(url + query).openConnection()
            conn.setRequestMethod("POST")
            conn.setDoOutput(true)
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", grailsApplication.config.api_key);

            def user = userService.getUser()
            if (user) {
                conn.setRequestProperty(grailsApplication.config.app.http.header.userId, user.userId) // used by ecodata
                conn.setRequestProperty("Cookie", "ALA-Auth=" + java.net.URLEncoder.encode(user.userName, charEncoding))
                // used by specieslist
            }
            writer = new OutputStreamWriter(conn.getOutputStream(), charEncoding)

            writer.flush()
            def resp = conn.inputStream.text
            writer.close()
            return [resp: JSON.parse(resp ?: "{}"), statusCode: HttpStatus.SC_OK]
            // fail over to empty json object if empty response string otherwise JSON.parse fails
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
    }

    def doPost(String url, Map postBody) {
        def charEncoding = "utf-8"

        URLConnection conn = null
        def response = [:]
        OutputStreamWriter writer = null
        try {
            conn = new URL(url).openConnection()
            conn.setDoOutput(true)
            conn.setRequestProperty("Content-Type", "application/json;charset=${charEncoding}");
            conn.setRequestProperty("Authorization", grailsApplication.config.api_key as String);

            def user = userService.getUser()
            if (user) {
                conn.setRequestProperty(grailsApplication.config.app.http.header.userId as String, user.userId as String)
                conn.setRequestProperty("Cookie", "ALA-Auth=${URLEncoder.encode(user.userName, charEncoding)}")
            }

            writer = new OutputStreamWriter(conn.getOutputStream(), charEncoding)
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
        url += (url.contains('?') ? '&' : '?') + "api_key=${grailsApplication.config.api_key}"
        Map response
        URLConnection conn = null
        try {
            conn = new URL(url).openConnection()
            conn.setRequestMethod("DELETE")
            conn.setRequestProperty("Authorization", grailsApplication.config.api_key);
            def user = userService.getUser()
            if (user) {
                conn.setRequestProperty(grailsApplication.config.app.http.header.userId, user.userId)
            }
            int responseCode = conn.getResponseCode()
            if (responseCode == HttpStatus.SC_OK || responseCode == HttpStatus.SC_NO_CONTENT) {
                response = [success: true, statusCode: responseCode]
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