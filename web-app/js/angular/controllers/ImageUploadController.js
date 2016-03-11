/**
 * Upload image modal dialog controller
 */
profileEditor.controller("ImageUploadController", function (profileService, util, config, $modalInstance, Upload, $cacheFactory, opus, $filter) {
    var self = this;

    self.metadata = {rightsHolder: opus.title};
    self.files = null;
    self.error = null;
    self.opus = opus;

    self.licences = null;

    var orderBy = $filter("orderBy");

    profileService.getLicences().then(function (data) {
        self.licences = orderBy(data, "name");
        self.metadata.licence = self.licences[0];
    });

    self.ok = function () {
        self.metadata.dataResourceId = self.opus.dataResourceUid;
        self.metadata.licence = self.metadata.licence.name;

        Upload.upload({
            url: util.contextRoot() + "/opus/" + util.getEntityId("opus") + "/profile/" + util.getEntityId("profile") + "/image/upload",
            fields: self.metadata,
            file: self.files[0]
        }).success(function (imageMetadata) {
            self.image = {};
            self.file = null;
            $modalInstance.close(imageMetadata);
            $cacheFactory.get('$http').removeAll();
        }).error(function () {
            self.error = "An error occurred while uploading your image."
        });
    };

    self.cancel = function () {
        $modalInstance.dismiss("cancel");
    };
});