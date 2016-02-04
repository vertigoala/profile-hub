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
            $scope.pageSize = 10;

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

                            var results = profileService.profileSearchByTaxonLevelAndName(self.opusId, taxon.level, taxon.scientificName, self.pageSize, offset);
                            results.then(function (data) {
                                    self.profiles = data;
                                },
                                function () {
                                    messageService.alert("Failed to perform search for '" + self.searchTerm + "'.");
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
                            return {level: level, scientificName: scientificName, count: childCount};
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


            $scope.loadSubordinateTaxa = function (offset, taxon) {
                taxon.expanded = !taxon.expanded | false;

                if (taxon.expanded) {
                    if (offset === undefined) {
                        offset = 0;
                    }

                    //var results = profileService.profileSearchByTaxonLevelAndName($scope.opusId, taxon.rank, taxon.name, $scope.pageSize, offset);
                    var results = profileService.profileSearchByTaxonLevel($scope.opusId, taxon.rank, $scope.pageSize, offset);
                    results.then(function (data) {
                            taxon.children = data;
                            console.log(JSON.stringify(taxon, undefined, 2))
                        },
                        function () {
                            messageService.alert("Failed to perform search for '" + $scope.searchTerm + "'.");
                        }
                    );
                }
            };
        }],
        link: function (scope, element, attrs, ctrl) {}
    };
});