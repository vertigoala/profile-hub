profileEditor.directive('taxonomy', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            taxonomy: '=data',
            currentName: "@",
            opusId: '=',
            layout: '@',
            includeRank: '@',
            showChildren: '@',
            limit: '@'
        },
        templateUrl: $browser.baseHref() + 'static/templates/taxonomy.html',
        controller: ['$scope', 'config', '$modal', 'messageService', 'profileService', function ($scope, config, $modal, messageService, profileService) {
            $scope.contextPath = config.contextPath;
            $scope.showChildren = false;
            $scope.includeRank = false;
            $scope.limit = -1;
            $scope.pageSize = 15;

            $scope.fetchChildren = function (level, scientificName, childCount) {
                $modal.open({
                    templateUrl: "showTaxonChildren.html",
                    controller: function (profileService, messageService, $modalInstance, taxon, contextPath, opusId) {
                        var self = this;

                        self.pageSize = 10;
                        self.taxon = taxon;
                        self.opusId = opusId;
                        self.contextPath = contextPath;

                        self.loadChildren = function (offset) {
                            if (offset === undefined) {
                                offset = 0;
                            }

                            var results = profileService.profileSearchByTaxonLevelAndName(self.opusId, taxon.rank, taxon.name, self.pageSize, offset);
                            results.then(function (data) {
                                    self.profiles = data;
                                },
                                function () {
                                    messageService.alert("Failed to perform search for '" + taxon.rank + " " + taxon.name + "'.");
                                }
                            );
                        };

                        self.loadChildren(0);

                        self.cancel = function () {
                            $modalInstance.dismiss("cancel");
                        }
                    },
                    controllerAs: "taxonChildrenCtrl",
                    size: "lg",
                    resolve: {
                        taxon: function () {
                            return {rank: level, name: scientificName, count: childCount};
                        },
                        contextPath: function () {
                            return $scope.contextPath;
                        },
                        opusId: function () {
                            return $scope.opusId;
                        }
                    }
                });
            };

            $scope.loadSubordinateTaxa = function (offset, taxon, openCloseSection) {
                if (openCloseSection) {
                    taxon.expanded = !taxon.expanded || false;

                    if (offset > 0 || taxon.showingCurrentProfileOnly) {
                        return;
                    }
                }

                if (taxon.expanded) {
                    if (_.isUndefined(offset) || offset < 0) {
                        offset = 0;
                    }

                    var results = profileService.profileSearchGetImmediateChildren($scope.opusId, taxon.rank, taxon.name, $scope.pageSize, offset, taxon.filter);
                    taxon.loading = true;
                    results.then(function (data) {
                            if (!_.isUndefined(data) && data.length > 0) {
                                if (offset > 0) {
                                    angular.forEach(data, function(child) {
                                        taxon.children.push(child);
                                    });
                                } else {
                                    taxon.children = data;
                                }
                                taxon.offset = (taxon.offset || 0) + $scope.pageSize;
                                taxon.showingCurrentProfileOnly = false;

                                taxon.mightHaveMore = data.length >= $scope.pageSize;
                            } else {
                                if (!_.isUndefined(taxon.filter) && taxon.filter) {
                                    taxon.children = [];
                                }
                                taxon.mightHaveMore = false;
                            }
                            taxon.loading = false;
                        },
                        function () {
                            taxon.loading = false;
                            messageService.alert("Failed to perform search for '" + taxon.rank + " " + taxon.name + "'.");
                        }
                    );
                }
            };

            $scope.filterChanged = function(taxon) {
                taxon.offset = 0;
                if (_.isUndefined(taxon.filter) || taxon.filter.trim().length == 0) {
                    $scope.loadSubordinateTaxa(0, taxon, true);
                }
            };

            $scope.hierarchialiseTaxonomy = function() {
                var previous = null;

                if ($scope.layout == 'tree') {
                    var tmp = angular.copy($scope.taxonomy);
                    angular.forEach (tmp, function (next) {
                        if (previous != null) {
                            previous.children = [next];
                            previous.expanded = true;
                            previous.offset = -1;
                            previous.showingCurrentProfileOnly = true;
                            previous.mightHaveMore = true;
                            previous.filter = null;
                        }
                        previous = next;
                    });
                    $scope.hierarchy = [tmp[0]];
                }
            }
        }],
        link: function (scope) {
            scope.$watch("includeRank", function(newValue) {
                if (!_.isUndefined(newValue)) {
                    scope.includeRank = isTruthy(newValue);
                }
            });
            scope.$watch("showChildren", function(newValue) {
                if (!_.isUndefined(newValue)) {
                    scope.showChildren = isTruthy(newValue);
                }
            });

            scope.$watch("layout", function(newValue) {
                if (!_.isUndefined(newValue)) {
                    scope.layout = newValue;
                }

                if (scope.layout == "tree" && !_.isUndefined(scope.taxonomy)) {
                    scope.hierarchialiseTaxonomy();
                }
            });
        }
    };

    function isTruthy(str) {
        return str == true || str === "true"
    }
});