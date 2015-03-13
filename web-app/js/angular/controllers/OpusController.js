/**
 * Opus controller
 */
profileEditor.controller('OpusController', function (profileService, util, messageService) {
    var self = this;

    self.opus = null;
    self.opusId = util.getPathItem(util.LAST);
    self.readonly = true;
    self.dataResource = null;
    self.dataResourceList = [];
    self.saving = false;
    self.newImageSources = [];
    self.newRecordSources = [];

    // make sure we have a UUID, not just the last element of some other URL (e.g. create)
    if (!util.isUuid(self.opusId)) {
        self.opusId = null;
        self.opus = {uuid: null, title: ""};
        console.log("Creating new opus....");
    }

    loadOpus();
    loadResources();

    self.saveOpus = function (form) {
        messageService.info("Saving...");
        self.saving = true;

        toggleMapPointerColourHash();

        var promise = profileService.saveOpus(self.opusId, self.opus);
        promise.then(function () {
                toggleMapPointerColourHash();

                messageService.pop();
                messageService.success("Successfully updated " + self.opus.title + ".");
                self.saving = false;
                if (form) {
                    form.$setPristine();
                }
            },
            function () {
                messageService.pop();
                messageService.alert("Failed to update " + self.opus.title + ".");
                self.saving = false;
            }
        );
    };

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

    self.opusResourceChanged = function ($item, $model, $label) {
        self.opus.title = $label;
        self.opus.dataResourceUid = $item.id;

        loadDataResource(self.opus.dataResourceUid);
    };

    function loadOpus() {
        if (!self.opusId) {
            return;
        }
        var promise = profileService.getOpus(self.opusId);

        messageService.info("Loading opus data...");
        promise.then(function (data) {
                console.log("Retrieved " + data.title);
                self.opus = data;

                toggleMapPointerColourHash();

                loadDataResource(self.opus.dataResourceUid);

                messageService.pop();
            },
            function () {
                messageService.alert("An error occurred while retrieving the opus.");
            }
        );
    }

    function toggleMapPointerColourHash() {
        if (self.opus.mapPointColour) {
            if (self.opus.mapPointColour.indexOf("#") > -1) {
                self.opus.mapPointColour = self.opus.mapPointColour.substr(1);
            } else {
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
});