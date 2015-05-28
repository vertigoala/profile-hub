/**
 * Taxon controller
 */
profileEditor.controller('TaxonController', function (profileService, util, messageService, $modal) {
    var self = this;

    self.speciesProfile = null;
    self.classifications = [];
    self.infraspecificTaxa = [];

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

                if (self.profile.rank == util.RANK.SPECIES) {
                    loadInfraspecificTaxa();
                }
            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    self.showChildren = function(level, scientificName) {
        var result = profileService.profileSearchByTaxonLevel(self.opusId, level, 1000, 0);

        result.then(function(data) {
            $modal.open({
                templateUrl: "showTaxonChildren.html",
                controller: "TaxonChildrenController",
                controllerAs: "taxonChildrenCtrl",
                size: "lg",
                resolve: {
                    taxon: function() {
                        return {level: level, scientificName: scientificName, count: data[scientificName]};
                    }
                }
            });
        })
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

    function loadInfraspecificTaxa() {
        var results = profileService.profileSearchByTaxonLevelAndName(self.opusId, util.RANK.SPECIES, self.profile.scientificName, 25, 0);
        results.then(function (data) {

                angular.forEach(data, function(subSpecies) {
                    console.log("subspecies: " + JSON.stringify(subSpecies))
                    if (subSpecies.scientificName != self.profile.scientificName && subSpecies.rank == util.RANK.SUBSPECIES) {
                        self.infraspecificTaxa.push(subSpecies);
                    }
                });
            },
            function () {
                console.log("Failed to retrieve infraspecific taxa");
            }
        );
    }
});




/**
 * Controller for the popup modal dialog showing the list of child taxa
 */
profileEditor.controller('TaxonChildrenController', function (profileService, util, $modalInstance, taxon) {
    var self = this;

    self.pageSize = 10;
    self.taxon = taxon;
    self.opusId = util.getEntityId("opus");

    self.loadChildren = function(offset) {
        if (offset === undefined) {
            offset = 0;
        }

        var results = profileService.profileSearchByTaxonLevelAndName(self.opusId, taxon.level, taxon.scientificName, self.pageSize, offset);
        results.then(function (data) {
                console.log("Found " + data.length + " results");
                self.profiles = data;
                self.taxon.count
            },
            function () {
                messageService.alert("Failed to perform search for '" + self.searchTerm + "'.");
            }
        );
    };

    self.loadChildren(0);

    self.cancel = function() {
        $modalInstance.dismiss("cancel");
    }
});
