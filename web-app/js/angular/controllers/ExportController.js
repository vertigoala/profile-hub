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
profileEditor.controller('ExportPDFController', function (opusId, rank, scientificName, $modalInstance, $scope, profileService) {
    var self = this;

    self.ASYNC_THRESHOLD = 11;

    self.loading = false;
    self.children = {id: "children", name: "Lower level taxa", selected: false};

    self.childCount = -1;

    self.options = [
        {id: "attributes", name: "Attributes", selected: true},
        {id: "map", name: "Map", selected: false},
        {id: "taxonomy", name: "Taxonomy", selected: true},
        {id: "nomenclature", name: "Nomenclature", selected: true},
        {id: "links", name: "Links", selected: false},
        {id: "bhllinks", name: "Biodiversity Heritage Library References", selected: false},
        {id: "specimens", name: "Specimens", selected: false},
        {id: "bibliography", name: "Bibliography", selected: false},
        {id: "images", name: "Images", selected: false},
        {id: "conservation", name: "Conservation Status", selected: false},
        {id: "features", name: "Features", selected: false},
        self.children
    ];

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