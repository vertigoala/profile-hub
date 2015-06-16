/**
 * Images controller
 */
profileEditor.controller('ImagesController', function (profileService, navService, util, messageService) {
    var self = this;
    
    self.images = [];
    self.primaryImage = null;

    self.init = function (edit) {
        self.readonly = edit != 'true';
        self.images = [];

        self.opusId = util.getEntityId("opus");
        self.profileId = util.getEntityId("profile");

        var profilePromise = profileService.getProfile(self.opusId, self.profileId);

        profilePromise.then(function (data) {
                self.profile = data.profile;
                self.opus = data.opus;

                self.loadImages()
            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    self.saveProfile = function(form) {
        self.profile.excludedImages = [];

        angular.forEach(self.images, function(image) {
            if (image.excluded) {
                self.profile.excludedImages.push(image.imageId);
            }
            if (image.primary) {
                self.profile.primaryImage = image.imageId;
            }
        });

        var profilePromise = profileService.updateProfile(self.opusId, self.profileId, self.profile);

        profilePromise.then(function (data) {
                messageService.info("Updating profile...");
                self.profile = data;

                self.loadImages();

                form.$setPristine();
            },
            function () {
                messageService.alert("An error occurred while updating the profile.");
            }
        );
    };

    self.loadImages = function() {
        if (self.opus.imageSources.length == 0) {
            return;
        }

        messageService.info("Loading images...");

        var searchIdentifier = self.profile.guid ? "lsid:" + self.profile.guid : self.profile.scientificName;
        var imagesPromise = profileService.retrieveImages(self.opusId, self.profileId, searchIdentifier, self.opus.imageSources.join());

        imagesPromise.then(function (data) {
                self.images = [];

                self.primaryImage = data.occurrences[0];

                angular.forEach(data.occurrences, function(occurrence) {
                    var excluded = false;

                    if (self.profile.excludedImages && self.profile.excludedImages.indexOf(occurrence.image) > -1) {
                        excluded = true;
                    }

                    var image = {
                        imageId: occurrence.image,
                        occurrenceId: occurrence.uuid,
                        largeImageUrl: occurrence.largeImageUrl,
                        dataResourceName: occurrence.dataResourceName,
                        excluded: excluded,
                        primary: occurrence.image == self.profile.primaryImage
                    };
                    self.images.push(image);

                    if (self.images.length > 0 || !self.readonly) {
                        navService.add("Images", "images");
                    }

                    if (occurrence.image == self.profile.primaryImage) {
                        self.primaryImage = image;
                    }
                });

                messageService.pop();
            },
            function () {
                messageService.alert("An error occurred while retrieving the images.");
            }
        );
    }

    self.changeImageDisplay = function(form) {
        form.$setDirty();
    };

    self.changePrimaryImage = function(imageId, form) {
        angular.forEach(self.images, function(image) {
            image.primary = image.imageId == imageId;
        });

        form.$setDirty();
    };

    self.addImage = function () {
        alert("Not implemented yet. Would upload to biocache & store image in image service");
    };
});