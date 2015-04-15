/**
 * Profile controller
 */
profileEditor.controller('ProfileController', function (profileService, util, messageService, config, $modal, $window, $filter) {
    var self = this;

    self.profile = null;
    self.profileId = null;
    self.opus = null;
    self.readonly = true;

    self.opusId = util.getEntityId("opus");

    var orderBy = $filter("orderBy");

    self.readonly = function() {
        return config.readonly
    };

    self.loadProfile = function() {
        self.profileId = util.getEntityId("profile");


        if (self.profileId) {
            var promise = profileService.getProfile(self.opusId, self.profileId);
            promise.then(function (data) {
                    self.profile = data.profile;
                    self.profileId = data.profile.uuid;
                    self.opus = data.opus;

                    $window.document.title = self.profile.scientificName + " | " + self.opus.title;
                },
                function () {
                    messageService.alert("An error occurred while loading the profile.");
                }
            );
        }
    };

    self.deleteProfile = function() {
        var deleteConf = util.confirm("Are you sure you wish to delete this profile? This operation cannot be undone.");
        deleteConf.then(function() {
            var promise = profileService.deleteProfile(self.opus.uuid, self.profileId);
            promise.then(function() {
                    util.redirect(util.contextRoot() + "/opus/" + self.opus.uuid);
                },
                function() {
                    messageService.alert("An error occurred while deleting the profile.");
                });
        });
    };

    self.createProfile = function (opusId) {
        var popup = $modal.open({
            templateUrl: "createProfile.html",
            controller: "CreateProfileController",
            controllerAs: "createProfileCtrl",
            size: "sm",
            resolve: {
                opusId: function() {
                    return opusId;
                }
            }
        });

        popup.result.then(function (profile) {
            messageService.success("Profile for " + profile.scientificName + " has been successfully created.");
            util.redirect(util.contextRoot() + "/opus/" + self.opusId + "/profile/" + profile.uuid + "/update");
        });
    };

    self.addBibliography = function(form) {
        if (!self.profile.bibliography) {
            self.profile.bibliography = [];
        }
        self.profile.bibliography.push({text: "", order: self.profile.bibliography.length});

        form.$setDirty();
    };

    self.deleteBibliography = function(index, form) {
        var deletedItemOrder = self.profile.bibliography[index].order;
        self.profile.bibliography.splice(index, 1);

        angular.forEach(self.profile.bibliography, function(bib) {
            if (bib.order > deletedItemOrder) {
                bib.order = bib.order - 1;
            }
        });

        form.$setDirty();
    };

    self.moveBibliographyUp = function(index, form) {
        if (index > 0) {
            self.profile.bibliography[index].order = self.profile.bibliography[index].order - 1;
            self.profile.bibliography[index - 1].order = self.profile.bibliography[index - 1].order + 1;

            self.profile.bibliography = orderBy(self.profile.bibliography, "order");

            form.$setDirty();
        }
    };

    self.moveBibliographyDown = function(index, form) {
        if (index < self.profile.bibliography.length) {
            self.profile.bibliography[index].order = self.profile.bibliography[index].order + 1;
            self.profile.bibliography[index + 1].order = self.profile.bibliography[index + 1].order - 1;

            self.profile.bibliography = orderBy(self.profile.bibliography, "order");

            form.$setDirty();
        }
    };

    self.saveProfile = function(form) {
        var future = profileService.updateProfile(self.opusId, self.profileId, self.profile);

        future.then(function(data) {
            messageService.success("The profile has been successfully updated.");

            self.profile = data;

            form.$setPristine();
        }, function() {
            messageService.alert("An error has occurred while updating the profile.");
        });
    };
});


/**
 * Create profile popup controller
 */
profileEditor.controller('CreateProfileController', function (profileService, $modalInstance, opusId) {
    var self = this;

    self.opusId = opusId;
    self.scientificName = "";
    self.error = "";

    self.ok = function () {
        var promise = profileService.profileSearch(self.opusId, self.scientificName, false);
        promise.then(function (matches) {
            if (matches.length > 0) {
                self.error = "A profile already exists for this scientific name.";
            } else {
                var future = profileService.createProfile(self.opusId, self.scientificName);
                future.then(function (profile) {
                        if (profile) {
                            $modalInstance.close(profile);
                        } else {
                            self.error = "An error occurred while creating the profile.";
                        }
                    },
                    function () {
                        self.error = "An error occurred while creating the profile.";
                    }
                );
            }
        },
        function() {
            self.error = "An error occurred while searching for existing profiles.";
        })
    };

    self.cancel = function () {
        $modalInstance.dismiss("Cancelled");
    };
});