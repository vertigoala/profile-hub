/**
 * Profile Publication controller
 */
profileEditor.controller('PublicationController', function (profileService, util, config, messageService, $filter) {
    var self = this;

    self.publications = [];
    self.opusId = util.getEntityId("opus");
    self.profileId = util.getEntityId("profile");
    self.newPublication = null;

    var orderBy = $filter("orderBy");

    loadPublications();

    self.readonly = function () {
        return config.readonly;
    };

    self.addPublication = function (form) {
        self.newPublication = {publicationId: ""};
        form.$setDirty();
    };

    self.savePublication = function (form) {
        var formData = new FormData();
        formData.append("file", self.newFile);
        formData.append("publication", JSON.stringify(self.newPublication));

        var promise = profileService.savePublication(self.opusId, self.profileId, null, formData);
        messageService.info("Saving publication...");
        promise.then(function () {
            messageService.pop();
            form.$setPristine();
            self.newPublication = null;
            self.newFile = null;
            loadPublications();
        }, function () {
            messageService.alert("An error occurred while saving the publication.");
        });
    };

    self.cancelNewPublication = function(form) {
        self.newPublication = null;
        self.newFile = null;
        form.$setPristine();
    };

    self.deletePublication = function(index) {
        var confirmed = util.confirm("Are you sure you wish to delete this publication?");

        confirmed.then(function () {
            var publication = self.publications[index];
            var promise = profileService.deletePublication(self.opusId, self.profileId, publication.uuid);
            promise.then(function () {
                    self.publications.splice(index, 1);
                    messageService.success("Publication successfully deleted.")
                },
                function () {
                    messageService.alert("An error occurred while deleting the publication.");
                }
            );
        })
    };

    self.mostRecentPublication = function() {
        return self.publications[0];
    };

    self.uploadFile = function(element) {
        self.newFile = element.files[0]
    };

    function loadPublications() {
        var promise = profileService.getPublications(self.opusId, self.profileId);
        messageService.info("Loading publications...");
        promise.then(function (data) {
                messageService.pop();

                console.log(data.length + " publications retreived");

                self.publications = data;

                self.publications = orderBy(self.publications, "publicationDate", true);
            },
            function () {
                messageService.alert("An error occurred while retrieving publications.")
            }
        );
    }

});