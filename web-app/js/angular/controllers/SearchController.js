/**
 * Profile Search controller
 */
profileEditor.controller('SearchController', function (profileService, util, messageService) {
    var self = this;

    self.pageSizes = [5, 10, 25, 50, 100];
    self.pagesToShow = 10;
    self.pageSize = 25;

    self.MAX_FACET_ITEMS = 25;

    self.orderedTaxonLevels = [{key: "kingdom", name: "Kingdom", order: 0},
        {key: "phylum", name: "Phylum", order: 1},
        {key: "class", name: "Class", order: 2},
        {key: "subclass", name: "Subclass", order: 3},
        {key: "order", name: "Order", order: 4},
        {key: "family", name: "Family", order: 5},
        {key: "genus", name: "Genus", order: 6}];
    self.opusId = util.getEntityId("opus");
    self.taxonResults = {};
    self.taxonLevels = [];
    self.profiles = [];
    self.selectedTaxon = {};

    self.search = function (wildcard) {
        if (wildcard === undefined) {
            wildcard = true
        }

        self.selectedTaxon = {};

        var searchResult = profileService.profileSearch(self.opusId, self.searchTerm, wildcard);
        searchResult.then(function (data) {
                console.log("Found " + data.length + " results");
                self.profiles = data;
            },
            function () {
                messageService.alert("Failed to perform search for '" + self.searchTerm + "'.");
            }
        );
    };

    self.getTaxonLevels = function() {
        var levels = profileService.getTaxonLevels(self.opusId);
        levels.then(function (data) {
            self.taxonLevels = data;
        },
        function() {
            messageService.alert("An error occurred while determining taxon levels.");
        });
    };

    self.searchByTaxonLevel = function(level, offset) {
        if (offset === undefined) {
            offset = 0;
        }

        var result = profileService.profileSearchByTaxonLevel(self.opusId, level, self.MAX_FACET_ITEMS, offset);
        result.then(function(data) {
            if (!self.taxonResults[level]) {
                self.taxonResults[level] = {};
            }
            angular.extend(self.taxonResults[level], data);
        }, function() {
            messageService.alert("Failed to perform taxon level search.");
        });
    };

    self.searchByTaxon = function(taxon, scientificName, count, offset) {
        self.selectedTaxon = {level: taxon, name: scientificName, count: count};

        if (offset === undefined) {
            offset = 0;
        }

        self.searchTerm = null;

        var results = profileService.profileSearchByTaxonLevelAndName(self.opusId, taxon, scientificName, self.pageSize, offset);
        results.then(function (data) {
                console.log("Found " + data.length + " results");
                self.profiles = data;
            },
            function () {
                messageService.alert("Failed to perform search for '" + self.searchTerm + "'.");
            }
        );
    };

    self.selectSingleResult = function() {
        if (self.profiles.length == 1) {
            util.redirect(util.contextRoot() + "/opus/" + self.opusId + "/profile/" + self.profiles[0].scientificName);
        }
    }
});