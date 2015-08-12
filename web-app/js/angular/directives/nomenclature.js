profileEditor.directive('nomenclature', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            nslNameId: '=',
            nslNomenclatureId: '=',
            readonly: '@'
        },
        templateUrl: $browser.baseHref() + 'static/templates/nomenclature.html',
        controller: ['$scope', '$http', 'profileService', function ($scope, $http, profileService) {
            $scope.selectedReference = null;
            $scope.nslNomenclatureId = null;
            $scope.loading = false;

            $scope.loadConcepts = function () {
                $scope.loading = true;
                if (!$scope.references) {
                    profileService.getNomenclatureList($scope.nslNameId).
                        then(function (resp) {
                            $scope.references = [];
                            console.log(resp.references.length + " references for id "+ $scope.nslNameId)
                            angular.forEach(resp.references, function (reference) {
                                var referenceUrl = reference._links.permalink.link;
                                var referenceId = referenceUrl.substring(referenceUrl.lastIndexOf("/") + 1);
                                var name = reference.citation;
                                if (isTruthy(reference.APCReference)) {
                                    name += " (APC)";
                                }
                                var formattedName = reference.citationHtml;
                                var citations = [];

                                var firstInstanceId = null;
                                angular.forEach(reference.citations, function (citation) {
                                    citations.push(citation.relationship);

                                    var citationUrl = citation.instance._links.permalink.link;
                                    var instanceId =  citationUrl.substring(citationUrl.lastIndexOf("/") + 1);

                                    if (!firstInstanceId) {
                                        firstInstanceId = instanceId;
                                    }
                                });

                                var ref = {
                                    instanceId: firstInstanceId,
                                    referenceId: referenceId,
                                    url: referenceUrl,
                                    name: name,
                                    formattedName: formattedName,
                                    citations: citations,
                                    apcReference: isTruthy(reference.APCReference)
                                };

                                if (firstInstanceId == $scope.nslNomenclatureId) {
                                    $scope.selectedReference = ref;
                                    console.log(JSON.stringify(ref))
                                } else if ($scope.nslNomenclatureId) {
                                    console.log("No matching nomenclature found for id " + $scope.nslNomenclatureId);
                                }

                                $scope.references.push(ref);
                            });

                            $scope.loading = false;
                        }
                    );
                }
            };

            $scope.toggleSynonomy = function() {
                $scope.showSynonomy = !$scope.showSynonomy;
            };
        }
        ],
        link: function (scope, element, attrs, ctrl) {
            scope.$watch("nslNameId", function (newValue) {
                if (newValue !== undefined) {
                    scope.nslNameId = newValue;
                    scope.loadConcepts();
                }
            });
            scope.$watch("nslNomenclatureId", function (newValue) {
                if (newValue !== undefined) {
                    scope.nslNomenclatureId = newValue;

                    if (!scope.selectedReference && scope.nslNameId) {
                        scope.loadConcepts();
                    }
                }
            });
            scope.$watch("selectedReference", function (newValue) {
                if (newValue !== undefined) {
                    scope.selectedReference = newValue;
                    scope.nslNomenclatureId = newValue == null ? null : newValue.instanceId;
                }
            }, true);
            scope.$watch("readonly", function (newValue) {
                scope.readonly = isTruthy(newValue);
            });
        }
    };

    function isTruthy(str) {
        return str == true || str === "true"
    }

});