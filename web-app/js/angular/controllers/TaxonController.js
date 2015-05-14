/**
 * Taxon controller
 */
profileEditor.controller('TaxonController', function (profileService, util, messageService) {
    var self = this;

    self.speciesProfile = null;
    self.classifications = [];

    self.init = function (edit) {
        self.readonly = edit != 'true';

        self.opusId = util.getEntityId("opus");
        self.profileId = util.getEntityId("profile");

        var profilePromise = profileService.getProfile(self.opusId, self.profileId);
        profilePromise.then(function (data) {
                self.profile = data.profile;
                self.opus = data.opus;

                loadSpeciesProfile();
                loadClassifications();
            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    function loadClassifications() {
        if (self.profile.guid) {
            messageService.info("Loading taxonomy...");

            var promise = profileService.getClassifications(self.opusId, self.profileId, self.profile.guid);
            promise.then(function (data) {
                    console.log("Fetched " + data.length + " classifications");

                    self.classifications = data;
                    messageService.pop();
                },
                function () {
                    messageService.alert("An error occurred while retrieving the taxonomy.");
                }
            );
        }
    }

    function loadSpeciesProfile() {
        if (self.profile.guid) {
            messageService.info("Loading taxon...");

            var promise = profileService.getSpeciesProfile(self.opusId, self.profileId, self.profile.guid);
            promise.then(function (data) {
                    console.log("Fetched species profile");

                    self.speciesProfile = data;

                    messageService.pop();
                },
                function () {
                    messageService.alert("An error occurred while retrieving the taxonomy.");
                }
            );
        }
    }
});