package au.org.ala.profile.hub

import grails.converters.JSON
import groovy.transform.CompileStatic
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject


/**
 * Utility class to deal with the stupid JSON parsing made with the null values by JSON Grails parser
 */
class JSONUtils {

    static def validateAndParseJSON(String data, boolean dropNulls = false /* new feature, so false by default to ensure regression */ ){
        data = data?.trim()
        if (!data || data == "null") return null
        def dataParsed = safe(JSON.parse(data), dropNulls)
        return dataParsed
    }

    @CompileStatic
    static Object safe(def o, boolean dropNullsInMaps = false) {
        if (o == null || o instanceof JSONObject.Null) {
            return null
        }
        if (o instanceof JSONObject) {
            JSONObject jsonObject = o as JSONObject
            Map newMap = new LinkedHashMap()
            jsonObject.keySet().each { Object key ->
                Object value = safe(jsonObject[key], dropNullsInMaps)
                if ("null" == value) {
                    newMap[key] = null
                }
                else if (!dropNullsInMaps || (dropNullsInMaps && value != null)) {
                    newMap[key] = value
                }
            }
            o = newMap
        } else if (o instanceof JSONArray) {
            JSONArray jsonArray = o as JSONArray
            List newList = jsonArray.collect { safe(it, dropNullsInMaps)}
            o = newList
        }
        return o
    }

}
