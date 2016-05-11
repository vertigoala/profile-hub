/**
 * Opus controller
 */
profileEditor.controller('OpusController', function (profileService, util, messageService, $window, $filter) {
    var self = this;

    var SUPPORTING_COLLECTION_REQUESTED = "REQUESTED";
    var SUPPORTING_COLLECTION_APPROVED = "APPROVED";
    var SUPPORTING_COLLECTION_REJECTED = "REJECTED";

    self.opus = null;
    self.opusId = null;
    self.opusList = [];
    self.readonly = true;
    self.dataResource = null;
    self.dataResourceList = [];
    self.allSpeciesLists = [];
    self.saving = false;
    self.newImageSources = [];
    self.newRecordSources = [];
    self.supportingOpuses = []; // separate list because the opus contains two sets of supporting opuses (approved and requested)
    self.newSupportingOpuses = [];
    self.newApprovedLists = [];
    self.newFeatureLists = [];
    self.valid = false;
    self.editors = [];
    self.initialShortName = null;
    self.keybaseProjects = [];
    self.selectedKeybaseProject = null;
    self.shortNameTipVisible = false;

    self.showUpload = {
        opusBanner: false,
        profileBanner: false,
        thumbnail: false,
        logo: false
    };

    self.imageUploadUrl = util.contextRoot() + "/opus/" + util.getEntityId("opus") + "/image?purpose=";

    loadResources();
    loadOpusList();
    loadKeybaseProjects();

    var orderBy = $filter("orderBy");

    self.loadOpus = function () {
        self.opusId = util.getEntityId("opus");

        if (!self.opusId) {
            return;
        }
        var promise = profileService.getOpus(self.opusId);

        messageService.info("Loading opus data...");
        promise.then(function (data) {
                self.opus = data;

                angular.forEach(self.opus.authorities, function (auth) {
                    if (auth.role == "ROLE_PROFILE_EDITOR") {
                        self.editors.push({userId: auth.userId, name: auth.name})
                    }
                });

                self.initialShortName = data.shortName;

                self.supportingOpuses = [].concat(self.opus.supportingOpuses);
                self.supportingOpuses = self.supportingOpuses.concat(self.opus.requestedSupportingOpuses);

                toggleMapPointerColourHash(true);

                loadDataResource(self.opus.dataResourceUid);

                if (self.keybaseProjects && self.opus.keybaseProjectId) {
                    setSelectedKeybaseProject();
                }

                $window.document.title = self.opus.title + " | Profile Collections";

            },
            function () {
                messageService.alert("An error occurred while retrieving the opus.");
            }
        );
    };

    self.saveOpus = function (form) {
        messageService.info("Saving...");
        self.saving = true;

        toggleMapPointerColourHash(false);

        if (self.opus.shortName !== self.initialShortName && self.opus.shortName) {
            var f = profileService.getOpus(self.opus.shortName);
            f.then(function () {
                messageService.alert("The specified short name is already in use. Short Names must be unique across all collections.");
            }, function () {
                console.log("Short name is unique");

                save(form);
            })
        } else {
            save(form)
        }
    };

    function save(form) {
        if (self.selectedKeybaseProject) {
            self.opus.keybaseProjectId = self.selectedKeybaseProject.project_id;
            self.opus.keybaseKeyId = self.selectedKeybaseProject.first_key.id;
        } else {
            self.opus.keybaseProjectId = "";
            self.opus.keybaseKeyId = "";
        }

        var promise = profileService.saveOpus(self.opusId, self.opus);
        promise.then(function (data) {
                toggleMapPointerColourHash(true);

                messageService.success("Successfully updated " + self.opus.title + ".");
                self.saving = false;
                if (form) {
                    form.$setPristine();
                }

                if (!self.opus.uuid) {
                    self.opusId = data.uuid;
                    self.opus = data;
                    util.redirect(util.contextRoot() + "/opus/" + self.opus.uuid + "/update");
                }
            },
            function () {
                messageService.alert("Failed to update " + self.opus.title + ".");
                self.saving = false;
            }
        );
    }

    self.addImageSource = function () {
        self.newImageSources.push({});
    };

    self.saveImageSources = function (form) {
        var invalid = [];
        var valid = [];

        angular.forEach(self.newImageSources, function (image) {
            if (image.dataResource) {
                if (image.dataResource.id) {
                    valid.push(image.dataResource.id);
                } else {
                    invalid.push(image);
                }
            }
        });

        if (invalid.length == 0) {
            self.newImageSources = [];
            if (valid.length > 0) {
                angular.forEach(valid, function (image) {
                    self.opus.imageSources.push(image);
                });
            }
            self.saveOpus(form);
        } else if (invalid.length > 0) {
            messageService.alert(invalid.length + " image source" + (invalid.length > 1 ? "s are" : " is") + " not valid. You must select items from the list.")
        }
    };

    self.removeImageSource = function (index, list, form) {
        if (list == 'existing') {
            self.opus.imageSources.splice(index, 1);
        } else {
            self.newImageSources.splice(index, 1);
        }
        form.$setDirty();
    };

    self.addRecordSource = function () {
        self.newRecordSources.push({});
    };

    self.saveRecordSources = function (form) {
        var invalid = [];
        var valid = [];

        angular.forEach(self.newRecordSources, function (record) {
            if (record.dataResource) {
                if (record.dataResource.id) {
                    valid.push(record.dataResource.id);
                } else {
                    invalid.push(record);
                }
            }
        });

        if (invalid.length == 0) {
            self.newRecordSources = [];
            if (valid.length > 0) {
                angular.forEach(valid, function (record) {
                    self.opus.recordSources.push(record);
                });
            }
            self.saveOpus(form);
        } else if (invalid.length > 0) {
            messageService.alert(invalid.length + " record source" + (invalid.length > 1 ? "s are" : " is") + " not valid. You must select items from the list.")
        }
    };

    self.removeRecordSource = function (index, list, form) {
        if (list == 'existing') {
            self.opus.recordSources.splice(index, 1);
        } else {
            self.newRecordSources.splice(index, 1);
        }
        form.$setDirty();
    };

    self.addSupportingOpus = function () {
        self.newSupportingOpuses.push({requestStatus: SUPPORTING_COLLECTION_REQUESTED});
    };

    self.saveSupportingOpuses = function (form) {
        var invalid = [];
        var valid = [];

        angular.forEach(self.newSupportingOpuses, function (selectedObject) {
            if (selectedObject.opus) {
                if (selectedObject.opus.uuid) {
                    valid.push(selectedObject.opus);
                } else {
                    invalid.push(selectedObject);
                }
            }
        });

        if (invalid.length == 0) {
            self.newSupportingOpuses = [];
            if (valid.length > 0) {
                angular.forEach(valid, function (opus) {
                    self.supportingOpuses.push(opus);
                });
            }

            var data = {
                supportingOpuses: self.supportingOpuses
            };

            if (typeof self.opus.autoApproveShareRequests != 'undefined') {
                data.autoApproveShareRequests = self.opus.autoApproveShareRequests;
            }
            if (typeof self.opus.allowCopyFromLinkedOpus != 'undefined') {
                data.allowCopyFromLinkedOpus = self.opus.allowCopyFromLinkedOpus;
            }
            if (typeof self.opus.showLinkedOpusAttributes != 'undefined') {
                data.showLinkedOpusAttributes = self.opus.showLinkedOpusAttributes;
            }

            var future = profileService.updateSupportingCollections(self.opusId, data);
            future.then(function () {
                messageService.success("Supporting collections have been successfully updated.");
                self.saving = false;
                form.$setPristine();
            }, function () {
                messageService.alert("An error has occurred while updating the supporting collections.");
            })
        } else if (invalid.length > 0) {
            messageService.alert(invalid.length + " supporting collection" + (invalid.length > 1 ? "s are" : " is") + " not valid. You must select items from the list.")
        }
    };

    self.supportingOpusSelected = function (supportingOpus) {
        supportingOpus.requestStatus = SUPPORTING_COLLECTION_REQUESTED;
    };

    self.revokeAccessToSupportedCollection = function(otherOpusUuid) {
        var confirm = util.confirm("Are you sure you wish to revoke access to your collection's data?");

        confirm.then(function() {
            var future = profileService.respondToSupportingCollectionRequest(self.opusId, otherOpusUuid, "revoke");

            future.then(function() {
                var indexToRemove = -1;
                angular.forEach(self.opus.sharingDataWith, function(opus, index) {
                    if (opus.uuid == otherOpusUuid) {
                        indexToRemove = index;
                    }
                });

                if (indexToRemove > -1) {
                    self.opus.sharingDataWith.splice(indexToRemove, 1);
                }
            })
        });
    };

    self.removeSupportingOpus = function (index, list, form) {
        if (list == 'existing') {
            self.supportingOpuses.splice(index, 1);
        } else {
            self.newSupportingOpuses.splice(index, 1);
        }
        form.$setDirty();
    };

    self.addApprovedList = function () {
        self.newApprovedLists.push({});
    };

    self.saveApprovedLists = function (form) {
        var invalid = [];
        var valid = [];

        angular.forEach(self.newApprovedLists, function (approvedList) {
            if (approvedList.list) {
                if (approvedList.list.dataResourceUid) {
                    valid.push(approvedList.list.dataResourceUid);
                } else {
                    invalid.push(approvedList);
                }
            }
        });

        if (invalid.length == 0) {
            self.newApprovedLists = [];
            if (valid.length > 0) {
                angular.forEach(valid, function (record) {
                    self.opus.approvedLists.push(record);
                });
            }
            self.saveOpus(form);
        } else if (invalid.length > 0) {
            messageService.alert(invalid.length + " list" + (invalid.length > 1 ? "s are" : " is") + " not valid. You must select items from the list.")
        }
    };

    self.removeApprovedList = function (index, list, form) {
        if (list == 'existing') {
            self.opus.approvedLists.splice(index, 1);
        } else {
            self.newApprovedLists.splice(index, 1);
        }
        form.$setDirty();
    };

    self.addFeatureList = function () {
        self.newFeatureLists.push({});
    };

    self.saveFeatureLists = function (form) {
        var invalid = [];
        var valid = [];

        angular.forEach(self.newFeatureLists, function (featureLists) {
            if (featureLists.list) {
                if (featureLists.list.dataResourceUid) {
                    valid.push(featureLists.list.dataResourceUid);
                } else {
                    invalid.push(featureLists);
                }
            }
        });

        if (invalid.length == 0) {
            self.newFeatureLists = [];
            if (valid.length > 0) {
                angular.forEach(valid, function (record) {
                    self.opus.featureLists.push(record);
                });
            }
            self.saveOpus(form);
        } else if (invalid.length > 0) {
            messageService.alert(invalid.length + " list" + (invalid.length > 1 ? "s are" : " is") + " not valid. You must select items from the list.")
        }
    };

    self.removeFeatureList = function (index, list, form) {
        if (list == 'existing') {
            self.opus.featureLists.splice(index, 1);
        } else {
            self.newFeatureLists.splice(index, 1);
        }
        form.$setDirty();
    };

    self.opusResourceChanged = function ($item) {
        self.opus.dataResourceUid = $item.id;

        loadDataResource(self.opus.dataResourceUid);
    };

    self.deleteOpus = function () {
        var deleteConf = util.confirm("Are you sure you wish to delete this entire collection? This operation cannot be undone.");
        deleteConf.then(function () {
            var promise = profileService.deleteOpus(self.opus.uuid);
            promise.then(function () {
                    util.redirect(util.contextRoot() + "/");
                },
                function () {
                    messageService.alert("An error occurred while deleting the collection.")
                });
        });
    };

    self.showShortNameTip = function() {
        self.shortNameTipVisible = true;
    };

    self.opusBannerUploaded = function(result) {
        self.opus.brandingConfig.opusBannerUrl = util.getBaseHref() + result.imageUrl;
        self.toggleUploadPanel('opusBanner');
        self.StyleForm.$setDirty();
    };

    self.profileBannerUploaded = function(result) {
        self.opus.brandingConfig.profileBannerUrl = util.getBaseHref() + result.imageUrl;
        self.toggleUploadPanel('profileBanner');
        self.StyleForm.$setDirty();
    };

    self.logoUploaded = function(result) {
        self.opus.brandingConfig.logoUrl = util.getBaseHref() + result.imageUrl;
        self.toggleUploadPanel('logo');
        self.StyleForm.$setDirty();
    };

    self.thumbnailUploaded = function(result) {
        self.opus.brandingConfig.thumbnailUrl = util.getBaseHref() + result.imageUrl;
        self.toggleUploadPanel('thumbnail');
        self.StyleForm.$setDirty();
    };

    self.toggleUploadPanel = function(section) {
        self.showUpload[section] = !self.showUpload[section];
    };

    // Support for lazy loading the keyplayer.
    self.keybaseTemplateUrl = undefined;
    self.initialiseKeyplayer = function() {
        self.keybaseTemplateUrl = 'keyplayer.html';
    };

    function loadKeybaseProjects() {
        console.log("loading keybase projects...");

        profileService.retrieveKeybaseProjects().then(function (data) {
            self.keybaseProjects = data;

            if (self.opus && self.opus.keybaseProjectId) {
                setSelectedKeybaseProject();
            }
            console.log("Keybase projects loaded");
        });
    }

    function setSelectedKeybaseProject() {
        angular.forEach(self.keybaseProjects, function (project) {
            if (project.project_id == self.opus.keybaseProjectId) {
                self.selectedKeybaseProject = project;
            }
        });
    }

    function toggleMapPointerColourHash(shouldExist) {
        if (self.opus.mapPointColour) {
            if (!shouldExist && self.opus.mapPointColour.indexOf("#") > -1) {
                self.opus.mapPointColour = self.opus.mapPointColour.substr(1);
            } else if (shouldExist && self.opus.mapPointColour.indexOf("#") == -1) {
                self.opus.mapPointColour = "#" + self.opus.mapPointColour;
            }
        }
    }

    function loadResources() {
        var promise = profileService.listResources();
        console.log("Loading data resources...");
        promise.then(function (data) {
                self.dataResources = data;

                self.dataResourceList = [];
                angular.forEach(self.dataResources, function (key, value) {
                    self.dataResourceList.push({id: value, name: key.trim()});
                });
            },
            function () {
                console.log("Failed to retrieve opus description from collectory.");
            }
        );

        var lists = profileService.getAllLists();
        lists.then(function (data) {
            self.allSpeciesLists = [];
            angular.forEach(data.lists, function (list) {
                self.allSpeciesLists.push({dataResourceUid: list.dataResourceUid, listName: list.listName.trim()});
            });
            self.allSpeciesLists = orderBy(self.allSpeciesLists, "listName");
        });
    }

    function loadDataResource(dataResourceId) {
        var promise = profileService.getResource(dataResourceId);
        console.log("Loading opus description...");
        promise.then(function (data) {
                self.dataResource = data;
            },
            function () {
                console.log("Failed to retrieve opus description from collectory.");
            }
        );
    }

    function loadOpusList() {
        console.log("Fetching opus list...");
        var promise = profileService.listOpus();
        promise.then(function (data) {
            angular.forEach(data, function (opus) {
                self.opusList.push({
                    uuid: opus.uuid,
                    title: opus.title,
                    thumbnailUrl: opus.brandingConfig.thumbnailUrl,
                    shortName: opus.shortName,
                    pubDescription: opus.pubDescription
                })
            });
        })
    }
});
