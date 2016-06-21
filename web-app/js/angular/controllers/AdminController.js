/**
 *  ALA Admin controller
 */
profileEditor.controller('ALAAdminController', function ($http, util, messageService, profileService) {
    var self = this;

    self.collectionMultiSelectOptions = {
        filterPlaceHolder: 'Start typing to filter the list below.',
        labelAll: 'All collections',
        labelSelected: 'Collections to rematch',
        orderProperty: 'name',
        selectedItems: [],
        items: []
    };

    self.tags = [];

    loadOpusList();
    loadPendingJobs();
    loadTags();

    self.reloadHelpUrls = function() {
        var promise = $http.post(util.contextRoot() + "/admin/reloadHelpUrls");

        promise.then(function() {
            messageService.success("The help url cache has been cleared");
        });
    };

    self.reindex = function () {
        var promise = $http.post(util.contextRoot() + "/admin/reindex");

        promise.then(function () {
                messageService.success("Re-index started");
            },
            function () {
                messageService.alert("An error prevented the re-index.");
            }
        );
    };

    self.rematchNames = function () {
        if (self.collectionMultiSelectOptions.selectedItems.length > 0) {
            var selectedIds = [];
            self.collectionMultiSelectOptions.selectedItems.forEach(function (opus) {
                selectedIds.push(opus.id);
            });

            var promise = $http.post(util.contextRoot() + "/admin/rematchNames", {opusIds: selectedIds.join(",")});

            promise.then(function () {
                    messageService.success("Name rematch started - watch the profile-service logs to see when it finishes");
                },
                function () {
                    messageService.alert("An error prevented the re-match.");
                }
            );
        } else {
            messageService.alert("Select one or more collections to rematch!");
        }
    };

    self.deleteJob = function(jobType, jobId) {
        var confirm = util.confirm("Are you sure you want to delete this job?");
        confirm.then(function() {
            var promise = $http.delete(util.contextRoot() + "/admin/job/" + jobType + "/" + jobId);
            promise.then(function() {
                messageService.success("Job deleted");
                loadPendingJobs();
            }, function() {
                messageService.alert("Failed to delete the job");
            });
        });
    };

    self.deleteTag = function(index) {
        var tag = self.tags[index];
        if (tag.uuid) {
            var confirm = util.confirm("Are you sure you want to delete this tag?");
            confirm.then(function () {
                var promise = $http.delete(util.contextRoot() + "/admin/tag/" + tag.uuid);
                promise.then(function () {
                    messageService.success("Job deleted");
                    self.tags.splice(index, 1);
                }, function () {
                    messageService.alert("Failed to delete the job");
                });
            });
        } else {
            self.tags.splice(index, 1);
        }
    };

    self.saveTag = function(index) {
        var tag = self.tags[index];
        var promise;
        if (tag.uuid) {
            promise = $http.post(util.contextRoot() + "/admin/tag/" + tag.uuid, tag);
        } else {
            promise = $http.put(util.contextRoot() + "/admin/tag/", tag);
        }

        promise.then(function(response) {
            self.tags[index] = response.data;
            messageService.success("Tag saved");
        })
    };

    self.addTag = function() {
        self.tags.push({uuid: null, colour: null, name: "", abbrev: ""});
    };

    function loadOpusList() {
        self.collectionMultiSelectOptions.items.length = 0;

        profileService.listOpus().then(function (data) {
            angular.forEach(data, function (opus) {
                self.collectionMultiSelectOptions.items.push({
                    id: opus.uuid,
                    name: opus.title
                });
            });
        });
    }

    function loadPendingJobs() {
        self.loadingPendingJobs = true;
        var promise = $http.get(util.contextRoot() + "/admin/job/");
        promise.then(function (response) {
            self.jobs = response.data.jobs;
            self.loadingPendingJobs = false;
        }, function() {
            self.loadingPendingJobs = false;
        });
    }

    function loadTags() {
        self.loadingTags = true;
        var promise = $http.get(util.contextRoot() + "/admin/tag/");
        promise.then(function (response) {
            self.tags = response.data || [];
            self.loadingTags = false;
        }, function() {
            self.loadingTags = false;
        });
    }
});
