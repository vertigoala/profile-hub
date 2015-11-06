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
            $scope.viewInNslLink = null;
            var IGNORE_STATUSES = ["legitimate", "[n/a]"];

            $scope.loadConcepts = function () {
                $scope.loading = true;
                if (!$scope.references) {
                    profileService.getNomenclatureList($scope.nslNameId).
                        then(function (resp) {
                            $scope.references = [];

                            angular.forEach(resp.references, function (reference) {
                                var referenceUrl = reference._links.permalink.link;
                                var referenceId = referenceUrl.substring(referenceUrl.lastIndexOf("/") + 1);
                                var name = reference.citation;
                                if (isTruthy(reference.APCReference)) {
                                    name += " (APC)";
                                }
                                var formattedName = reference.citationHtml;
                                if (reference.citations && reference.citations.length > 0 && reference.citations[0].page) {
                                    formattedName += ": " + reference.citations[0].page;
                                }
                                var details = [];

                                var firstInstanceId = null;
                                angular.forEach(reference.citations, function (citation) {
                                    var citationPage = citation.page;

                                    if (citation.relationship) {
                                        var text = citation.relationship;
                                        if (citation.page && citation.page != citationPage && citation.page != "-") {
                                            text += ": " + citation.page;
                                        }
                                        details.push(text);
                                    } else if (citation.relationships) {
                                        angular.forEach(citation.relationships, function (relationship) {
                                            text = relationship.relationship;
                                            if (relationship.page && relationship.page != citationPage && relationship.page != "-") {
                                                text += ": " + relationship.page;
                                            }
                                            details.push(text);
                                        });
                                    }


                                    var citationUrl = citation._links.permalink.link;
                                    var instanceId = citationUrl.substring(citationUrl.lastIndexOf("/") + 1);

                                    if (!firstInstanceId) {
                                        firstInstanceId = instanceId;
                                    }
                                });

                                angular.forEach(reference.notes, function (note) {
                                    if (note.instanceNoteKey === "Type") {
                                        details.unshift("<b>Type:</b> " + note.instanceNoteText);
                                    }
                                });

                                var ref = {
                                    instanceId: firstInstanceId,
                                    referenceId: referenceId,
                                    url: referenceUrl,
                                    name: name,
                                    formattedName: formattedName,
                                    details: details,
                                    apcReference: isTruthy(reference.APCReference)
                                };

                                if (firstInstanceId == $scope.nslNomenclatureId) {
                                    $scope.selectedReference = ref;
                                    $scope.viewInNslLink = reference.citations[0]._links.permalink.link;
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