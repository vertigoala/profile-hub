/**
 * Controller for handling creating a new profile (via a modal popup)
 */
profileEditor.controller('CreateProfileController', function (profileService, $modalInstance, opusId) {
    var self = this;

    self.opusId = opusId;
    self.scientificName = "";
    self.error = null;

    self.cancel = function () {
        $modalInstance.dismiss("Cancelled");
    };

    self.ok = function() {
        var future = profileService.createProfile(self.opusId, self.scientificName);
        future.then(function (profile) {
                if (profile) {
                    $modalInstance.close(profile);
                } else {
                    self.error = "An error occurred while creating the profile.";
                }
            },
            function () {
                self.error = "An error occurred while creating the profile.";
            }
        );
    }
});