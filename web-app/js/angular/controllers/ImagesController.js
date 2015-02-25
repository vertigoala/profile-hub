/**
 * Images controller
 */
profileEditor.controller('ImagesController', function ($scope, profileService, util, messageService) {

    $scope.images = [];

    $scope.init = function (edit) {
        $scope.readonly = edit != 'true';
        $scope.imagesSlideShowInterval = 5000; // milliseconds
        $scope.slides = [];
        $scope.images = [];

        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));

        profilePromise.then(function (data) {
                $scope.profile = data.profile;
                $scope.opus = data.opus;

                loadImages()
            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    function loadImages() {
        messageService.info("Loading images...");

        var searchIdentifier = $scope.profile.guid ? "lsid:" + $scope.profile.guid : $scope.profile.scientificName;
        var imagesPromise = profileService.retrieveImages(searchIdentifier, $scope.opus.imageSources.join());

        imagesPromise.then(function (data) {
                console.log("Fetched " + data.occurrences.length + " images");

                $scope.firstImage = data.occurrences[0];
                $scope.images = data.occurrences;

                angular.forEach(data.occurrences, function (image) {
                    $scope.slides.push({
                        image: image.largeImageUrl,
                        text: image.dataResourceName
                    })
                }, $scope.slides);

                messageService.pop();
            },
            function () {
                messageService.alert("An error occurred while retrieving the images.");
            }
        );
    }

    $scope.addImage = function () {
        alert("Not implemented yet. Would upload to biocache & store image in image service");
    };
});