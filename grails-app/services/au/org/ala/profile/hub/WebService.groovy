package au.org.ala.profile.hub

import grails.converters.JSON
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.HttpEntity
import org.apache.http.HttpStatus
import org.apache.http.client.config.RequestConfig
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.entity.mime.content.ByteArrayBody
import org.apache.http.entity.mime.content.StringBody
import org.springframework.web.multipart.commons.CommonsMultipartFile
import javax.servlet.http.HttpServletResponse

import static groovyx.net.http.Method.*
import static org.codehaus.groovy.grails.web.servlet.HttpHeaders.CONNECTION
import static org.codehaus.groovy.grails.web.servlet.HttpHeaders.CONTENT_DISPOSITION
import static org.codehaus.groovy.grails.web.servlet.HttpHeaders.TRANSFER_ENCODING

class WebService {
    static final String CHAR_ENCODING = "utf-8"

    static final DEFAULT_TIMEOUT_MILLIS = 600000; // five minutes

    def grailsApplication
    UserService userService

    /**
     * Sends an HTTP GET request to the specified URL. Any parameters must already be URL-encoded.
     *
     * @param url The url-encoded URL to send the request to
     * @return [statusCode: int, resp: [:]] on success, or [statusCode: int, error: string] on error
     */
    Map get(String url) {
        send(GET, url)
    }

    /**
     * Sends an HTTP PUT request to the specified URL.
     *
     * The data map will be sent as the JSON body of the request (i.e. use request.getJSON() on the receiving end).
     *
     * @param url The url-encoded url to send the request to
     * @param data Map containing the data to be sent as the post body
     * @return [statusCode: int, resp: [:]] on success, or [statusCode: int, error: string] on error
     */
    Map put(String url, Map data) {
        send(PUT, url, data)
    }

    /**
     * Sends an HTTP POST request to the specified URL.
     *
     * The data map will be sent as the JSON body of the request (i.e. use request.getJSON() on the receiving end).
     *
     * @param url The url-encoded url to send the request to
     * @param data Map containing the data to be sent as the post body
     * @return [statusCode: int, resp: [:]] on success, or [statusCode: int, error: string] on error
     */
    Map post(String url, Map data) {
        send(POST, url, data)
    }

    /**
     * Sends a multipart HTTP POST request to the specified URL.
     *
     * The data map will be sent as the JSON body of the request (i.e. use request.getJSON() on the receiving end).
     *
     * @param url The url-encoded url to send the request to
     * @param data Map containing the data to be sent as the post body
     * @param files List of 0 or more files to be included in the multipart request (note: if files is null, then the request will NOT be multipart)
     * @return [statusCode: int, resp: [:]] on success, or [statusCode: int, error: string] on error
     */
    Map postMultipart(String url, Map data, List files) {
        send(POST, url, data, files)
    }

    /**
     * Sends a HTTP DELETE request to the specified URL. Any parameters must already be URL-encoded.
     *
     * @param url The url-encoded url to send the request to
     * @return [statusCode: int, resp: [:]] on success, or [statusCode: int, error: string] on error
     */
    Map delete(String url) {
        send(DELETE, url)
    }

    /**
     * Proxies a request URL but doesn't assume the response is text based.
     *
     * Used for operations like proxying a download request from one application to another.
     *
     * @param response The HttpServletResponse of the calling request: the response from the proxied request will be written to this object
     * @param url The URL of the service to proxy to
     */
    void proxyGetRequest(HttpServletResponse response, String url) {
        HttpURLConnection conn = (HttpURLConnection) configureConnection(url, true)
        conn.useCaches = false

        try {
            conn.setRequestProperty(CONNECTION, 'close') // disable Keep Alive

            conn.connect()

            response.contentType = conn.contentType
            int contentLength = conn.contentLength
            if (contentLength != -1) {
                response.contentLength = contentLength
            }

            List<String> headers = [CONTENT_DISPOSITION, TRANSFER_ENCODING]
            headers.each { header ->
                String headerValue = conn.getHeaderField(header)
                if (headerValue) {
                    response.setHeader(header, headerValue)
                }
            }
            response.status = conn.responseCode
            conn.inputStream.withStream { response.outputStream << it }
        } finally {
            conn.disconnect()
        }

    }

    private Map send(Method method, String url, Map data = null, List files = null) {
        log.debug("${method} request to ${url}")

        Map result = [:]

        try {
            HTTPBuilder http = new HTTPBuilder(url)

            http.request(method) { request ->
                configureRequestTimeouts(request)
                configureRequestHeaders(url, headers)

                if (files != null) {
                    request.entity = constructMultiPartEntity(data, files)
                } else if (data != null) {
                    contentType = ContentType.APPLICATION_JSON
                    body = data
                } else {
                    contentType = ContentType.APPLICATION_JSON
                }

                response.success = { resp, json ->
                    result.statusCode = resp.status
                    result.resp = json
                }
                response.failure = { resp ->
                    result.statusCode = resp.status
                    result.error = "Failed calling web service - service returned HTTP ${resp.status}"
                }

                result
            } as Map
        } catch (Exception e) {
            log.error("Failed sending ${method} request to ${url}", e)
            result.statusCode = HttpStatus.SC_INTERNAL_SERVER_ERROR
            result.error = "Failed calling web service. ${e.getClass()} ${e.getMessage()} URL= ${url}, method ${method}."
        }

        result
    }

    private String getApiKey(String url) {
        url?.startsWith("${grailsApplication.config.profile.service.url}") ? grailsApplication.config.profile.service.apiKey : null
    }

    private void configureRequestTimeouts(request) {
        int timeout = (grailsApplication.config.webservice.connectTimeout ?: DEFAULT_TIMEOUT_MILLIS) as int

        RequestConfig.Builder config = RequestConfig.custom()
        config.setConnectTimeout(timeout)
        config.setSocketTimeout(timeout)
        config.setConnectionRequestTimeout(timeout)

        request.config = config.build()
    }

    private void configureRequestHeaders(String url, Map headers) {
        String apiKey = getApiKey(url)
        if (apiKey) {
            headers.apiKey = apiKey
        }

        UserDetails user = userService.getUser()

        if (user) {
            headers.put(grailsApplication.config.app.http.header.userId as String, user.userId as String)
            headers.put("Cookie", "ALA-Auth=${URLEncoder.encode(user.userName, CHAR_ENCODING)}")
        }
    }

    private HttpEntity constructMultiPartEntity(Map data, List files) {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
        entityBuilder.addPart("data", new StringBody((data as JSON) as String))
        files.eachWithIndex { it, index ->
            if (it instanceof byte[]) {
                entityBuilder.addPart("file${index}", new ByteArrayBody(it, "file${index}"))
            } else if (it instanceof CommonsMultipartFile) {
                entityBuilder.addPart(it.originalFilename, new ByteArrayBody(it.bytes, it.contentType, it.originalFilename))
            } else {
                entityBuilder.addPart("file${index}", new ByteArrayBody(it.bytes, "file${index}"))
            }
        }
        entityBuilder.build()
    }

    private URLConnection configureConnection(String url, boolean includeUserId, Integer timeout = null) {
        URLConnection conn = new URL(url).openConnection()

        conn.setConnectTimeout((grailsApplication.config.webservice.connectTimeout ?: DEFAULT_TIMEOUT_MILLIS) as int)
        conn.setReadTimeout((timeout ?: grailsApplication.config.webservice.readTimeout ?: DEFAULT_TIMEOUT_MILLIS) as int)
        def user = userService.getUser()

        if (user && includeUserId) {
            conn.setRequestProperty(grailsApplication.config.app.http.header.userId as String, user.userId as String)
            conn.setRequestProperty("Cookie", "ALA-Auth=${URLEncoder.encode(user.userName, CHAR_ENCODING)}")
        }

        String apiKey = getApiKey(url)
        if (apiKey) {
            conn.setRequestProperty("apiKey", apiKey)
        }

        conn
    }
}