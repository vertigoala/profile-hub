/**
 * Species Lists controller
 */
profileEditor.controller('ListsEditor', function ($scope, profileService, util, messageService) {

    $scope.attributes = [];
    $scope.attributeTitles = [];
    $scope.lists = [];

    $scope.init = function (edit) {
        $scope.readonly = edit != 'true';

        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));
        messageService.info("Loading profile data...");
        profilePromise.success(function (data, status, headers, config) {
            messageService.pop();
            $scope.profile = data.profile;

            loadLists();
        });
        profilePromise.error(function (data, status, headers, config) {
            console.log("There was a problem retrieving profile..." + status);
            messageService.alert("An error occurred while retrieving the profile.");
        });
    };

    function loadLists() {
        messageService.info("Loading lists...");

        var listsPromise = profileService.retrieveLists($scope.profile.guid);

        listsPromise.success(function (data, status, headers, config) {
            console.log("Fetched " + data.length + " lists");

            $scope.lists = data;
console.log($scope.lists[0].dataResourceUid);
            messageService.pop();
        });
        listsPromise.error(function (data, status, headers, config) {
            console.log("There was a problem retrieving lists..." + status);
            messageService.alert("An error occurred while retrieving the lists.");
        });
    };
});