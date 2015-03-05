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
        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));
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
        var promise = profileService.updateLinks(self.profile.uuid, JSON.stringify({profileId: self.profile.uuid, links: self.links}));
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