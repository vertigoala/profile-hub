/**
 * Directive to display a generic search control e.g. in the page header.
 *
 * Search behaviour will depend on whether the URL contains an OpusId or not.
 *
 * The search action redirects the user to either the Collection landing page's search tab, or the Collection home page
 * search tab, and populates the session with the search term, which the main SearchController will check and execute the
 * search. I.e. this directive does not actually perform a search - it merely sends the user to the appropriate search
 * page.
 */
profileEditor.directive('delegatedSearch', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            layout: '@'
        },
        templateUrl: $browser.baseHref() + 'static/templates/delegatedSearch.html',
        controller: ['$scope', '$sessionStorage', 'util', function ($scope, $sessionStorage, util) {
            var self = this;
            self.layout = $scope.layout || 'small';

            self.textSearch = {key: 'T', value: 'text'};
            self.nameSearch = {key: 'N', value: 'name'};

            self.searchOptions = {
                includeArchived: false,
                matchAll: true,
                nameOnly: false,
                includeNameAttributes: true,
                searchAla: true,
                searchNsl: true
            };
            self.showOptions = false;

            self.searchTerm = null;
            self.contextPath = $browser.baseHref();

            self.search = function() {
                if (!self.searchTerm || _.isUndefined(self.searchTerm) || self.searchTerm.trim().length == 0) {
                    return;
                }

                var opusId = util.getEntityId("opus");
                if (!$sessionStorage.delegatedSearches || _.isUndefined($sessionStorage.delegatedSearches)) {
                    $sessionStorage.delegatedSearches = {};
                }
                $sessionStorage.delegatedSearches[opusId ? opusId : 'all'] = {
                    term: self.searchTerm,
                    searchOptions: self.searchOptions
                };

                if (_.isUndefined(opusId) || opusId == null || !opusId) {
                    util.redirect(util.contextRoot() + "/opus/search");
                } else {
                    util.redirect(util.contextRoot() + "/opus/" + opusId);
                }
            };

            self.setSearchOption = function (option) {
                self.searchOptions.nameOnly = option == 'name'
            };
        }],
        controllerAs: "delSearchCtrl",
        link: function (scope, element, attrs, ctrl) {
            scope.$watch("layout", function(newValue) {
                if (!_.isUndefined(newValue)) {
                    scope.layout = newValue;
                }
            });
        }
    };
});

