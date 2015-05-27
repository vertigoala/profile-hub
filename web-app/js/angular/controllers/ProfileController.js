/**
 * Profile controller
 */
profileEditor.controller('ProfileController', function (profileService, util, messageService, config, $modal, $window, $filter, $sce, $location) {
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

                    self.nslUrl = $sce.trustAsResourceUrl(config.nslNameUrl + self.profile.nslNameIdentifier + ".html");

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
            util.redirect(util.contextRoot() + "/opus/" + self.opusId + "/profile/" + profile.scientificName + "/update");
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

    self.toggleDraftMode = function() {
        var future = profileService.toggleDraftMode(self.opusId, self.profileId);

        future.then(function() {
            messageService.success("The profile has been successfully updated.");

            self.loadProfile();
        }, function() {
            messageService.alert("An error has occurred while updating the profile.");
        });
    };

    self.discardDraftChanges = function() {
        var confirm = util.confirm("Are you sure you wish to discard all draft changes? This operation cannot be undone.");
        confirm.then(function() {
            var future = profileService.discardDraftChanges(self.opusId, self.profileId);

            future.then(function () {
                messageService.success("The profile has been successfully restored.");

                util.redirect($location.absUrl());
            }, function () {
                messageService.alert("An error has occurred while restoring the profile.");
            });
        })
    };

    self.saveProfile = function(form) {
        var future = profileService.updateProfile(self.opusId, self.profileId, self.profile);

        future.then(function(data) {
            messageService.success("The profile has been successfully updated.");

            self.profile = data;

            if (form) {
                form.$setPristine();
            }
        }, function() {
            messageService.alert("An error has occurred while updating the profile.");
        });
    };

    self.addAuthorship = function(form) {
        if (!self.profile.authorship || self.profile.authorship.length == 0) {
            self.profile.authorship = [{category: "Author", text: ""}];
        } else {
            self.profile.authorship.push({category: "", text: ""});
        }

        form.$setDirty();
    };

    self.saveAuthorship = function(form) {
        var future = profileService.saveAuthorship(self.opusId, self.profileId, {authorship: self.profile.authorship});

        future.then(function() {
            form.$setPristine();

            messageService.success("Authorship and acknowledgements successfully updated.");
        }, function() {
            messageService.alert("An error occurred while updating authorship and acknowledgements.");
        })
    };

    self.deleteAuthorship = function(index, form) {
        self.profile.authorship.splice(index, 1);

        form.$setDirty();
    };
});


/**
 * Create profile popup controller
 */
profileEditor.controller('CreateProfileController', function (profileService, $modalInstance, opusId) {
    var self = this;

    self.opusId = opusId;
    self.scientificName = "";
    self.nameAuthor = "";
    self.error = "";

    self.ok = function () {
        var promise = profileService.profileSearch(self.opusId, self.scientificName, false);
        promise.then(function (matches) {
            if (matches.length > 0) {
                self.error = "A profile already exists for this scientific name.";
            } else {
                var future = profileService.createProfile(self.opusId, self.scientificName, self.nameAuthor);
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