/**
 * Profile controller
 */
profileEditor.controller('ProfileController', function (profileService, util, messageService, config) {
    var self = this;

    self.profile = null;
    self.opus = null;
    self.profileId = util.getPathItem(util.LAST);
    self.readonly = true;

    // make sure we have a UUID, not just the last element of some other URL (e.g. create)
    if (!util.isUuid(self.profileId)) {
        self.profileId = null;
        self.profile = {uuid: null, scientificName: ""};
        console.log("Creating new profile....");
    }

    self.readonly = function() {
        return config.readonly
    };

    loadProfile();

    function loadProfile() {
        if (self.profileId) {
            var promise = profileService.getProfile(self.profileId);
            promise.then(function (data) {
                    self.profile = data.profile;
                    self.opus = data.opus;
                },
                function () {
                    messageService.alert("An error occurred while loading the profile.")
                }
            );
        }
    }

    self.deleteProfile = function() {
        var deleteConf = util.confirm("Are you sure you wish to delete this profile? This operation cannot be undone.");
        deleteConf.then(function() {
            var promise = profileService.deleteProfile(self.profileId, self.opus.uuid);
            promise.then(function() {
                    util.redirect(util.contextRoot() + "/opus/" + self.opus.uuid);
                },
                function() {
                    messageService.alert("An error occurred while deleting the profile.")
                });
        });
    };

});