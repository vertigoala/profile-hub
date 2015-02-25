/**
 *  Links controller
 */
profileEditor.controller('LinksEditor', function ($scope, profileService, util, messageService) {
    $scope.links = [];

    $scope.init = function (edit) {
        $scope.readonly = edit != 'true';

        loadLinks();
    };

    function loadLinks() {
        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));
        profilePromise.then(function (data) {
                $scope.profile = data.profile;
                $scope.opus = data.opus;
                $scope.links = data.profile.links;
            },
            function () {
                messageService.alert("An error occurred while retrieving the links.");
            }
        );
    }

    $scope.addLink = function () {
        $scope.links.unshift({uuid: null, url: "http://", description: "", title: ""});
    };

    $scope.deleteLink = function (idx) {
        $scope.links.splice(idx, 1);
    };

    $scope.saveLinks = function () {
        var promise = profileService.updateLinks($scope.profile.uuid, JSON.stringify({profileId: $scope.profile.uuid, links: $scope.links}));
        promise.then(function () {
                messageService.success("Links successfully updated.");
                loadLinks();
            },
            function () {
                messageService.alert("An error occurred while updating the links.");
            }
        );
    };
});