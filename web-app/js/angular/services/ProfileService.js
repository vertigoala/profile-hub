/**
 * Angular service for interacting with the profile service application
 */
profileEditor.service('profileService', function ($http, util, $q) {
    // The $http service returns an extended promise object which has success and error functions.
    // This introduces inconsistency with other code that deals with promises, and complicates the unit tests.
    // Therefore, we will create a new standard promise (which just uses then()) and return it instead.
    // http://weblog.west-wind.com/posts/2014/Oct/24/AngularJs-and-Promises-with-the-http-Service has a good explanation.
    function toStandardPromise(httpPromise) {
        var defer = $q.defer();

        httpPromise.success(function(data) {
            defer.resolve(data);
        });
        httpPromise.error(function(data, status, context, request) {
            var msg = "Failed to invoke URL " + request.url + ": Response code " + status;
            console.log(msg);
            defer.reject(msg);
        });

        return defer.promise;
    }

    return {
        getProfile: function (profileId) {
            console.log("Fetching profile " + profileId, {cache: true});

            var future = $http.get(util.contextRoot() + "/profile/json/" + profileId);
            future.then(function (response) {
                console.log("Profile fetched with response code " + response.status);
            });

            return toStandardPromise(future);
        },

        getOpusVocabulary: function (vocubularyId) {
            console.log("Fetching vocabulary...");
            var future = $http.get(util.contextRoot() + "/vocab/" + vocubularyId, {cache: true});
            future.then(function (response) {
                console.log("Vocab fetched with response code " + response.status)
            });
            return toStandardPromise(future);
        },

        getAuditForAttribute: function (attributeId) {
            console.log("Fetching audit for attribute " + attributeId);
            var future = $http.get(util.contextRoot() + "/audit/object/" + attributeId, {cache: true});
            future.then(function (response) {
                console.log("Audit fetched with response code " + response.status)
            });
            return toStandardPromise(future);
        },

        deleteAttribute: function (attributeId, profileId) {
            console.log("Deleting attribute " + attributeId);
            var future = $http.delete(util.contextRoot() + "/profile/deleteAttribute/" + attributeId + "?profileId=" + profileId, {cache: true});
            future.then(function (response) {
                console.log("Attribute deleted with response code " + response.status)
            });
            return toStandardPromise(future);
        },

        saveAttribute: function (profileId, attributeId, data) {
            console.log("Saving attribute " + attributeId);
            var future = $http.post(util.contextRoot() + "/profile/updateAttribute/" + profileId, data, {cache: true});
            future.then(function (response) {
                console.log("Attribute saved with response code " + response.status)
            });
            return toStandardPromise(future);
        },

        retrieveImages: function (searchIdentifier, imageSources) {
            console.log("Retrieving images for " + searchIdentifier);
            var future = $http.get(util.contextRoot() + "/profile/images?searchIdentifier=" + searchIdentifier + "&imageSources=" + imageSources, {cache: true});
            future.then(function (response) {
                console.log("Images retrieved with response code " + response.status)
            });
            return toStandardPromise(future);
        },

        retrieveLists: function (guid) {
            console.log("Retrieving lists for " + guid);
            var future = $http.get(util.contextRoot() + "/profile/lists?guid=" + guid, {cache: true});
            future.then(function (response) {
                console.log("Lists retrieved with response code " + response.status)
            });
            return toStandardPromise(future);
        },

        getClassifications: function (guid) {
            console.log("Retrieving classifications for " + guid);
            var future = $http.get(util.contextRoot() + "/profile/classifications?guid=" + guid, {cache: true});
            future.then(function (response) {
                console.log("Classifications retrieved with response code " + response.status)
            });
            return toStandardPromise(future);
        },

        getSpeciesProfile: function (guid) {
            console.log("Retrieving species profile for " + guid);
            var future = $http.get(util.contextRoot() + "/profile/speciesProfile?guid=" + guid, {cache: true});
            future.then(function (response) {
                console.log("Species Profile retrieved with response code " + response.status)
            });
            return toStandardPromise(future);
        }
    }
});
