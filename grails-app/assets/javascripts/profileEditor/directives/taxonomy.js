profileEditor.directive('taxonomy', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            data: '=data',
            currentName: "@",
            opusId: '=',
            layout: '@',
            includeRank: '@',
            showChildren: '@',
            showChildrenForLastOnly: '@',
            showInfraspecific: '@',
            showWithProfileOnly: '@',
            limit: '@'
        },
        templateUrl: '/profileEditor/taxonomy.htm',
        controller: ['$scope', 'config', '$modal', 'messageService', 'profileService', function ($scope, config, $modal, messageService, profileService) {
            $scope.contextPath = config.contextPath;
            $scope.showChildren = false;
            $scope.showChildrenForLastOnly = false;
            $scope.showInfraspecific = false;
            $scope.showWithProfileOnly = false;
            $scope.includeRank = false;
            $scope.limit = -1;
            $scope.pageSize = 15;

            /**
             * Fetch all subordinate taxa (of any rank, not just the immediate children) and populate the 'children'
             * property of the taxon. This is used to display a drown-down list of subordinate taxa.
             */
            $scope.showAllSubordinateTaxaList = function(taxon) {
                if (_.isUndefined(taxon.children) || !taxon.children) {
                    taxon.loading = true;
                    var results = profileService.profileSearchByTaxonLevelAndName($scope.opusId, taxon.rank, taxon.name, $scope.pageSize, 0);
                    results.then(function (data) {
                            taxon.children = data;
                            taxon.loading = false;
                        },
                        function () {
                            taxon.loading = false;
                            messageService.alert("Failed to perform search for '" + taxon.rank + " " + taxon.name + "'.");
                        }
                    );
                }
            };

            /**
             * Executes showAllSubordinateTaxa when the directive is loaded and the last rank in hierarchy is Species.
             * This lets us display the infraspecific taxa on the page at page load, rather than on a click event
             */
            $scope.initialiseAllSubordinateTaxaList = function() {
                if ($scope.layout != "tree" && !_.isUndefined($scope.taxonomy) && $scope.showInfraspecific) {
                    var lastTaxon = $scope.taxonomy[$scope.taxonomy.length - 1];
                    if (!_.isUndefined(lastTaxon) && lastTaxon.rank == "species") {
                        $scope.showAllSubordinateTaxaList(lastTaxon);
                    }
                }
            };

            /**
             * Fetch all subordinate taxa (of any rank, not just the immediate children) and display a modal dialog with
             * pagination.
             */
            $scope.showAllSubordinateTaxaPopup = function (level, scientificName, childCount) {
                $modal.open({
                    templateUrl: "/profileEditor/showTaxonChildren.htm",
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

            /**
             * Fetch only the immediate children of the specified taxon (e.g. if taxon is a genus, then only get the
             * species, not the subspecies)
             */
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
                    $scope.loadSubordinateTaxa(0, taxon, false);
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
            };

            $scope.removeRanksWithNoProfile = function() {
                var tmp = angular.copy($scope.taxonomy);
                $scope.taxonomy = [];
                angular.forEach(tmp, function(taxon) {
                    if (taxon.profileId) {
                        $scope.taxonomy.push(taxon);
                    }
                });
            };
        }],
        link: function (scope) {
            scope.$watch("data", function(newValue) {
                if (!_.isUndefined(newValue)) {
                    scope.taxonomy = angular.copy(newValue);

                    scope.initialiseAllSubordinateTaxaList();
                }
            });
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
            scope.$watch("showChildrenForLastOnly", function(newValue) {
                if (!_.isUndefined(newValue)) {
                    scope.showChildrenForLastOnly = isTruthy(newValue);
                }
            });
            scope.$watch("showInfraspecific", function(newValue) {
                if (!_.isUndefined(newValue)) {
                    scope.showInfraspecific = isTruthy(newValue);

                    scope.initialiseAllSubordinateTaxaList();
                }
            });
            scope.$watch("showWithProfileOnly", function(newValue) {
                if (!_.isUndefined(newValue)) {
                    scope.showWithProfileOnly = isTruthy(newValue);
                    if (scope.showWithProfileOnly && !_.isUndefined(scope.taxonomy)) {
                        scope.removeRanksWithNoProfile();
                    }
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