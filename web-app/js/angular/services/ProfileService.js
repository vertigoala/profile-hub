/**
 * Angular service for interacting with the profile service application
 */
profileEditor.service('profileService', function ($http, util, $cacheFactory, config) {

    function clearCache() {
        console.log("Clearing $http cache");
        var httpCache = $cacheFactory.get('$http');
        httpCache.removeAll();
    }

    return {
        getProfile: function (opusId, profileId) {
            console.log("Fetching profile " + profileId);

            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/json", {cache: true});
            future.then(function (response) {
                console.log("Profile fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        deleteProfile: function(opusId, profileId) {
            console.log("Deleting profile " + profileId + " from opus " + opusId);
            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/delete");
            future.then(function (response) {
                console.log("Profile deleted with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        createProfile: function(opusId, scientificName) {
            console.log("Creating profile for " + scientificName + " in opus " + opusId);
            var future = $http.put(util.contextRoot() + "/opus/" + opusId + "/profile/create", {opusId: opusId, scientificName: scientificName});
            future.then(function(response) {
                console.log("Profile created with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        updateProfile: function(opusId, profileId, data) {
            console.log("Updating profile " + profileId)
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/update", data);
            future.then(function(response) {
                console.log("Profile updated with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        },

        getOpus: function (opusId) {
            // make sure we have a UUID, not just the last element of some other URL (e.g. create)
            if (!util.isUuid(opusId)) {
                return;
            }

            console.log("Fetching opus " + opusId);

            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/json", {cache: true});
            future.then(function (response) {
                console.log("Opus fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        listOpus: function() {
            console.log("Fetching all opuses");
            var future = $http.get(util.contextRoot() + "/opus/list", {cache: true});
            future.then(function(response) {
                console.log("Opus list fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        deleteOpus: function(opusId) {
            console.log("Deleting opus " + opusId);
            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/delete");
            future.then(function(response) {
                console.log("Opus deleted with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        saveOpus: function(opusId, opus) {
            var future;
            if (opusId) {
                console.log("Saving opus " + opusId);
                future = $http.post(util.contextRoot() + "/opus/" + opusId + "/update", opus);
            } else {
                console.log("Creating new opus...");
                future = $http.put(util.contextRoot() + "/opus/create", opus);
            }
            future.then(function(response) {
                console.log("Opus saved with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        },

        getOpusVocabulary: function (opusId, vocubularyId) {
            console.log("Fetching vocabulary " + vocubularyId);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/vocab/" + vocubularyId, {cache: true});
            future.then(function (response) {
                console.log("Vocab fetched with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        updateVocabulary: function(opusId, vocabularyId, data) {
            console.log("Updating vocabulary " + vocabularyId);
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/vocab/" + vocabularyId + "/update", data);
            future.then(function(response) {
                console.log("Vocab updated with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        findUsagesOfVocabTerm: function(opusId, vocabularyId, termName) {
            console.log("Finding usages of vocab term " + termName + " from vocab " + vocabularyId);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/vocab/" + vocabularyId + "/findUsages?termName=" + termName);
            future.then(function (response) {
                console.log("Usages found with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        replaceUsagesOfVocabTerm: function(opusId, vocabularyId, data) {
            console.log("Replacing usages of vocab terms");
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/vocab/" + vocabularyId + "/replaceUsages", data);
            future.then(function (response) {
                console.log("Terms replaced with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        getAuditForAttribute: function (attributeId) {
            console.log("Fetching audit for attribute " + attributeId);
            var future = $http.get(util.contextRoot() + "/audit/object/" + attributeId, {cache: true});
            future.then(function (response) {
                console.log("Audit fetched with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        deleteAttribute: function (opusId, profileId, attributeId) {
            console.log("Deleting attribute " + attributeId);
            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/attribute/" + attributeId + "/delete", {cache: true});
            future.then(function (response) {
                console.log("Attribute deleted with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        saveAttribute: function (opusId, profileId, attributeId, data) {
            console.log("Saving attribute " + attributeId);
            var future = null;

            if (attributeId) {
                future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/attribute/" + attributeId + "/update", data);
            } else {
                future = $http.put(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/attribute/create", data);
            }

            future.then(function (response) {
                console.log("Attribute saved with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        retrieveImages: function (opusId, profileId, searchIdentifier, imageSources) {
            console.log("Retrieving images for " + searchIdentifier);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/images?searchIdentifier=" + searchIdentifier + "&imageSources=" + imageSources, {cache: true});
            future.then(function (response) {
                console.log("Images retrieved with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        retrieveLists: function (opusId, profileId, guid) {
            console.log("Retrieving lists for " + guid);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/lists?guid=" + guid, {cache: true});
            future.then(function (response) {
                console.log("Lists retrieved with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        getAllLists: function () {
            console.log("Fetching all species lists...");

            var future = $http.get(util.contextRoot() + "/speciesList", {cache: true});
            future.then(function (response) {
                console.log("Species lists fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        getClassifications: function (opusId, profileId, guid) {
            console.log("Retrieving classifications for " + guid);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/classifications?guid=" + guid, {cache: true});
            future.then(function (response) {
                console.log("Classifications retrieved with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        getPublications: function (opusId, profileId) {
            console.log("Retrieving publications for profile " + profileId);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/publication", {cache: true});
            future.then(function (response) {
                console.log("Publication retrieved with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        deletePublication: function (opusId, profileId, publicationId) {
            console.log("Deleting publication " + publicationId);
            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/publication/" + publicationId + "/delete");
            future.then(function (response) {
                console.log("Publication deleted with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        savePublication: function (opusId, profileId, publicationId, data) {
            console.log("Saving publication " + publicationId);
            var future = null;

            if (publicationId) {
                future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/publication/" + publicationId + "/update", data);
            } else {
                future = $http.put(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/publication/create", data, {
                    transformRequest: angular.identity,
                    headers: {'Content-Type': undefined}
                });
            }

            future.then(function (response) {
                console.log("Publication saved with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        getSpeciesProfile: function (opusId, profileId, guid) {
            console.log("Retrieving species profile for " + guid);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/speciesProfile?guid=" + guid, {cache: true});
            future.then(function (response) {
                console.log("Species Profile retrieved with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        updateLinks: function(opusId, profileId, links) {
            console.log("Updating links for profile " + profileId);
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/links/update", links);
            future.then(function (response) {
                console.log("Links updated with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        updateBhlLinks: function(opusId, profileId, links) {
            console.log("Updating BHL links for profile " + profileId);
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/bhllinks/update", links);
            future.then(function (response) {
                console.log("BHL Links updated with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        lookupBhlPage: function(pageId) {
            console.log("Looking up BHL page " + pageId);
            var future = $http.get(util.contextRoot() + "/bhl/" + pageId);
            future.then(function (response) {
                console.log("BHL page retrieved with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        lookupSpecimenDetails: function(specimenId) {
            console.log("Looking up specimen details for id " + specimenId);

            var future = $http.get(util.contextRoot() + "/specimen/" + specimenId);
            future.then(function(response) {
                console.log("Specimen details retrieved with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        profileSearch: function(opusId, scientificName, useWildcard) {
            console.log("Searching for " + scientificName + (useWildcard ? " with wildcard" : ""));

            if (typeof useWildcard == 'undefined') {
                useWildcard = true;
            }
            var future = $http.get(util.contextRoot() + "/profile/search?opusId=" + opusId + "&scientificName=" + scientificName + "&useWildcard=" + useWildcard);
            future.then(function (response) {
                console.log("Profile search returned with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        listResources: function () {
            console.log("Fetching all resources");

            var future = $http.get(util.contextRoot()+ "/dataResource");
            future.then(function (response) {
                console.log("Resources fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        getResource: function (dataResourceId) {
            console.log("Fetching resource " + dataResourceId);

            var future = $http.get(util.contextRoot()+ "/dataResource/" + dataResourceId);
            future.then(function (response) {
                console.log("Resource fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        userSearch: function(email) {
            console.log("Searching for user " + email);

            var future = $http.get(util.contextRoot() + "/user/search?userName=" + email);
            future.then(function (response) {
                console.log("Search completed with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        updateUsers: function(opusId, admins, editors) {
            console.log("Updating users for opus " + opusId);

            var data = {
                opusId: opusId,
                admins: admins,
                editors: editors
            };

            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/users/update", data);
            future.then(function (response) {
                console.log("Update Users completed with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        },

        uploadGlossary: function(opusId, data) {
            console.log("Uploading glossary for opus " + opusId);

            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/glossary/upload", data, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            });
            future.then(function (response) {
                console.log("Uploaded glossary with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        },

        getGlossary: function(opusId, prefix) {
            console.log("Fetching glossary for opus " + opusId);

            var future = $http.get(config.profileServiceUrl + "/glossary/" + opusId + "/" + prefix);
            future.then(function (response) {
                console.log("Glossary fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        deleteGlossaryItem: function(opusId, glossaryItemId) {
            console.log("Deleting glossary item " + glossaryItemId);

            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/glossary/item/" + glossaryItemId + "/delete");
            future.then(function (response) {
                console.log("Glossary item deleted with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        saveGlossaryItem: function(opusId, glossaryItemId, data) {
            console.log("Updating glossary item " + glossaryItemId);

            var future;
            if (glossaryItemId) {
                future = $http.post(util.contextRoot() + "/opus/" + opusId + "/glossary/item/" + glossaryItemId + "/update", data);
            } else {
                future = $http.put(util.contextRoot() + "/opus/" + opusId + "/glossary/item/create", data);
            }
            future.then(function (response) {
                console.log("Glossary item updated with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        }
    }
});
