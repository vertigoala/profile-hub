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

    loadOpusList();
    loadPendingJobs();

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
});
