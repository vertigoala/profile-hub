profileEditor.directive('profileComparison', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            left: '=',
            right: '=',
            leftTitle: '@',
            rightTitle: '@',
            showEverything: '@'
        },
        templateUrl: $browser.baseHref() + 'static/templates/profileComparison.html',
        controller: ['$scope', '$filter', 'config', function ($scope, $filter, config) {
            $scope.diff = {};
            $scope.showEverything = false;

            var orderBy = $filter("orderBy");

            $scope.compareProfiles = function() {
                if ($scope.left && $scope.right) {
                    $scope.diff.fullName = compare($scope.left.fullName, $scope.right.fullName);
                    $scope.diff.matchedName = {
                        fullName: compare($scope.left.matchedName ? $scope.left.matchedName.fullName : null,
                            $scope.right.matchedName ? $scope.right.matchedName.fullName : null)
                    };

                    $scope.diff.authorship = compareLists($scope.left.authorship, $scope.right.authorship, "category", ["text"]);
                    $scope.diff.bibliography = compareLists($scope.left.bibliography, $scope.right.bibliography, "plainText", ["plainText"]);
                    $scope.diff.links = compareLists($scope.left.links, $scope.right.links, "uuid", ["url", "title", "description"]);
                    $scope.diff.bhl = compareLists($scope.left.bhl, $scope.right.bhl, "uuid", ["url", "title", "description"]);
                    $scope.diff.primaryImage = compare($scope.left.primaryImage, $scope.right.primaryImage);
                    if ((!$scope.left.attributes && !$scope.right.attributes) || ($scope.left.attributes.length == 0 && $scope.right.attributes.length == 0)) {
                        $scope.diff.lastAttributeChange = compare($scope.left.lastAttributeChange, $scope.right.lastAttributeChange);
                    }
                    $scope.diff.attributes = compareLists($scope.left.attributes, $scope.right.attributes, "title", ["plainText"]);
                    $scope.diff.specimenIds = compare(
                        $scope.left.specimenIds ? $scope.left.specimenIds.join(", ") : "",
                        $scope.right.specimenIds ? $scope.right.specimenIds.join(", ") : "");
                    $scope.diff.imageDisplayOptions = compareLists($scope.left.imageDisplayOptions, $scope.right.imageDisplayOptions, "imageId", ["displayOption"]);
                    $scope.diff.nslNomenclatureIdentifier = compare($scope.left.nslNomenclatureIdentifier, $scope.right.nslNomenclatureIdentifier);
                    $scope.diff.scientificName = compare($scope.left.scientificName, $scope.right.scientificName);
                    $scope.diff.archivedDate = compare($scope.left.archivedDate, $scope.right.archivedDate);
                    $scope.diff.privateMode = compare($scope.left.privateMode+"", $scope.right.privateMode+"");// convert boolean to string
                    var attachmentFields = getAttachmentFields($scope.left, $scope.right);
                    $scope.diff.attachments = compareLists($scope.left.attachments, $scope.right.attachments, "uuid", attachmentFields);

                    var imageFields = ["license", "originalFileName", "rights", "rightsHolder", "title", "description"];
                    $scope.diff.privateImages = compareLists($scope.left.privateImages, $scope.right.privateImages, "imageId", imageFields);
                    $scope.diff.stagedImages = compareLists($scope.left.stagedImages, $scope.right.stagedImages, "imageId", imageFields);

                }
            };

            $scope.getImageUrl = function(profile, imageId) {
                var url = null;

                var privateImage = _.find(profile.privateImages, function(item) { return item.imageId == imageId });
                if (!_.isUndefined(privateImage)) {
                    var extension = privateImage.originalFileName.substring(privateImage.originalFileName.lastIndexOf("."));
                    url = config.contextPath + "/opus/" + profile.opusId + "/profile/" + profile.uuid + "/image/" + imageId + extension + "?type=PRIVATE";
                } else {
                    var stagedImage = _.find(profile.stagedImages, function(item) { return item.imageId == imageId });
                    if (!_.isUndefined(stagedImage)) {
                        var extension = stagedImage.originalFileName.substring(stagedImage.originalFileName.lastIndexOf("."));
                        url = config.contextPath + "/opus/" + profile.opusId + "/profile/" + profile.uuid + "/image/" + imageId + extension + "?type=STAGED";
                    } else {
                        url = config.imageServiceUrl + "/image/proxyImageThumbnail?imageId=" + imageId;
                    }
                }

                return url;
            };

            $scope.compareProfiles();

            function getAttachmentFields(left, right) {
                var fields = [];

                if (!_.isUndefined(left.attachments) && left.attachments.length > 0) {
                    fields = Object.keys($scope.left.attachments[0]);
                } else if (!_.isUndefined(right.attachments) && right.attachments.length > 0) {
                    fields = Object.keys($scope.right.attachments[0])
                }

                return fields;
            }
        }],
        link: function (scope, element, attrs, ctrl) {
            scope.$watch("left", function(newValue) {
                if (newValue !== undefined) {
                    scope.left = newValue;
                    scope.compareProfiles();
                }
            });
            scope.$watch("right", function(newValue) {
                if (newValue !== undefined) {
                    scope.right = newValue;
                    scope.compareProfiles();
                }
            });
            scope.$watch("showEverything", function(newValue) {
                scope.showEverything = isTruthy(newValue);
            });
        }
    };

    function isTruthy(str) {
        return str == true || str === "true"
    }

    function compare(left, right) {
        var d = new diff_match_patch();
        var diff = d.diff_main(right ? right : "", left ? left : "");
        var changed = false;
        angular.forEach(diff, function(d) {
            if (d[0] != 0) {
                changed = true;
            }
        });
        return {changed: changed, comp: d.diff_prettyHtml(diff), left: left, right: right};
    }

    function compareLists(left, right, keyField, compareFields) {
        var comparisons = [];
        var changed = false;
        angular.forEach(right, function(rightItem) {
            var oldInNew = false;
            var comparison = null;
            angular.forEach(left, function(leftItem) {
                if (leftItem[keyField] == rightItem[keyField]) {
                    comparison = {};
                    comparison[keyField] = leftItem[keyField];
                    angular.forEach(compareFields, function(compareField) {
                        comparison[compareField] = compare(leftItem[compareField], rightItem[compareField]);
                        if (comparison[compareField].changed) {
                            changed = true;
                        }
                    });
                    comparisons.push(comparison);
                    oldInNew = true;
                }
            });

            if (!oldInNew) {
                comparison = {};
                comparison[keyField] = null;
                angular.forEach(compareFields, function(compareField) {
                    comparison[compareField] = compare(null, rightItem[compareField]);
                    if (comparison[compareField].changed) {
                        changed = true;
                    }
                });
                comparisons.push(comparison);
            }
        });

        angular.forEach(left, function(leftItem) {
            var newInOld = false;
            angular.forEach(right, function(rightItem) {
                if (leftItem[keyField] == rightItem[keyField]) {
                    newInOld = true;
                }
            });

            if (!newInOld) {
                var comparison = {};
                comparison[keyField] = leftItem[keyField];
                angular.forEach(compareFields, function(compareField) {
                    comparison[compareField] = compare(leftItem[compareField], null);
                    if (comparison[compareField].changed) {
                        changed = true;
                    }
                });
                comparisons.push(comparison);
            }
        });

        return {changed: changed, comp: comparisons};
    }
});