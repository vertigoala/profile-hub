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
                    $scope.diff.excludedImages = compare(
                        $scope.left.excludedImages ? $scope.left.excludedImages.join(", ") : "",
                        $scope.right.excludedImages ? $scope.right.excludedImages.join(", ") : "");
                }
            };

            $scope.getImageUrl = function(imageId) {
                return config.imageServiceUrl + "/image/proxyImageThumbnailLarge?imageId=" + imageId;
            };

            $scope.compareProfiles();
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
                if (leftItem[keyField] === rightItem[keyField]) {
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
                if (leftItem[keyField] === rightItem[keyField]) {
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