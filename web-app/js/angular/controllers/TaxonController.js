/**
 * Taxon controller
 */
profileEditor.controller('TaxonController', function ($scope, profileService, util, messageService) {

    $scope.speciesProfile = null;
    $scope.classifications = [];

    $scope.init = function (edit) {
        $scope.readonly = edit != 'true';

        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));
        profilePromise.then(function (data) {
                $scope.profile = data.profile;
                $scope.opus = data.opus;

                loadSpeciesProfile();
                loadClassifications();
            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    function loadClassifications() {
        messageService.info("Loading taxonomy...");

        var promise = profileService.getClassifications($scope.profile.guid);
        promise.then(function (data) {
                console.log("Fetched " + data.length + " classifications");

                $scope.classifications = data;
                messageService.pop();
            },
            function () {
                messageService.alert("An error occurred while retrieving the taxonomy.");
            }
        );
    }

    function loadSpeciesProfile() {
        messageService.info("Loading taxon...");

        var promise = profileService.getSpeciesProfile($scope.profile.guid);
        promise.then(function (data) {
                console.log("Fetched species profile");

                $scope.speciesProfile = data;
                messageService.pop();
            },
            function () {
                messageService.alert("An error occurred while retrieving the taxonomy.");
            }
        );
    }
});