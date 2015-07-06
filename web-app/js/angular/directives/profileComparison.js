profileEditor.directive('profileComparison', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            left: '=',
            right: '=',
            leftTitle: '@',
            rightTitle: '@'
        },
        templateUrl: $browser.baseHref() + 'static/templates/profileComparison.html',
        controller: ['$scope', '$filter', function ($scope, $filter) {
            $scope.diff = {};

            var orderBy = $filter("orderBy");

            $scope.compareProfiles = function() {
                console.log($scope.left)
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
                $scope.diff.lastAttributeChange = compare($scope.left.lastAttributeChange, $scope.right.lastAttributeChange);
                $scope.diff.attributes = compareLists($scope.left.attributes, $scope.right.attributes, ["title", "plainText"]);
                $scope.diff.specimenIds = compare(
                    $scope.left.specimenIds ? $scope.left.specimenIds.join(", ") : "",
                    $scope.right.specimenIds ? $scope.right.specimenIds.join(", ") : "");
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
        }
    };

    function compare(left, right) {
        var d = new diff_match_patch();
        var diff = d.diff_main(right ? right : "", left ? left : "");
        var changed = false;
        angular.forEach(diff, function(d) {
            if (d[0] != 0) {
                changed = true;
            }
        });
        console.log(left + " / " + right + " - " + JSON.stringify(diff) + " - " + changed)
        return {changed: changed, comp: d.diff_prettyHtml(diff), left: left, right: right};
    }

    function compareLists(left, right, keyField, compareFields) {
        var comparisons = [];
        var changed = false;
        angular.forEach(right, function(rightItem) {
            console.log("1 " + rightItem.category)
            var oldInNew = false;
            var comparison = null;
            angular.forEach(left, function(leftItem) {
                console.log("2 " + leftItem.category)
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
                    console.log(rightItem.category + " old in new")
                }
            });

            if (!oldInNew) {
                console.log(rightItem.category + " old NOT in new")

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
            console.log("3 " + leftItem.category)
            angular.forEach(right, function(rightItem) {
                console.log("4 " + rightItem.category);
                if (leftItem[keyField] === rightItem[keyField]) {
                    console.log(leftItem.category + " new in old")
                    newInOld = true;
                }
            });

            if (!newInOld) {
                console.log(leftItem.category + " new NOT in old")

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