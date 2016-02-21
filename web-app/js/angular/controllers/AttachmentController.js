/**
 *  Attachment/document controller
 */
profileEditor.controller('AttachmentController', function (profileService, messageService, util, $modal) {
    var self = this;

    self.opusId = util.getEntityId("opus");
    self.profileId = util.getEntityId("profile");

    self.downloadUrl = util.contextRoot() + "/opus/" + self.opusId;
    if (self.profileId) {
        self.downloadUrl += "/profile/" + self.profileId;
    }
    self.downloadUrl += "/attachment/";

    self.loadAttachments = function () {
        var future = profileService.getAttachmentMetadata(self.opusId, self.profileId, null);
        future.then (function (data) {
                self.attachments = data;
            },
            function () {
                messageService.alert("An error occurred while retrieving attachments");
            }
        );
    };

    self.deleteAttachment = function(attachmentId) {
        var deleteConf = util.confirm("Are you sure you wish to delete this attachment? This operation cannot be undone.");
        deleteConf.then(function () {
            var future = profileService.deleteAttachment(self.opusId, self.profileId, attachmentId);
            future.then (function () {
                    self.loadAttachments();
                },
                function () {
                    messageService.alert("An error occurred while deleting the attachment");
                }
            );
        });
    };

    self.uploadAttachment = function () {
        var popup = $modal.open({
            templateUrl: "attachmentUpload.html",
            controller: "AttachmentUploadController",
            controllerAs: "attachmentUploadCtrl",
            size: "md",
            resolve: {
                attachment: function() {
                    return {};
                }
            }
        });

        popup.result.then(function () {
            self.loadAttachments();
        });
    };

    self.editAttachment = function (attachment) {
        var popup = $modal.open({
            templateUrl: "attachmentUpload.html",
            controller: "AttachmentUploadController",
            controllerAs: "attachmentUploadCtrl",
            size: "md",
            resolve: {
                attachment: function() {
                    return attachment;
                }
            }
        });

        popup.result.then(function () {
            self.loadAttachments();
        });
    };

    self.loadAttachments();
});


/**
 * Upload image modal dialog controller
 */
profileEditor.controller("AttachmentUploadController", function (profileService, util, config, $modalInstance, Upload, $cacheFactory, $filter, attachment) {
    var self = this;

    self.opusId = util.getEntityId("opus");
    self.profileId = util.getEntityId("profile");

    self.metadata = attachment || {};
    self.files = null;
    self.error = null;

    self.licences = null;

    var orderBy = $filter("orderBy");

    profileService.getLicences().then(function (data) {
        self.licences = orderBy(data, "name");
        self.metadata.licence = self.licences[0];
    });

    self.ok = function () {
        self.metadata.licence = self.metadata.licence.name;

        var url = "";
        if (self.profileId) {
            url = util.contextRoot() + "/opus/" + self.opusId + "/profile/" + self.profileId + "/attachment";
        } else {
            url = util.contextRoot() + "/opus/" + self.opusId + "/attachment";
        }

        Upload.upload({
            url: url,
            file: self.metadata.uuid ? null : self.files[0],
            data: self.metadata
        }).success(function () {
            self.metadata = {};
            self.file = null;
            $modalInstance.close();
            $cacheFactory.get('$http').removeAll();
        }).error(function () {
            self.error = "An error occurred while uploading your file."
        });
    };

    self.cancel = function () {
        $modalInstance.dismiss("cancel");
    };
});