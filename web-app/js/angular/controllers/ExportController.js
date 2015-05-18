/**
 *  Export controller
 */
profileEditor.controller('ExportController', function (util, $window, $modal) {
    var self = this;

    self.profileId = util.getEntityId("profile");
    self.opusId = util.getEntityId("opus");

    self.exportPdf = function() {
        var popup = $modal.open({
            templateUrl: "exportPdf.html",
            controller: "ExportPDFController",
            controllerAs: "pdfCtrl",
            size: "sm",
            resolve: {

            }
        });

        popup.result.then(function(options) {
            var queryString = [];

            angular.forEach(options, function(option) {
                if (option.selected) {
                    queryString.push(option.id + "=true");
                }
            });

            $window.open(util.contextRoot() + "/opus/" + self.opusId + "/profile/" + self.profileId + "/pdf?" + queryString.join("&"))
        });
    }
});



/**
 * Export pdf modal dialog controller
 */
profileEditor.controller('ExportPDFController', function ($modalInstance) {
    var self = this;

    self.options = [
        {id: "attributes", name: "Attributes", selected: true},
        {id: "map", name: "Map", selected: true},
        {id: "taxonomy", name: "Taxonomy", selected: true},
        {id: "nomenclature", name: "Nomenclature", selected: true},
        {id: "links", name: "Links", selected: false},
        {id: "bhllinks", name: "Biodiversity Heritage Library References", selected: false},
        {id: "specimens", name: "Specimens", selected: false},
        {id: "bibliography", name: "Bibliography", selected: false},
        {id: "images", name: "Images", selected: false},
        {id: "conservation", name: "Conservation Status", selected: false}
    ];

    self.ok = function() {
        $modalInstance.close(self.options);
    };

    self.cancel = function() {
        $modalInstance.dismiss("cancel");
    }
});