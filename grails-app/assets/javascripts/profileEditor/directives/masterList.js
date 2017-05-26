(function() {
    "use strict";

    function MasterListController($scope, $http, config, messageService, profileService) {
        var self = this;

        self.lists = [];
        var listType = config.masterListType;
        $http.get(config.listServiceUrl + '/ws/speciesList', { params: { 'listType': 'eq:' + config.masterListType, max: -1, user: config.currentUserId } })
            .then(function(response) {
                self.lists = response.data.lists;
            }, function(response) {
                messageService.alert("Could not load Master List candidates")
            });

        // injected self.opus
        function setUID() {
            var masterListUid;
            masterListUid = self.opus ? self.opus.masterListUid || null : null;
            self.masterListUid = masterListUid;
        }
        $scope.$watch(function() { return self.opus }, setUID);
        setUID();
        self.isEdittable = true;

        self.saveMasterList = function() {
            profileService.updateMasterList(self.opus.uuid, self.masterListUid).then(
                function(response) {
                    self.opus.masterListUid = self.masterListUid;
                    self.MasterListForm.$setPristine();
                    messageService.success("Master list updated");
                }, function (response) {
                    $log.error("Couldn't update master list for opus", opus, response);
                    messageService.alert("Failed to update master list 😢");
                }
            )
        };

        self.syncNow = function(regen) {
            messageService.info("Syncing master list...");
            profileService.syncMasterList(self.opus.uuid, regen).then(
                function(response) {
                    messageService.success("Master list sync completed");
                }, function(response) {
                    messageService.alert("Master list sync failed 😢");
                }
            )
        };
    }

    profileEditor.directive('masterList', function() {
        return {
            restrict: 'AE',
            scope: {
                opus: '='
            },
            controller: MasterListController,
            controllerAs: '$ctrl',
            bindToController: true,
            templateUrl: '/profileEditor/masterList.htm'
        };
    });
})();