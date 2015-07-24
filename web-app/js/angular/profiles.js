var profileEditor = angular.module('profileEditor', ['app.config', 'ui.bootstrap', 'leaflet-directive', 'colorpicker.module', 'angular-loading-bar', 'textAngular', 'duScroll', 'ngFileUpload', 'checklist-model']);

profileEditor.config(function () {

});

profileEditor.config(['cfpLoadingBarProvider', function (cfpLoadingBarProvider) {
    cfpLoadingBarProvider.includeSpinner = false;
}]);

profileEditor.run(function ($rootScope, config) {
    $rootScope.config = config;

    $rootScope.richTextToolbarFull = "[['h1','h2','h3','p'],['bold','italics','underline'],['ul','ol'],['undo']]";
    $rootScope.richTextToolbarSimple = "[['bold','italics','underline']]";
});


/**
 *  ALA Admin controller
 */
profileEditor.controller('ALAAdminController', function ($http, util) {
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
});