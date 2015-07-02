/**
 * Profile controller
 */
profileEditor.controller('ProfileController', function (profileService, util, messageService, navService, config, $modal, $window, $filter, $sce, $location) {
    var self = this;

    self.profile = null;
    self.profileId = util.getEntityId("profile");
    self.opus = null;
    self.readonly = true;

    self.showMap = true;

    self.opusId = util.getEntityId("opus");

    self.showNameEditControls = false;
    self.manuallyMatchedGuid = "";

    var orderBy = $filter("orderBy");

    self.readonly = function() {
        return config.readonly
    };

    self.loadProfile = function() {
        if (self.profileId) {
            var promise = profileService.getProfile(self.opusId, self.profileId);
            promise.then(function (data) {
                    self.profile = data.profile;
                    self.profileId = data.profile.uuid;
                    self.opus = data.opus;

                    self.nslUrl = $sce.trustAsResourceUrl(config.nslNameUrl + self.profile.nslNameIdentifier + ".html");

                    $window.document.title = self.profile.scientificName + " | " + self.opus.title;

                    if (self.profile.specimenIds && self.profile.specimenIds.length > 0 || !self.readonly()) {
                        navService.add("Specimens", "specimens");
                    }

                    if (self.profile.bibliography && self.profile.bibliography.length > 0 || !self.readonly()) {
                        navService.add("Bibliography", "bibliography");
                    }

                    if (!self.readonly() || self.profile.authorship.length > 1) {
                        navService.add("Authors & Acknowledgements", "authorship");
                    }

                    findCommonName();
                    loadVocabulary();

                    if (self.profile.matchedName) {
                        self.profile.matchedName.formattedName = util.formatScientificName(self.profile.matchedName.scientificName, self.profile.matchedName.nameAuthor, self.profile.matchedName.fullName);
                    }
                },
                function () {
                    messageService.alert("An error occurred while loading the profile.");
                }
            );
        }
    };

    function loadVocabulary() {
        if (self.opus.attributeVocabUuid != null) {
            var vocabPromise = profileService.getOpusVocabulary(self.opusId, self.opus.authorshipVocabUuid);
            vocabPromise.then(function (data) {
                self.authorVocab = data.terms;

                self.authorVocabStrict = data.strict;

                var authorCategories = [];
                angular.forEach(self.profile.authorship, function(author) {
                    if (authorCategories.indexOf(author.category) == -1) {
                        authorCategories.push(author.category);
                    }
                });

                angular.forEach(self.authorVocab, function(term) {
                    if (authorCategories.indexOf(term.name) == -1) {
                        self.profile.authorship.push({category: term.name, text: null});
                    }
                });
            });
        }
    }

    function findCommonName() {
        self.commonNames = [];

        angular.forEach(self.profile.attributes, function(attribute) {
            var title = attribute.title.toLowerCase();
            if (title === "common name" || title === "commonname" || title === "common-name") {
                self.commonNames.push(attribute.plainText);
            }
        });
    }

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
            size: "md",
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
        if (self.profile.privateMode) {
            var confirm = util.confirm("Would you like to take a snapshot of the current public version before releasing your changes?", "Yes", "No");

            confirm.then(function() {
                toggleDraftMode(true);
            }, function() {
                toggleDraftMode(false);
            });
        } else {
            toggleDraftMode(false);
        }
    };

    function toggleDraftMode(snapshot) {
        if (self.profile.privateMode && snapshot) {
            messageService.info("Creating snapshot and applying changes. Please wait...");
        } else if (self.profile.privateMode && !snapshot) {
            messageService.info("Applying changes. Please wait...");
        }

        var future = profileService.toggleDraftMode(self.opusId, self.profileId, snapshot);

        future.then(function() {
            messageService.success("The profile has been successfully updated.");

            // the name may have been changed in the draft, so the url parameter may no longer be correct. Update the profileId to ensure we load the correct name.
            self.profileId = self.profile.scientificName;

            self.loadProfile();
        }, function() {
            messageService.alert("An error has occurred while updating the profile.");
        });
    }

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
        for (var i = self.profile.authorship.length - 1; i >= 0; i--) {
            if (!self.profile.authorship[i].text) {
                self.profile.authorship.splice(i, 1);
            }
        }

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

    self.formatName = function() {
        if (!self.profile) {
            return null;
        }

        if (self.profile.matchedName && self.profile.scientificName == self.profile.matchedName.scientificName) {
            return util.formatScientificName(self.profile.scientificName, self.profile.nameAuthor, self.profile.fullName);
        } else {
            return util.formatScientificName(self.profile.scientificName, self.profile.nameAuthor, null);
        }
    };

    self.editName = function() {
        self.showNameEditControls = !self.showNameEditControls;
        self.newName = self.profile.scientificName;
    };

    self.saveNameChange = function() {
        var confirm = util.confirm("Are you sure you wish to rename this profile?");

        confirm.then(function() {
            var future = profileService.renameProfile(self.opusId, self.profileId, {newName: self.newName, manuallyMatchedGuid: self.manuallyMatchedGuid, clearMatch: false});

            future.then(function(profile) {
                self.profile = profile;

                messageService.success("The profile name has been successfully updated.");

                util.redirect(util.contextRoot() + "/opus/" + self.opusId + "/profile/" + profile.scientificName + "/update");
            }, function() {
                messageService.error("An error occurred while updating the profile name.");
            });
        });
    };

    self.clearNameMatch = function() {
        var confirm = util.confirm("Are you sure you wish to remove the matched name from this profile? This will mean that information from Atlas of Living Australia resources will not be available.");

        confirm.then(function() {
            var future = profileService.renameProfile(self.opusId, self.profileId, {newName: null, clearMatch: true});

            future.then(function(profile) {
                self.profile = profile;

                messageService.success("The profile name has been successfully updated.");

                util.redirect(util.contextRoot() + "/opus/" + self.opusId + "/profile/" + profile.scientificName + "/update");
            }, function() {
                messageService.error("An error occurred while updating the profile name.");
            });
        });
    }
});

