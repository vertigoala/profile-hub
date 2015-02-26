/**
 * Species Lists controller
 */
profileEditor.controller('ListsEditor', function ($scope, profileService, util, messageService) {

    $scope.lists = [];

    $scope.init = function (edit) {
        $scope.readonly = edit != 'true';

        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));
        profilePromise.then(function (data) {
                $scope.profile = data.profile;
                $scope.opus = data.opus;

                loadLists();
            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    function loadLists() {
        if ($scope.profile.guid) {
            messageService.info("Loading lists...");

            var listsPromise = profileService.retrieveLists($scope.profile.guid);

            listsPromise.then(function (data) {
                    console.log("Fetched " + data.length + " lists");

                    $scope.lists = data;

                    messageService.pop();
                },
                function () {
                    messageService.alert("An error occurred while retrieving the lists.");
                }
            );
        }
    }
});