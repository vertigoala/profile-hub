/**
 * Profile Search controller
 */
profileEditor.controller('SearchController', function (profileService, util, messageService) {
    var self = this;

    self.search = function () {
        self.opusId = util.getEntityId("opus");
        var searchResult = profileService.profileSearch(self.opusId, self.searchTerm, true);
        searchResult.then(function (data) {
                console.log("Found " + data.length + " results");
                self.profiles = data;
            },
            function () {
                messageService.alert("Failed to perform search for '" + self.searchTerm + "'.");
            }
        );
    };
});