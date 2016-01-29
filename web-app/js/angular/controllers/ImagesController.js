/**
 * Images controller
 */
profileEditor.controller('ImagesController', function ($browser, profileService, navService, util, messageService, $modal) {
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

    self.saveProfile = function (form) {
        self.profile.excludedImages = [];

        angular.forEach(self.images, function (image) {
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

    self.loadImages = function () {
        messageService.info("Loading images...");

        var searchIdentifier = self.profile.guid ? "lsid:" + self.profile.guid : "";

        var sources = angular.copy(self.opus.imageSources);
        sources.unshift(self.opus.dataResourceUid);

        var imagesPromise = profileService.retrieveImages(self.opusId, self.profileId, searchIdentifier, sources.join());

        imagesPromise.then(function (data) {
                self.images = [];

                self.primaryImage = data[0];

                angular.forEach(data, function (image) {
                    self.images.push(image);

                    if (image.imageId == self.profile.primaryImage) {
                        self.primaryImage = image;
                    }
                });

                if (self.images.length > 0 || !self.readonly) {
                    navService.add("Images", "images");
                }

            },
            function () {
                messageService.alert("An error occurred while retrieving the images.");
            }
        );
    };

    self.changeImageDisplay = function (form) {
        form.$setDirty();
    };

    self.changePrimaryImage = function (imageId, form) {
        angular.forEach(self.images, function (image) {
            image.primary = image.imageId == imageId;
        });

        form.$setDirty();
    };

    self.uploadImage = function () {
        var popup = $modal.open({
            templateUrl: "imageUpload.html",
            controller: "ImageUploadController",
            controllerAs: "imageUploadCtrl",
            size: "md",
            resolve: {
                opus: function () {
                    return self.opus;
                }
            }
        });

        popup.result.then(function () {
            self.loadImages();
        });
    };

    self.showMetadata = function (image) {
        var popup = $modal.open({
            templateUrl: $browser.baseHref() + "static/templates/imageMetadata.html",
            controller: "ImageMetadataController",
            controllerAs: "imageMetadataCtrl",
            size: "md",
            resolve: {
                image: function () {
                    return image;
                }
            }
        });

        popup.result.then(function () {
            self.loadImages();
        });
    };

    self.publishPrivateImage = function(imageId) {
        var confirm = util.confirm("Are you sure you wish to make this image available to other Atlas of Living Australia applications?");

        confirm.then(function() {
            var future = profileService.publishPrivateImage(self.opusId, self.profileId, imageId);

            future.then(function () {
                self.loadImages();

                messageService.success("You images has been successfully published to the Atlas of Living Australia image library.");
            }, function() {
                messageService.alert("An error occurred while publishing your image.");
            })
        })
    };

    self.deleteLocalImage = function(imageId, type) {
        var confirm = util.confirm("Are you sure you wish to delete this image?");

        confirm.then(function() {
            var future = profileService.deleteLocalImage(self.opusId, self.profileId, imageId, type);

            future.then(function () {
                self.loadImages();
            }, function() {
                messageService.alert("An error occurred while deleting your staged image.");
            });
        });
    }
});

/**
 * Upload image modal dialog controller
 */
profileEditor.controller("ImageUploadController", function (profileService, util, config, $modalInstance, Upload, $cacheFactory, opus, $filter) {
    var self = this;

    self.metadata = {rightsHolder: opus.title};
    self.files = null;
    self.error = null;
    self.opus = opus;

    self.licences = null;

    var orderBy = $filter("orderBy");

    profileService.getLicences().then(function (data) {
        self.licences = orderBy(data, "name");
        self.metadata.licence = self.licences[0];
    });

    self.ok = function () {
        self.metadata.dataResourceId = self.opus.dataResourceUid;
        self.metadata.licence = self.metadata.licence.name;

        Upload.upload({
            url: util.contextRoot() + "/opus/" + util.getEntityId("opus") + "/profile/" + util.getEntityId("profile") + "/image/upload",
            fields: self.metadata,
            file: self.files[0]
        }).success(function () {
            self.image = {};
            self.file = null;
            $modalInstance.close();
            $cacheFactory.get('$http').removeAll();
        }).error(function () {
            self.error = "An error occurred while uploading your image."
        });
    };

    self.cancel = function () {
        $modalInstance.dismiss("cancel");
    }
});

/**
 * Image metadata modal dialog controller
 */
profileEditor.controller("ImageMetadataController", function($modalInstance, image) {
    var self = this;

    self.image = image;

    self.cancel = function () {
        $modalInstance.dismiss("cancel");
    }
});