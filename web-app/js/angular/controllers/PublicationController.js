/**
 * Profile Publication controller
 */
profileEditor.controller('PublicationController', function (profileService, navService, util, config, messageService, $filter) {
    var self = this;

    self.publications = [];
    self.opusId = util.getEntityId("opus");
    self.profileId = util.getEntityId("profile");

    var orderBy = $filter("orderBy");

    loadPublications();

    self.readonly = function () {
        return config.readonly;
    };

    self.savePublication = function () {
        var confirm = util.confirm("This will create a snapshot of the current public view of the profile. This snapshot cannot be removed. Do you wish to continue?");

        confirm.then(function() {
            var promise = profileService.createPublication(self.opusId, self.profileId);
            messageService.info("Creating snapshot. Please wait...");
            promise.then(function () {
                messageService.pop();

                loadPublications();
            }, function () {
                messageService.alert("An error occurred while creating the snapshot.");
            });
        });
    };

    self.mostRecentPublication = function() {
        return self.publications[0];
    };

    function loadPublications() {
        var promise = profileService.getPublications(self.opusId, self.profileId);
        messageService.info("Loading publications...");
        promise.then(function (data) {
                messageService.pop();

                console.log(data.length + " publications retreived");

                self.publications = data;

                self.publications = orderBy(self.publications, "publicationDate", true);

                if (self.publications.length > 0 || !self.readonly()) {
                    navService.add("Versions", "publications");
                }
            },
            function () {
                messageService.alert("An error occurred while retrieving publications.")
            }
        );
    }

});