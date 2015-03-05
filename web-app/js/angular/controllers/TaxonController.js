/**
 * Taxon controller
 */
profileEditor.controller('TaxonController', function ($scope, profileService, util, messageService) {

    $scope.speciesProfile = null;
    $scope.classifications = [];

    $scope.init = function (edit) {
        $scope.readonly = edit != 'true';

        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));
        profilePromise.then(function (data) {
                $scope.profile = data.profile;
                $scope.opus = data.opus;

                loadSpeciesProfile();
                loadClassifications();
            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    function loadClassifications() {
        if ($scope.profile.guid) {
            messageService.info("Loading taxonomy...");

            var promise = profileService.getClassifications($scope.profile.guid);
            promise.then(function (data) {
                    console.log("Fetched " + data.length + " classifications");

                    $scope.classifications = data;
                    messageService.pop();
                },
                function () {
                    messageService.alert("An error occurred while retrieving the taxonomy.");
                }
            );
        }
    }

    function loadSpeciesProfile() {
        if ($scope.profile.guid) {
            messageService.info("Loading taxon...");

            var promise = profileService.getSpeciesProfile($scope.profile.guid);
            promise.then(function (data) {
                    console.log("Fetched species profile");

                    $scope.speciesProfile = data;
                    messageService.pop();
                },
                function () {
                    messageService.alert("An error occurred while retrieving the taxonomy.");
                }
            );
        }
    }

    //$scope.taxaUpload = function(){
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