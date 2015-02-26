/**
 * BHL Links controller
 */
profileEditor.controller('BHLLinksEditor', function ($scope, profileService, util, messageService) {

    $scope.bhl = [];

    $scope.init = function (edit) {
        $scope.readonly = edit != 'true';

        var future = profileService.getProfile(util.getPathItem(util.LAST));

        future.then(function (data) {
            $scope.profile = data.profile;
            $scope.opus = data.opus;
            $scope.bhl = data.profile.bhl;
            console.log("Fetched " + $scope.bhl.length + " BHL links");
        },
        function () {
            messageService.alert("An error occurred while retrieving the Biodiversity Heritage References.");
        });
    };

    $scope.updateThumbnail = function (idx) {
        console.log("Updating...");
        var url = $scope.bhl[idx].url.trim();
        if (url) {
            var pageId = util.getPathItemFromUrl(util.LAST, url);

            var bhlPromise = profileService.lookupBhlPage(pageId);
            bhlPromise.then(function (data) {
                    $scope.bhl[idx].thumbnailUrl = data.thumbnailUrl;
                    $scope.bhl[idx].fullTitle = data.Result.FullTitle;
                    $scope.bhl[idx].edition = data.Result.Edition;
                    $scope.bhl[idx].publisherName = data.Result.PublisherName;
                    $scope.bhl[idx].doi = data.Result.Doi;
                },
                function () {
                    messageService.alert("Failed to lookup page information from the biodiversity heritage library.");
                }
            );
        }
    };

    $scope.addLink = function () {
        $scope.bhl.unshift(
            {
                url: "",
                description: "",
                title: "",
                thumbnailUrl: ""
            });
    };

    $scope.deleteLink = function (idx) {
        $scope.bhl.splice(idx, 1);
    };

    $scope.saveLinks = function () {
        var promise = profileService.updateBhlLinks($scope.profile.uuid, JSON.stringify({
            profileId: $scope.profile.uuid,
            links: $scope.bhl
        }));
        promise.then(function () {
                messageService.success("Links successfully updated.");
            },
            function () {
                messageService.alert("An error occurred while updating the links.");
            }
        );
    };
});
