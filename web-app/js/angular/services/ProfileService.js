/**
 * Angular service for interacting with the profile service application
 */
profileEditor.service('profileService', function ($http, util, $cacheFactory, config, $log) {

    function clearCache() {
        $log.debug("Clearing $http cache");
        var httpCache = $cacheFactory.get('$http');
        httpCache.removeAll();
    }

    return {
        getProfile: function (opusId, profileId) {
            $log.debug("Fetching profile " + profileId);

            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/json", {cache: true});
            future.then(function (response) {
                $log.debug("Profile fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        deleteProfile: function(opusId, profileId) {
            $log.debug("Deleting profile " + profileId + " from opus " + opusId);
            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/delete");
            future.then(function (response) {
                $log.debug("Profile deleted with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        archiveProfile: function(opusId, profileId, archiveComment) {
            $log.debug("Archiving profile " + profileId + " from opus " + opusId);
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/archive", {archiveComment: archiveComment});
            future.then(function (response) {
                $log.debug("Profile archived with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        restoreArchivedProfile: function(opusId, profileId, newName) {
            $log.debug("Restoring archived profile " + profileId + " from opus " + opusId);
            if (!newName) {
                newName = null;
            }

            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/restore", {newName: newName});
            future.then(function (response) {
                $log.debug("Profile restored with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        createProfile: function(opusId, scientificName, manuallyMatchedGuid) {
            $log.debug("Creating profile for " + scientificName + " in opus " + opusId);
            var future = $http.put(util.contextRoot() + "/opus/" + opusId + "/profile/create", {opusId: opusId, scientificName: scientificName, manuallyMatchedGuid: manuallyMatchedGuid});
            future.then(function(response) {
                $log.debug("Profile created with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        updateProfile: function(opusId, profileId, data) {
            $log.debug("Updating profile " + profileId);
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/update", data);
            future.then(function(response) {
                $log.debug("Profile updated with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        },

        checkName: function(opusId, scientificName) {
            $log.debug("Checking name " + scientificName);

            var future = $http.get(util.contextRoot() + "/checkName?opusId=" + opusId + "&scientificName=" + encodeURIComponent(scientificName));
            future.then(function(response)  {
                $log.debug("Name checked with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        renameProfile: function(opusId, profileId, data) {
            $log.debug("Renaming profile " + profileId);

            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/rename", data);
            future.then(function(response) {
                $log.debug("Profile renamed with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        toggleDraftMode: function(opusId, profileId, snapshot) {
            $log.debug("Toggling draft mode for profile " + profileId);
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/toggleDraftMode?snapshot=" + snapshot);
            future.then(function(response) {
                $log.debug("Profile updated with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        },

        discardDraftChanges: function(opusId, profileId) {
            $log.debug("Discarding draft changes for profile " + profileId);
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/discardDraftChanges");
            future.then(function(response) {
                $log.debug("Profile updated with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        },

        getOpus: function (opusId) {
            $log.debug("Fetching opus " + opusId);

            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/json", {cache: true});
            future.then(function (response) {
                $log.debug("Opus fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        getOpusAbout: function (opusId) {
            $log.debug("Fetching about page for opus " + opusId);

            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/about/json", {cache: true});
            future.then(function (response) {
                $log.debug("Opus fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        updateOpusAbout: function (opusId, aboutHtml, citationHtml) {
            $log.debug("Updating about page for opus " + opusId);

            var future = $http.put(util.contextRoot() + "/opus/" + opusId + "/about/update", {opusId: opusId, aboutHtml: aboutHtml, citationHtml: citationHtml});
            future.then(function (response) {
                $log.debug("Opus fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        listOpus: function() {
            $log.debug("Fetching all opuses");
            var future = $http.get(util.contextRoot() + "/opus/list", {cache: true});
            future.then(function(response) {
                $log.debug("Opus list fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        deleteOpus: function(opusId) {
            $log.debug("Deleting opus " + opusId);
            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/delete");
            future.then(function(response) {
                $log.debug("Opus deleted with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        saveOpus: function(opusId, opus) {
            var future;
            if (opusId) {
                $log.debug("Saving opus " + opusId);
                future = $http.post(util.contextRoot() + "/opus/" + opusId + "/update", opus);
            } else {
                $log.debug("Creating new opus...");
                future = $http.put(util.contextRoot() + "/opus/create", opus);
            }
            future.then(function(response) {
                $log.debug("Opus saved with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        },

        generateAccessTokenForOpus: function(opusId) {
            $log.debug("Creating access token for opus " + opusId);

            var future = $http.put(util.contextRoot() + "/opus/" + opusId + "/access/token");
            future.then(function(response) {
                $log.debug("Created access token with " + response.status);
            });

            return util.toStandardPromise(future);
        },

        revokeAccessTokenForOpus: function(opusId) {
            $log.debug("Revoking access token for opus " + opusId);

            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/access/token");
            future.then(function(response) {
                $log.debug("Revoked access token with " + response.status);
            });

            return util.toStandardPromise(future);
        },

        updateSupportingCollections: function(opusId, supportingCollections) {
            $log.debug("Updating supporting collections for " + opusId);

            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/supportingCollections/update", supportingCollections);
            future.then(function(response) {
                $log.debug("Supporting collections updated with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        respondToSupportingCollectionRequest: function(opusId, requestingOpusId, accept) {
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/supportingCollections/respond/" + requestingOpusId + "/" + accept);
            future.then(function(response) {
                $log.debug("Supporting collections request responded to with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        getOpusVocabulary: function (opusId, vocubularyId) {
            $log.debug("Fetching vocabulary " + vocubularyId);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/vocab/" + vocubularyId, {cache: true});
            future.then(function (response) {
                $log.debug("Vocab fetched with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        updateVocabulary: function(opusId, vocabularyId, data) {
            $log.debug("Updating vocabulary " + vocabularyId);
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/vocab/" + vocabularyId + "/update", data);
            future.then(function(response) {
                $log.debug("Vocab updated with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        findUsagesOfVocabTerm: function(opusId, vocabularyId, termName) {
            $log.debug("Finding usages of vocab term " + termName + " from vocab " + vocabularyId);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/vocab/" + vocabularyId + "/findUsages?termName=" + termName);
            future.then(function (response) {
                $log.debug("Usages found with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        replaceUsagesOfVocabTerm: function(opusId, vocabularyId, data) {
            $log.debug("Replacing usages of vocab terms");
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/vocab/" + vocabularyId + "/replaceUsages", data);
            future.then(function (response) {
                $log.debug("Terms replaced with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        getAuditHistory: function (objectId) {
            $log.debug("Fetching audit for object " + objectId);
            var future = $http.get(util.contextRoot() + "/audit/object/" + objectId, {cache: true});
            future.then(function (response) {
                $log.debug("Audit fetched with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        deleteAttribute: function (opusId, profileId, attributeId) {
            $log.debug("Deleting attribute " + attributeId);
            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/attribute/" + attributeId + "/delete");
            future.then(function (response) {
                $log.debug("Attribute deleted with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        saveAttribute: function (opusId, profileId, attributeId, data) {
            $log.debug("Saving attribute " + attributeId);
            var future = null;

            if (attributeId) {
                future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/attribute/" + attributeId + "/update", data);
            } else {
                future = $http.put(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/attribute/create", data);
            }

            future.then(function (response) {
                $log.debug("Attribute saved with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        getPrimaryImage: function(opusId, profileId) {
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/primaryImage");
            return util.toStandardPromise(future);
        },

        retrieveImages: function (opusId, profileId, searchIdentifier, imageSources) {
            $log.debug("Retrieving images for " + searchIdentifier);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/images?searchIdentifier=" + searchIdentifier + "&imageSources=" + imageSources, {cache: true});
            future.then(function (response) {
                $log.debug("Images retrieved with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        getImageMetadata: function(imageId) {
            var future = $http.get(config.imageServiceUrl + "/ws/image/" + imageId, {cache: true});
            return util.toStandardPromise(future);
        },

        publishPrivateImage: function(opusId, profileId, imageId) {
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/image/" + imageId + "/publish");
            future.then(function(response) {
                $log.debug("Image published wtih response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        deleteLocalImage: function(opusId, profileId, imageId, type) {
            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/image/" + imageId + "/delete?type=" + type);
            future.then(function(response) {
                $log.debug("Image deleted with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        },

        retrieveLists: function (opusId, profileId, guid) {
            $log.debug("Retrieving lists for " + guid);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/lists?guid=" + guid, {cache: true});
            future.then(function (response) {
                $log.debug("Lists retrieved with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        getAllLists: function () {
            $log.debug("Fetching all species lists...");

            var future = $http.get(util.contextRoot() + "/speciesList", {cache: true});
            future.then(function (response) {
                $log.debug("Species lists fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        getPublications: function (opusId, profileId) {
            $log.debug("Retrieving publications for profile " + profileId);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/publication", {cache: true});
            future.then(function (response) {
                $log.debug("Publication retrieved with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        deletePublication: function (opusId, profileId, publicationId) {
            $log.debug("Deleting publication " + publicationId);
            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/publication/" + publicationId + "/delete");
            future.then(function (response) {
                $log.debug("Publication deleted with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        createPublication: function (opusId, profileId) {
            $log.debug("Creating publication...");
            var future = $http.put(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/publication/create");

            future.then(function (response) {
                $log.debug("Publication saved with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        getSpeciesProfile: function (opusId, profileId, guid) {
            $log.debug("Retrieving species profile for " + guid);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/speciesProfile?guid=" + guid, {cache: true});
            future.then(function (response) {
                $log.debug("Species Profile retrieved with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        getFeature: function(opusId, profileId){
            $log.debug("Retrieving bio status for " + profileId);
            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/featureList");
            future.then(function (response) {
                $log.debug("Bio status retrieved with response code " + response.status)
            });
            return util.toStandardPromise(future);
        },

        updateLinks: function(opusId, profileId, links) {
            $log.debug("Updating links for profile " + profileId);
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/links/update", links);
            future.then(function (response) {
                $log.debug("Links updated with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        updateBhlLinks: function(opusId, profileId, links) {
            $log.debug("Updating BHL links for profile " + profileId);
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/bhllinks/update", links);
            future.then(function (response) {
                $log.debug("BHL Links updated with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        lookupBhlPage: function(pageId) {
            $log.debug("Looking up BHL page " + pageId);
            var future = $http.get(util.contextRoot() + "/bhl/" + pageId);
            future.then(function (response) {
                $log.debug("BHL page retrieved with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        lookupSpecimenDetails: function(specimenId) {
            $log.debug("Looking up specimen details for id " + specimenId);

            var future = $http.get(util.contextRoot() + "/specimen/" + specimenId);
            future.then(function(response) {
                $log.debug("Specimen details retrieved with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        search: function(opusId, term, options) {
            var queryParams = "";
            if (options) {
                for (var key in options) {
                    if (options.hasOwnProperty(key)) {
                        queryParams += "&" + key + "=" + options[key]
                    }
                }
            }

            var future = $http.get(util.contextRoot() + "/profile/search?opusId=" + opusId + "&term=" + term + queryParams);
            future.then(function (response) {
                $log.debug("Profile search returned with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        profileSearch: function(opusId, scientificName, useWildcard, sortBy) {
            $log.debug("Searching for " + scientificName + (useWildcard ? " with wildcard" : ""));

            if (typeof sortBy == 'undefined') {
                sortBy = "name";
            }
            if (typeof useWildcard == 'undefined') {
                useWildcard = true;
            }
            if (typeof opusId == 'undefined') {
                opusId = "";
            }
            var future = $http.get(util.contextRoot() + "/profile/search/scientificName?opusId=" + opusId + "&scientificName=" + scientificName + "&useWildcard=" + useWildcard + "&sortBy=" + sortBy);
            future.then(function (response) {
                $log.debug("Profile search returned with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        profileSearchByTaxonLevel: function(opusId, taxon, max, offset) {
            var future = $http.get(util.contextRoot() + "/profile/search/taxon/level?opusId=" + opusId + "&taxon=" + taxon + "&max=" + max + "&offset=" + offset);
            future.then(function(response) {
                $log.debug("Facet search returned with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        profileSearchByTaxonLevelAndName: function(opusId, taxon, scientificName, max, offset, sortBy, countChildren) {
            if (_.isUndefined(countChildren)) {
                countChildren = false;
            }
            if (_.isUndefined(sortBy)) {
                sortBy = "name";
            }
            var future = $http.get(util.contextRoot() + "/profile/search/taxon/name?opusId=" + opusId + "&taxon=" + taxon + "&scientificName=" + scientificName + "&max=" + max + "&offset=" + offset + "&sortBy=" + sortBy + "&countChildren=" + countChildren);
            future.then(function(response) {
                $log.debug("Facet search returned with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        profileSearchGetImmediateChildren: function(opusId, rank, name, max, offset) {
            var future = $http.get(util.contextRoot() + "/profile/search/children?opusId=" + opusId + "&rank=" + rank + "&name=" + name + "&max=" + max + "&offset=" + offset);
            future.then(function(response) {
                $log.debug("Child search returned with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        getTaxonLevels: function(opusId) {
            var future = $http.get(util.contextRoot() + "/profile/search/taxon/levels?opusId=" + opusId);
            future.then(function(response) {
                $log.debug("Get taxon levels returned with response code " + response.status);
            });
            return util.toStandardPromise(future);
        },

        listResources: function () {
            $log.debug("Fetching all resources");

            var future = $http.get(util.contextRoot()+ "/dataResource");
            future.then(function (response) {
                $log.debug("Resources fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        getResource: function (dataResourceId) {
            $log.debug("Fetching resource " + dataResourceId);

            var future = $http.get(util.contextRoot()+ "/dataResource/" + dataResourceId);
            future.then(function (response) {
                $log.debug("Resource fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        userSearch: function(email) {
            $log.debug("Searching for user " + email);

            var future = $http.get(util.contextRoot() + "/user/search?userName=" + email);
            future.then(function (response) {
                $log.debug("Search completed with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        updateUsers: function(opusId, users) {
            $log.debug("Updating users for opus " + opusId);
            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/users/update", users);
            future.then(function (response) {
                $log.debug("Update Users completed with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        },

        uploadGlossary: function(opusId, data) {
            $log.debug("Uploading glossary for opus " + opusId);

            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/glossary/upload", data, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            });
            future.then(function (response) {
                $log.debug("Uploaded glossary with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        },

        getGlossary: function(opusId, prefix) {
            $log.debug("Fetching glossary for opus " + opusId);

            var future = $http.get(config.profileServiceUrl + "/opus/" + opusId + "/glossary/" + prefix);
            future.then(function (response) {
                $log.debug("Glossary fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        deleteGlossaryItem: function(opusId, glossaryItemId) {
            $log.debug("Deleting glossary item " + glossaryItemId);

            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/glossary/item/" + glossaryItemId + "/delete");
            future.then(function (response) {
                $log.debug("Glossary item deleted with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        saveGlossaryItem: function(opusId, glossaryItemId, data) {
            $log.debug("Updating glossary item " + glossaryItemId);

            var future;
            if (glossaryItemId) {
                future = $http.post(util.contextRoot() + "/opus/" + opusId + "/glossary/item/" + glossaryItemId + "/update", data);
            } else {
                future = $http.put(util.contextRoot() + "/opus/" + opusId + "/glossary/item/create", data);
            }
            future.then(function (response) {
                $log.debug("Glossary item updated with response code " + response.status);

                clearCache();
            });

            return util.toStandardPromise(future);
        },

        addComment: function(opusId, profileId, data) {
            $log.debug("Creating comment for profile " + profileId);

            var future = $http.put(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/comment/create", data);
            future.then(function(response) {
                $log.debug("Comment created with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future)
        },

        updateComment: function(opusId, profileId, commentId, data) {
            $log.debug("Updating comment for profile " + profileId);

            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/comment/" + commentId + "/update", data);
            future.then(function(response) {
                $log.debug("Comment updated with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future)
        },

        getComments: function(opusId, profileId) {
            $log.debug("Fetching comments for profile " + profileId);

            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/comment");
            future.then(function(response) {
                $log.debug("Comments fetched with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future)
        },

        deleteComment: function(opusId, profileId, commentId) {
            $log.debug("Deleting comment " + commentId);

            var future = $http.delete(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/comment/" + commentId + "/delete");
            future.then(function(response) {
                $log.debug("Comment deleted with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future)
        },

        saveAuthorship: function(opusId, profileId, authorship) {
            $log.debug("Saving authorship for profile " + profileId);

            var future = $http.post(util.contextRoot() + "/opus/" + opusId + "/profile/" + profileId + "/authorship/update", authorship);
            future.then(function(response) {
                $log.debug("Authorship updated with response code " + response.status);

                clearCache();
            });
            return util.toStandardPromise(future);
        },

        retrieveKeybaseProjects: function() {
            $log.debug("Retrieving keybase projects");

            var future = $http.get(util.contextRoot() + "/keybase/projects", {cache: true});
            future.then(function(response) {
                $log.debug("Keybase projects retreived with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        getLicences: function() {
            var future = $http.get(util.contextRoot() + "/licences", {cache: true});
            return util.toStandardPromise(future);
        },

        loadReport: function(opusId, reportId, pageSize, offset, period, from, to,
                             isCountOnly) {
            $log.debug("Loading report " + reportId);

            // these parameters are for recent updates. add them only if value is present.
            var dateParms = '';
            if(period){
                dateParms = "&period=" + period;
                if(from && to){
                    dateParms += "&from=" + from + "&to=" + to;
                }
            }

            var countParms = '';
            if (isCountOnly) {
                countParms = "&countOnly=true";
            }

            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/report/"
                + reportId + "?pageSize=" + pageSize + "&offset=" + offset + dateParms
                + countParms);
            future.then(function(response) {
                $log.debug("Report loaded with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        getNomenclatureList: function(nslNameIdentifier) {
            $log.debug("Fetching nomenclature list for " + nslNameIdentifier);

            var future = $http.get(util.contextRoot() + "/nsl/listConcepts/" + nslNameIdentifier, {cache: true});
            future.then(function (response) {
                $log.debug("Nomenclature concepts fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        getNslNameDetails: function(nslNameIdentifier) {
            $log.debug("Fetching name details for NSL name " + nslNameIdentifier);

            var future = $http.get(util.contextRoot() + "/nsl/nameDetails/" + nslNameIdentifier, {cache: true});
            return util.toStandardPromise(future);
        },

        getPublicationsFromId: function(pubId){
            $log.debug("Fetching publication with Publication Id:" + pubId);

            var future = $http.get(util.contextRoot() + "/publication/" + pubId + "/json", {cache: true});
            future.then(function (response) {
                $log.debug("Publications fetched with " + response.status);
            });

            return util.toStandardPromise(future);
        },

        getStatistics: function(opusId) {
            $log.debug("Fetching statistics");

            var future = $http.get(util.contextRoot() + "/opus/" + opusId + "/statistics", {cache: true});
            future.then(function (response) {
               $log.debug("Statistics fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        },

        getUserDetails: function(opusId) {
            var future = $http.get(util.contextRoot() + "/user/details?opusId=" + opusId, {cache: true});
            future.then(function (response) {
                $log.debug("User details fetched with response code " + response.status);
            });

            return util.toStandardPromise(future);
        }
    }
});
