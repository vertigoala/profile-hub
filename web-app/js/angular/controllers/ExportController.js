/**
 *  Export controller
 */
profileEditor.controller('ExportController', function (util, $window, $modal, $http) {
    var self = this;

    self.profileId = util.getEntityId("profile");
    self.opusId = util.getEntityId("opus");

    self.exportPdf = function(rank, scientificName) {
        var popup = $modal.open({
            templateUrl: "exportPdf.html",
            controller: "ExportPDFController",
            controllerAs: "pdfCtrl",
            size: "md",
            resolve: {
                opusId: function() {
                    return self.opusId;
                },
                rank: function() {
                    return rank;
                },
                scientificName: function() {
                    return scientificName;
                }
            }
        });

        popup.result.then(function(result) {
            var queryString = [];

            var childrenSelected = false;
            angular.forEach(result.options, function(option) {
                if (option.selected) {
                    queryString.push(option.id + "=true");
                    if (option.id === "children") {
                        childrenSelected = true;
                    }
                }
            });

            if (result.email) {
                queryString.push("&email=" + result.email);
            }

            var url = util.contextRoot() + "/opus/" + self.opusId + "/profile/" + self.profileId + "/pdf?" + queryString.join("&");
            if (!childrenSelected || !result.email) {
                $window.open(url);
            } else {
                $http.get(url);
            }
        });
    }
});



/**
 * Export pdf modal dialog controller
 */
profileEditor.controller('ExportPDFController', function (opusId, rank, scientificName, $modalInstance, $scope, profileService, $rootScope) {
    var self = this;

    self.ASYNC_THRESHOLD = 11;

    self.loading = false;
    self.children = {id: "children", name: "Lower level taxa", selected: true};

    self.childCount = -1;

    var exportableSections = ["attributes", "map", "taxonomy", "taxon", "nomenclature", "links", "bhllinks", "specimens", "bibliography", "images", "conservation", "features", "key"];

    self.options = [];
    var attributeAdded = false;
    angular.forEach($rootScope.nav, function (navItem) {
        if ((_.isUndefined(navItem.category) || !navItem.category || navItem.category == 'pdf') && exportableSections.indexOf(navItem.key) > -1) {
            self.options.push({id: navItem.key, name: navItem.label, selected: true});
        } else if (navItem.category == "attribute" && !attributeAdded) {
            self.options.push({id: "attributes", name: "Attributes", selected: true});
            attributeAdded = true;
        }
    });
    self.options.push(self.children);

    self.email = null;

    self.ok = function() {
        $modalInstance.close({options: self.options, email: self.email});
    };

    self.cancel = function() {
        $modalInstance.dismiss("cancel");
    };

    $scope.$watch(function() {return self.children.selected}, function() {
        if (self.childCount == -1 && self.children.selected) {
            self.loading = true;
            profileService.profileSearchByTaxonLevelAndName(opusId, rank, scientificName, 9999, 0).then(function(data) {
                self.childCount = data.length;
                self.loading = false;
            });
        }
    })

});