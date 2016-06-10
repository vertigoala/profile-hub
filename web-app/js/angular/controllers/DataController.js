/**
 * Controller for uploading & managing data sets
 */
profileEditor.controller('DataController', function (profileService, util, messageService) {
    var self = this;

    self.opusId = util.getEntityId("opus");
    self.dataSets = [];
    self.contextPath = util.contextRoot();
    self.uploadFile = true;

    self.deleteDataSet = function(dataResourceId) {
        var confirm = util.confirm("Are you sure you wish to delete this data set? This operation cannot be undone.");
        confirm.then(function() {
            profileService.deleteDataSet(self.opusId, dataResourceId).then(function () {
                loadDataSets();
            }, function() {
                messageService.alert("An error occurred while deleting the data set");
            });
        });
    };

    function loadDataSets() {
        profileService.getDataSets(self.opusId).then(function (dataSets) {
            self.dataSets = dataSets;
        });
    }

    loadDataSets();
});