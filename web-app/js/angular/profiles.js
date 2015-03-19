var profileEditor = angular.module('profileEditor', ['grails.config', 'ui.bootstrap', 'leaflet-directive', 'colorpicker.module', 'angular-loading-bar']);

profileEditor.config(function ($locationProvider) {
    // This disables 'hashbang' mode and removes the need to specify <base href="/my-base"> in the views.
    // This makes AngularJS take control of all links on the page: if you do not want Angular to control a particular
    // link, add target="_self".
    $locationProvider.html5Mode({enabled: true, requireBase: false});
});

profileEditor.config(['cfpLoadingBarProvider', function(cfpLoadingBarProvider) {
    cfpLoadingBarProvider.includeSpinner = false;
}]);