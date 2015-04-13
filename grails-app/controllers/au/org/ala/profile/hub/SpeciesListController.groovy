package au.org.ala.profile.hub

class SpeciesListController extends BaseController {

    SpeciesListService speciesListService

    def retrieveLists() {
        if (!params.guid) {
            badRequest "GUID is a required parameter"
        } else {
            def response = speciesListService.getListsForGuid(params.guid)

            handle response
        }
    }


    def getAllLists() {
        def response = speciesListService.getAllLists()

        handle response
    }
}
