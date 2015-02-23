/**
 * Taxon controller
 */
profileEditor.controller('TaxonController', function ($scope, profileService, util, messageService) {

    $scope.speciesProfile = null;
    $scope.classifications = null;

    $scope.init = function (edit) {
        $scope.readonly = edit != 'true';

        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));
        messageService.info("Loading profile data...");
        profilePromise.success(function (data, status, headers, config) {
            messageService.pop();
            $scope.profile = data.profile;
            $scope.opus = data.opus;

            loadSpeciesProfile();
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
            console.log("Fetched " + data.length + " classifications");
            $scope.classifications = data;
            messageService.pop();
        });
        promise.error(function (data, status, headers, config) {
            console.log("There was a problem retrieving classification for profile..." + status);
            messageService.alert("An error occurred while retrieving the taxonomy.");
        });
    };

    function loadSpeciesProfile() {
        messageService.info("Loading taxon...");

        var promise = profileService.getSpeciesProfile($scope.profile.guid);
        promise.success(function (data, status, headers, config) {
            console.log("Fetched species profile");
            $scope.speciesProfile = data;
            messageService.pop();
        });
        promise.error(function (data, status, headers, config) {
            console.log("There was a problem retrieving species profile for profile..." + status);
            messageService.alert("An error occurred while retrieving the taxonomy.");
        });
    };
});