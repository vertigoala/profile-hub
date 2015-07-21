package au.org.ala.profile.hub.util

enum ReportType {
    MISMATCHED_NAME("mismatchedNames"),
    DRAFT_PROFILE("draftProfiles"),
    ARCHIVED_PROFILE("archivedProfiles"),
    MOST_RECENT_CHANGE("mostRecentChange")

    String id

    ReportType(String id) {
        this.id = id
    }

    static ReportType byId(String id) {
        return values().find { it.id == id }
    }
}