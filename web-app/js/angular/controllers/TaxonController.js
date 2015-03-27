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

    //self.taxaUpload = function(){
    //    console.log("Taxa upload....");
    //    var file = document.getElementById('taxaUploadFile').files[0];
    //    var formData = new FormData();
    //    formData.append("taxaUploadFile", file);
    //    formData.append("opusId", "${opus.uuid}");
    //
    //    //send you binary data via $http or $resource or do anything else with it
    //    $.ajax({
    //        url: '${grailsApplication.config.profile.service.url}/opus/taxaUpload',
    //        type: 'POST',
    //        data: formData,
    //        cache: false,
    //        dataType: 'json',
    //        processData: false, // Don't process the files
    //        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
    //        success: function(data, textStatus, jqXHR){
    //            alert("Successful upload - Loaded:" + data.taxaCreated + ", lines skipped: " + data.linesSkipped + ", already exists: " + data.alreadyExists);
    //        },
    //        error: function(jqXHR, textStatus, errorThrown){
    //            // Handle errors here
    //            alert("error upload - " + textStatus);
    //            console.log('ERRORS: ' + textStatus);
    //            console.log(errorThrown)
    //            // STOP LOADING SPINNER
    //        }
    //    });
    //};

    //$(":file").filestyle();
});