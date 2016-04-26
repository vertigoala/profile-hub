/**
 *  ALA Admin controller
 */
profileEditor.controller('ALAAdminController', function ($http, util, messageService) {
    var self = this;

    self.reindex = function () {
        var promise = $http.post(util.contextRoot() + "/admin/reindex");

        promise.then(function () {
                messageService.success("Re-index started");
            },
            function () {
                messageService.alert("An error prevented the re-index.");
            }
        );
    };
});
