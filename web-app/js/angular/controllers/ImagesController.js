/**
 * Images controller
 */
profileEditor.controller('ImagesController', function (profileService, util, messageService) {
    var self = this;
    
    self.images = [];

    self.init = function (edit) {
        self.readonly = edit != 'true';
        self.imagesSlideShowInterval = 5000; // milliseconds
        self.slides = [];
        self.images = [];

        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));

        profilePromise.then(function (data) {
                self.profile = data.profile;
                self.opus = data.opus;

                loadImages()
            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    function loadImages() {
        messageService.info("Loading images...");

        var searchIdentifier = self.profile.guid ? "lsid:" + self.profile.guid : self.profile.scientificName;
        var imagesPromise = profileService.retrieveImages(searchIdentifier, self.opus.imageSources.join());

        imagesPromise.then(function (data) {
                console.log("Fetched " + data.occurrences.length + " images");

                self.firstImage = data.occurrences[0];
                self.images = data.occurrences;

                angular.forEach(data.occurrences, function (image) {
                    self.slides.push({
                        image: image.largeImageUrl,
                        text: image.dataResourceName
                    })
                }, self.slides);

                messageService.pop();
            },
            function () {
                messageService.alert("An error occurred while retrieving the images.");
            }
        );
    }

    self.addImage = function () {
        alert("Not implemented yet. Would upload to biocache & store image in image service");
    };
});