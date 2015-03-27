/**
 *  Links controller
 */
profileEditor.controller('LinksEditor', function (profileService, util, messageService) {
    var self = this;
    
    self.links = [];

    self.init = function (edit) {
        self.readonly = edit != 'true';

        loadLinks();
    };

    function loadLinks() {
        self.opusId = util.getEntityId("opus");
        self.profileId = util.getEntityId("profile");

        var profilePromise = profileService.getProfile(self.opusId, self.profileId);
        profilePromise.then(function (data) {
                self.profile = data.profile;
                self.opus = data.opus;
                self.links = data.profile.links;
            },
            function () {
                messageService.alert("An error occurred while retrieving the links.");
            }
        );
    }

    self.addLink = function () {
        self.links.unshift({uuid: null, url: "http://", description: "", title: ""});
    };

    self.deleteLink = function (idx) {
        self.links.splice(idx, 1);
    };

    self.saveLinks = function () {
        var promise = profileService.updateLinks(self.opusId, self.profile.uuid, JSON.stringify({profileId: self.profile.uuid, links: self.links}));
        promise.then(function () {
                messageService.success("Links successfully updated.");
                loadLinks();
            },
            function () {
                messageService.alert("An error occurred while updating the links.");
            }
        );
    };
});