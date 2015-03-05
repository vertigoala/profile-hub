/**
 * Profile Search controller
 */
profileEditor.controller('SearchController', function (profileService, util, messageService) {
    var self = this;

    self.search = function () {
        var opusId = util.getPathItem(util.LAST);
        var searchResult = profileService.profileSearch(opusId, self.searchTerm);
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