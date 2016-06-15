/**
 * Controller for uploading & managing data sets
 */
profileEditor.controller('DataController', ["profileService", "util", "messageService", "Upload", function (profileService, util, messageService, Upload) {
    var self = this;

    self.opusId = util.getEntityId("opus");
    self.dataSets = [];
    self.contextPath = util.contextRoot();
    self.uploadFile = util.getQueryParameter("uploadFile") === "true" || false;
    self.files = [];
    self.uploadedSandboxFileId = util.getQueryParameter("id");
    self.uploadedSandboxFileName = util.getQueryParameter("fn");

    self.deleteDataSet = function (dataResourceId) {
        var confirm = util.confirm("Are you sure you wish to delete this data set? This operation cannot be undone.");
        confirm.then(function () {
            profileService.deleteDataSet(self.opusId, dataResourceId).then(function () {
                loadDataSets();
            }, function () {
                messageService.alert("An error occurred while deleting the data set");
            });
        });
    };

    self.doUpload = function () {
        Upload.upload({
            url: self.contextPath + "/opus/" + self.opusId + "/data/uploadFile",
            file: self.files[0]
        }).success(function (data) {
            util.redirect(self.contextPath + "/opus/" + self.opusId + "/data/upload?uploadFile=true&fn=" + data.fileName + "&id=" + data.fileId);
            self.uploadedSandboxFileId = data.fileId;
            self.uploadedSandboxFileName = data.fileName;
        }).error(function () {
            messageService.alert("An error occurred while uploading your file.");
        });
    };

    self.fixSandboxUploadUrls = function () {
        if (angular.isDefined(SANDBOXUPLOAD)) {
            SANDBOXUPLOAD.fileId = self.uploadedSandboxFileId;
            SANDBOXUPLOAD.uploadStatusUrl = self.contextPath + "/dataCheck/upload/uploadStatus";
            SANDBOXUPLOAD.uploadLink = self.contextPath + "/dataCheck/upload/uploadToSandbox";
            SANDBOXUPLOAD.parseColumnsWithFirstLineInfoUrl = self.contextPath + "/dataCheck/upload/parseColumnsWithFirstLineInfo?id=" + self.uploadedSandboxFileId + "&firstLineIsData=";
            SANDBOXUPLOAD.viewProcessDataUrl = self.contextPath + "/dataCheck/upload/processData?id=";
            SANDBOXUPLOAD.parseColumnsUrl = self.contextPath + "/dataCheck/upload/parseColumns?id=";
        }
    };

    self.dataSourceOptionChanged = function () {
        var url = self.contextPath + "/opus/" + self.opusId + "/data/upload?uploadFile=" + self.uploadFile;
        self.uploadedSandboxFileName = null;
        self.uploadedSandboxFileId = null;
        //if (angular.isDefined(self.uploadedSandboxFileId) && self.uploadedSandboxFileId) {
        //    url += "&fn=" + self.uploadedSandboxFileName + "&id=" + self.uploadedSandboxFileId;
        //}
        util.redirect(url);
    };


    function loadDataSets() {
        profileService.getDataSets(self.opusId).then(function (dataSets) {
            self.dataSets = dataSets;
        });
    }

    loadDataSets();
}])
;