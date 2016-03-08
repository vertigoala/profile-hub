
/**
 * Images controller
 */
profileEditor.controller('ImagesController', function ($browser, $scope, profileService, navService, util, messageService, $modal, config) {
    var self = this;

    // Flag to prevent reloading images during the update process.
    var saving = false;

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

                // If the primary image specified for the profile changes then reload the images.
                // Note that this function will be called once for initiation which is required to load the images.
                $scope.$watch(function() {
                    return self.profile && self.profile.primaryImage;
                }, function() {
                    if (!saving) {
                        self.loadImages();
                    }
                });

            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    self.saveProfile = function (form) {
        saving = true;
        self.profile.imageSettings = [];

        self.profile.primaryImage = null;

        angular.forEach(self.images, function (image) {
            self.profile.imageSettings.push({imageId: image.imageId, caption: image.caption, displayOption: image.displayOption});

            if (image.primary) {
                self.profile.primaryImage = image.imageId;
            }
        });

        var profilePromise = profileService.updateProfile(self.opusId, self.profileId, self.profile);

        profilePromise.then(function (data) {
                messageService.info("Updating profile...");
                self.profile = data;

                var loadImagesProfile = self.loadImages();

                form.$setPristine();

                loadImagesProfile.finally(function() {
                    saving = false;
                });
            },
            function () {
                saving = false;

                messageService.alert("An error occurred while updating the profile.");
            }
        );
    };

    self.loadImages = function () {
        messageService.info("Loading images...");

        var searchIdentifier = self.profile.guid ? "lsid:" + self.profile.guid : "";

        var sources = angular.copy(self.opus.imageSources);
        sources.unshift(self.opus.dataResourceUid);

        var imagesPromise = profileService.retrieveImages(self.opusId, self.profileId, searchIdentifier, sources.join(), self.readonly);

        imagesPromise.then(function (data) {
                self.images = [];

                angular.forEach(data, function (image) {
                    if (!self.readonly || !image.excluded) {
                        self.images.push(image);

                        if (image.imageId == self.profile.primaryImage) {
                            self.primaryImage = image;
                        }
                    }
                });

                if (!self.primaryImage && self.images.length > 0) {
                    angular.forEach(self.images, function (image) {
                        if (!image.excluded && !self.primaryImage) {
                            self.primaryImage = image;
                        }
                    });
                }

                if (self.images.length > 0 || !self.readonly) {
                    navService.add("Images", "images");
                }

            },
            function () {
                messageService.alert("An error occurred while retrieving the images.");
            }
        );
        return imagesPromise;
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
            size: "lg",
            resolve: {
                image: function () {
                    return image;
                }
            }
        });

        popup.opened.then(function() {
            // Disable the Leaflet.Sleep functionality, which is enabled by default as soon as Leaflet.Sleep.js is included.
            // The ALA Map plugin uses this, but it explicitly enables/disables the feature based on its config.
            L.Map.mergeOptions({
                sleep: false
            });

            imgvwr.viewImage('#imageViewer', image.imageId, {
                imageServiceBaseUrl: image.type.name == 'OPEN' ? config.imageServiceUrl : util.contextRoot(),
                addDrawer: false,
                addSubImageToggle: false,
                addCalibration: false,
                addImageInfo: false,
                imageClientUrl: util.contextRoot()
            });
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
    };

    self.imageCaption = function (image) {
        if (image.caption) {
            return image.caption;
        }
        return image.metadata ? image.metadata.title : '';
    };
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
    };
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