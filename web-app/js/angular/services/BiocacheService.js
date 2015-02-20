/**
 * Angular service for interacting with the Biocache
 */
profileEditor.factory('biocacheService', function ($http, util) {
    return {
        getImages: function(query) {
            console.log("Retrieving images...");
            var future = $http.post(util.contextRoot() + "/profile/updateAttribute/" + profileId, data);
            future.then(function (response) {
                console.log(response.totalRecords + " images retrieved. Response Code =" + response.status)
            });
            return future
        }

    }
});