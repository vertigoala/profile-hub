/**
 * Classifications controller
 */
profileEditor.controller('ClassificationsController', function ($scope, profileService, util, messageService) {

    $scope.classifications = [];

    $scope.init = function (edit) {
        $scope.readonly = edit != 'true';

        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));
        messageService.info("Loading profile data...");
        profilePromise.success(function (data, status, headers, config) {
            messageService.pop();
            $scope.profile = data.profile;
            $scope.opus = data.opus;

            loadClassifications();
        });
        profilePromise.error(function (data, status, headers, config) {
            console.log("There was a problem retrieving profile..." + status);
            messageService.alert("An error occurred while retrieving the profile.");
        });
    };

    function loadClassifications() {
        messageService.info("Loading taxonomy...");

        var promise = profileService.getClassifications($scope.profile.guid);
        promise.success(function (data, status, headers, config) {
            console.log("Fetched " + data.classifications.length + " classifications");
            $scope.classifications = data.classifications;
            $scope.speciesProfile = data.speciesProfile;
            messageService.pop();
        });
        promise.error(function (data, status, headers, config) {
            console.log("There was a problem retrieving classification for profile..." + status);
            messageService.alert("An error occurred while retrieving the taxonomy.");
        });
    };
});