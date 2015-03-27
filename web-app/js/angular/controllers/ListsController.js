/**
 * Species Lists controller
 */
profileEditor.controller('ListsEditor', function (profileService, util, messageService) {
    var self = this;
    
    self.lists = [];

    self.init = function (edit) {
        self.readonly = edit != 'true';

        self.opusId = util.getEntityId("opus");
        self.profileId = util.getEntityId("profile");

        var profilePromise = profileService.getProfile(self.opusId, self.profileId);
        profilePromise.then(function (data) {
                self.profile = data.profile;
                self.opus = data.opus;

                loadLists();
            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    function loadLists() {
        if (self.profile.guid) {
            messageService.info("Loading lists...");

            var listsPromise = profileService.retrieveLists(self.opusId, self.profileId, self.profile.guid);

            listsPromise.then(function (data) {
                    console.log("Fetched " + data.length + " lists");

                    self.lists = data;

                    messageService.pop();
                },
                function () {
                    messageService.alert("An error occurred while retrieving the lists.");
                }
            );
        }
    }
});