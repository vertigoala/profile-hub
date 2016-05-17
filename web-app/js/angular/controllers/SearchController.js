/**
 * Profile Search controller
 */
profileEditor.controller('SearchController', function (profileService, util, messageService, $filter, $sessionStorage) {
    var self = this;

    self.opusId = util.getEntityId("opus");

    self.searchOptions = {
        matchAll: true,
        includeArchived: false,
        nameOnly: false,
        includeNameAttributes: true,
        searchAla: true,
        searchNsl: true
    };
    self.searchResults = {};

    self.pageSize = 25;
    self.showOptions = false;

    self.search = function (pageSize, offset) {
        self.searchOptions.offset = offset || 0;
        self.searchOptions.pageSize = pageSize || self.pageSize;

        if (!_.isUndefined(self.searchTerm) && !_.isEmpty(self.searchTerm)) {
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
                }
            );
        }
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

    self.retrieveCachedOrDelegatedSearch = function() {
        if ($sessionStorage.delegatedSearches && $sessionStorage.delegatedSearches[self.opusId ? self.opusId : 'all'] != null) {
            var delegatedSearch = $sessionStorage.delegatedSearches[self.opusId ? self.opusId : 'all'];
            self.searchTerm = delegatedSearch.term;
            self.searchOptions = delegatedSearch.searchOptions ? delegatedSearch.searchOptions : _.clone(self.searchOptions);

            delete $sessionStorage.delegatedSearches[self.opusId ? self.opusId : 'all'];

            self.search();
        } else {
            var cachedResult = $sessionStorage.searches ? $sessionStorage.searches[self.opusId ? self.opusId : 'all'] : {};
            if (cachedResult) {
                self.searchResults = cachedResult.result;
                self.searchTerm = cachedResult.term;
                self.searchOptions = cachedResult.options ? cachedResult.options : _.clone(self.searchOptions);
            }
        }
    };

    self.clearSearch = function () {
        self.searchResults = {};
        self.searchTerm = "";

        $sessionStorage.searches[self.opusId ? self.opusId : 'all'] = {};

        self.resetSearchOptions();
    };

    self.resetSearchOptions = function() {
        // don't reset the nameOnly option so the user remains on the search type they selected
        var selectedSearchType = self.searchOptions.nameOnly;
        self.searchOptions = {
            matchAll: true,
            includeArchived: false,
            includeNameAttributes: true,
            searchAla: true,
            searchNsl: true,
            nameOnly: selectedSearchType
        };
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

    self.setSearchOption = function (option) {
        self.searchOptions.nameOnly = option == 'name'
    };

    self.retrieveCachedOrDelegatedSearch();

});