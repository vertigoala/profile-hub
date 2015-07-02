package au.org.ala.profile.hub.util

/**
 * Created by Temi Varghese on 2/07/15.
 */
enum DateRangeType {
    TODAY("today"),
    LAST_7DAYS("last7Days"),
    LAST_30DAYS("last30Days"),
    CUSTOM("custom")

    String id

    DateRangeType(String id) {
        this.id = id
    }

    static DateRangeType byId(String id) {
        return values().find { it.id == id }
    }
}