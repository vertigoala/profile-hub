package au.org.ala.profile.hub

class SearchController extends BaseController {

    private static final List<String> SEARCH_OPTIONS = ["nameOnly"]

    ProfileService profileService

    def search() {
        if (!params.term) {
            badRequest "term is a required parameter"
        } else {
            List queryParams = params.findResults { key, value -> SEARCH_OPTIONS.contains(key) ? "&${key}=${value}" : null }
            def response = profileService.search(params.opusId, params.term, queryParams)

            handle response
        }
    }

    def findByScientificName() {
        if (!params.scientificName) {
            badRequest "scientificName is a required parameter. opusId and useWildcard are optional."
        } else {
            boolean wildcard = params.useWildcard ? params.useWildcard.toBoolean() : true
            def response = profileService.findByScientificName(params.opusId, params.scientificName, params.max, wildcard);

            handle response
        }
    }

    def findByNameAndTaxonLevel() {
        if (!params.taxon || !params.scientificName) {
            badRequest "taxon (e.g. phylum, genus, species, etc) and scientificName are a required parameters. You can also optionally supply opusId (comma-separated list of opus ids), max (max records to return), offset (0 based index to start from)."
        } else {
            boolean wildcard = params.useWildcard ? params.useWildcard.toBoolean() : true
            def response = profileService.findByNameAndTaxonLevel(params.opusId, params.taxon, params.scientificName, params.max, params.offset, wildcard)

            handle response
        }
    }

    def groupByTaxonLevel() {
        if (!params.opusId || !params.taxon) {
            badRequest "opusId and taxon are required parameters. You can also optionally supply max (max records to return) and offset (0 based index to start from)."
        } else {
            def response = profileService.groupByTaxonLevel(params.opusId, params.taxon, params.max, params.offset)
            handle response
        }
    }

    def getTaxonLevels() {
        if (!params.opusId) {
            badRequest "opusId is a required parameter"
        }

        def response = profileService.getTaxonLevels(params.opusId)
        handle response
    }
}
