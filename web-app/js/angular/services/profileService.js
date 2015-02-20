/**
 * Angular service for interacting with the profile service application
 */
profileEditor.service('profileService', function ($http, util, $q) {
    return {
        getProfile: function (profileId) {
            console.log("Fetching profile " + profileId, {cache: true});
            var future = $http.get(util.contextRoot() + "/profile/json/" + profileId);
            future.then(function (response) {
                console.log("Profile fetched with response code " + response.status);
            });
            return future
        },

        getOpusVocabulary: function (vocubularyId) {
            console.log("Fetching vocabulary...");
            var future = $http.get(util.contextRoot() + "/vocab/" + vocubularyId, {cache: true});
            future.then(function (response) {
                console.log("Vocab fetched with response code " + response.status)
            });
            return future
        },

        getAuditForAttribute: function (attributeId) {
            console.log("Fetching audit for attribute " + attributeId);
            var future = $http.get(util.contextRoot() + "/audit/object/" + attributeId, {cache: true});
            future.then(function (response) {
                console.log("Audit fetched with response code " + response.status)
            });
            return future
        },

        deleteAttribute: function (attributeId, profileId) {
            console.log("Deleting attribute " + attributeId)
            var future = $http.delete(util.contextRoot() + "/profile/deleteAttribute/" + attributeId + "?profileId=" + profileId, {cache: true});
            future.then(function (response) {
                console.log("Attribute deleted with response code " + response.status)
            });
            return future
        },

        saveAttribute: function (profileId, attributeId, data) {
            console.log("Saving attribute " + attributeId);
            var future = $http.post(util.contextRoot() + "/profile/updateAttribute/" + profileId, data, {cache: true});
            future.then(function (response) {
                console.log("Attribute saved with response code " + response.status)
            });
            return future
        },

        retrieveImages: function (searchIdentifier, imageSources) {
            console.log("Retrieving images for " + searchIdentifier);
            var future = $http.get(util.contextRoot() + "/profile/retrieveImages?searchIdentifier=" + searchIdentifier + "&imageSources=" + imageSources, {cache: true});
            future.then(function (response) {
                console.log("Images retrieved with response code " + response.status)
            });
            return future
        }
    }
});
