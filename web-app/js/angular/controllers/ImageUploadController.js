/**
 * Upload image modal dialog controller - just provides a modal wrapper around the image-upload directive
 */
profileEditor.controller("ImageUploadController", function ($scope, opus, $modalInstance, util) {
    var self = this;

    self.opus = opus;

    self.uploadUrl = util.contextRoot() + "/opus/" + util.getEntityId("opus") + "/profile/" + util.getEntityId("profile") + "/image/upload";

    self.ok = function () {
        $scope.$broadcast("performUpload");
    };

    self.uploadComplete = function (imageMetadata) {
        $modalInstance.close(imageMetadata);
    };

    self.cancel = function () {
        $modalInstance.dismiss("cancel");
    };
});