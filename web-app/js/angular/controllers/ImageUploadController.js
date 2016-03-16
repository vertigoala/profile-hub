/**
 * Upload image modal dialog controller - just provides a modal wrapper around the image-upload directive
 */
profileEditor.controller("ImageUploadController", function ($scope, opus, $modalInstance) {
    var self = this;

    self.opus = opus;

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