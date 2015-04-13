/**
 * Species Lists controller
 */
profileEditor.controller('ListsEditor', function (profileService, util, messageService, $filter) {
    var self = this;
    
    self.lists = [];

    var orderBy = $filter("orderBy");

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

                    self.lists = [];

                    angular.forEach(data, function(list) {
                        if (!self.opus.approvedLists || self.opus.approvedLists.length == 0 || self.opus.approvedLists.indexOf(list.dataResourceUid) > -1) {
                            self.lists.push(list);
                        }
                    });

                    self.lists = orderBy(self.lists, 'listName');

                    messageService.pop();
                },
                function () {
                    messageService.alert("An error occurred while retrieving the lists.");
                }
            );
        }
    }
});