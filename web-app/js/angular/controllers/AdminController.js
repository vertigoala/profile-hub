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
});
