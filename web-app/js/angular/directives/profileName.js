profileEditor.directive('profileName', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            name: '=',
            valid: '=',
            currentProfileId: '='
        },
        templateUrl: $browser.baseHref() + 'static/templates/profileNameCheck.html',
        controller: ['$scope', 'profileService', 'util', function ($scope, profileService, util) {
            $scope.nameCheck = null;
            $scope.opusId = util.getEntityId("opus");

            $scope.resetNameCheck = function () {
                $scope.nameCheck = null;
                $scope.errors = [];
                $scope.warnings = [];
                $scope.valid = false;
            };

            $scope.formatName = function (scientificName, nameAuthor, fullName) {
                return util.formatScientificName(scientificName, nameAuthor, fullName);
            };

            $scope.checkName = function() {
                if (!$scope.name) {
                    return;
                }

                $scope.resetNameCheck();

                var promise = profileService.checkName($scope.opusId, $scope.name);
                promise.then(function (report) {
                        $scope.nameCheck = report;

                        $scope.nameCheck.matchedName.formattedName = util.formatScientificName(report.matchedName.scientificName, report.matchedName.nameAuthor, report.matchedName.fullName);

                        $scope.nameCheck.providedNameDuplicate = report.providedNameDuplicates.length > 0 && report.providedNameDuplicates[0].profileId != $scope.currentProfileId;
                        $scope.nameCheck.noMatch = !report.matchedName || !report.matchedName.scientificName;
                        $scope.nameCheck.mismatch = report.matchedName && report.matchedName.scientificName != $scope.name && report.matchedName.fullName != $scope.name;
                        $scope.nameCheck.matchedNameDuplicate = $scope.nameCheck.mismatch && report.matchedNameDuplicates.length > 0 && report.matchedNameDuplicates[0].profileId != $scope.currentProfileId;

                        $scope.valid = $scope.nameCheck != null && !$scope.nameCheck.providedNameDuplicate;
                    },
                    function () {
                        $scope.errors.push("An error occurred while checking the name.");
                    }
                );
            };

            $scope.useMatchedName = function() {
                $scope.name = $scope.nameCheck.matchedName.scientificName;

                $scope.checkName();
            };

        }],
        link: function (scope, element, attrs, ctrl) {

        }
    }
});