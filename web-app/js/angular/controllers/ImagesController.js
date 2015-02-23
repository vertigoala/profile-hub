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
        profilePromise.success(function (data) {
            $scope.profile = data.profile;
            $scope.opus = data.opus;
            loadImages()
        });
        profilePromise.error(function (data, status, headers, config) {
            console.log("There was a problem retrieving profile..." + status);
            messageService.alert("An error occurred while retrieving the profile.");
        });
    };

    function loadImages() {
        messageService.info("Loading images...");

        var searchIdentifier = $scope.profile.guid ? "lsid:" + $scope.profile.guid : $scope.profile.scientificName;
        var imagesPromise = profileService.retrieveImages(searchIdentifier, $scope.opus.imageSources.join());

        imagesPromise.success(function (data, status, headers, config) {
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
        });
        imagesPromise.error(function (data, status, headers, config) {
            console.log("There was a problem retrieving images..." + status);
            messageService.alert("An error occurred while retrieving the images.");
        });
    };


    $scope.addImage = function () {
        alert("Not implemented yet. Would upload to biocache & store image in image service");
    };
});