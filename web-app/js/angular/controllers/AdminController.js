/**
 * Created by shi131 on 4/03/2016.
 */
/**
 *  ALA Admin controller
 */
profileEditor.controller('ALAAdminController', function ($http, util, messageService) {
    var self = this;

    self.message = null;
    self.timestamp = null;

    var future = $http.get(util.contextRoot() + "/admin/message");
    future.then(function (response) {

        self.message = response.data.message;
        self.timestamp = response.data.timestamp;
    });

    self.postMessage = function () {
        $http.post(util.contextRoot() + "/admin/message", {message: self.message})
    };

    self.reloadConfig = function () {
        $http.post(util.contextRoot() + "/admin/reloadConfig")
    };

    self.reindex = function () {
        var promise = $http.post(util.contextRoot() + "/admin/reindex")
      promise.then(function () {
                messageService.success("Re-index started");
            },
            function () {
                messageService.alert("An error prevented the re-index.");
            }
        );
    };
});
