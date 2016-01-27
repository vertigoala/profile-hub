/**
 * Profile Search controller
 */
profileEditor.controller('SearchController', function (profileService, util, messageService, $filter, $sessionStorage) {
    var self = this;

    self.opusId = util.getEntityId("opus");

    self.showSearchOptions = false;
    self.searchOptions = {
        nameOnly: false
    };
    self.searchResults = {};
    retrieveCachedSearchResult();

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
    self.taxonResults = {};
    self.taxonLevels = [];
    self.profiles = [];
    self.selectedTaxon = {};

    self.search = function (pageSize, offset) {
        self.searchOptions.offset = offset || 0;
        self.searchOptions.pageSize = pageSize || self.pageSize;

        var searchResult = profileService.search(self.opusId, self.searchTerm, self.searchOptions);
        searchResult.then(function (data) {
                self.searchResults = data;

                angular.forEach(self.searchResults.items, function (profile) {
                    profile.image = {
                        status: 'not-checked',
                        type: {}
                    };
                });

                cacheSearchResult(self.searchResults);
            },
            function () {
                messageService.alert("Failed to perform search for '" + self.searchTerm + "'.");
            });
    };

    function cacheSearchResult(result) {
        if (!$sessionStorage.searches) {
            $sessionStorage.searches = {};
        }
        if (!$sessionStorage.searches[self.opusId ? self.opusId : 'all']) {
            $sessionStorage.searches[self.opusId ? self.opusId : 'all'] = {};
        }
        $sessionStorage.searches[self.opusId ? self.opusId : 'all'].result = result;
        $sessionStorage.searches[self.opusId ? self.opusId : 'all'].term = self.searchTerm;
        $sessionStorage.searches[self.opusId ? self.opusId : 'all'].options = self.searchOptions;
    }

    function retrieveCachedSearchResult() {
        var cachedResult = $sessionStorage.searches ? $sessionStorage.searches[self.opusId ? self.opusId : 'all'] : {};
        if (cachedResult) {
            self.searchResults = cachedResult.result;
            self.searchTerm = cachedResult.term;
            self.searchOptions = cachedResult.options ? cachedResult.options : {
                nameOnly: false
            };
        }
    }

    self.clearSearch = function () {
        self.searchResults = {};
        self.searchTerm = "";

        $sessionStorage.searches[self.opusId ? self.opusId : 'all'] = {};
    };

    self.loadImageForProfile = function (profileId) {
        var profile = $filter('filter')(self.searchResults.items, {uuid: profileId}, true)[0];

        if (profile.image.status == 'not-checked') {
            var future = profileService.getPrimaryImage(profile.opusId, profileId);
            profile.image.status = 'checking';
            future.then(function (data) {
                if (data) {
                    profile.image.url = data.thumbnailUrl;
                    profile.image.type = data.type;
                }
                profile.image.status = 'checked';
            });
        }
    };

    self.searchByScientificName = function (wildcard) {
        if (wildcard === undefined) {
            wildcard = true
        }

        self.selectedTaxon = {};

        var searchResult = profileService.profileSearch(self.opusId, self.searchTerm, wildcard);
        searchResult.then(function (data) {
                self.profiles = data;
            },
            function () {
                messageService.alert("Failed to perform search for '" + self.searchTerm + "'.");
            }
        );
    };

    self.getTaxonLevels = function () {
        var levels = profileService.getTaxonLevels(self.opusId);
        levels.then(function (data) {
                self.taxonLevels = data;
            },
            function () {
                messageService.alert("An error occurred while determining taxon levels.");
            });
    };

    self.searchByTaxonLevel = function (level, offset) {
        if (offset === undefined) {
            offset = 0;
        }

        var result = profileService.profileSearchByTaxonLevel(self.opusId, level, self.MAX_FACET_ITEMS, offset);
        result.then(function (data) {
            if (!self.taxonResults[level]) {
                self.taxonResults[level] = {};
            }
            angular.extend(self.taxonResults[level], data);
        }, function () {
            messageService.alert("Failed to perform taxon level search.");
        });
    };

    self.searchByTaxon = function (taxon, scientificName, count, offset) {
        self.selectedTaxon = {level: taxon, name: scientificName, count: count};

        if (offset === undefined) {
            offset = 0;
        }

        self.searchTerm = null;

        var results = profileService.profileSearchByTaxonLevelAndName(self.opusId, taxon, scientificName, self.pageSize, offset);
        results.then(function (data) {
                self.profiles = data;
            },
            function () {
                messageService.alert("Failed to perform search for '" + self.searchTerm + "'.");
            }
        );
    };

    self.selectSingleResult = function () {
        if (self.profiles.length == 1) {
            util.redirect(util.contextRoot() + "/opus/" + self.opusId + "/profile/" + self.profiles[0].scientificName);
        }
    };

    self.toggleSearchOptions = function () {
        self.showSearchOptions = !self.showSearchOptions;
    };

    self.setSearchOption = function (option) {
        self.searchOptions.nameOnly = option == 'name'
    }
});