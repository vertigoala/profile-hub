var profileEditor = angular.module('profileEditor', ['app.config', 'ui.bootstrap', 'leaflet-directive', 'colorpicker.module', 'angular-loading-bar', 'duScroll', 'ngFileUpload', 'checklist-model', 'ngCkeditor', 'ngPercentDisplay']);

profileEditor.config(function () {

});

profileEditor.config(['cfpLoadingBarProvider', function (cfpLoadingBarProvider) {
    cfpLoadingBarProvider.includeSpinner = false;
}]);

profileEditor.run(function ($rootScope, config) {
    $rootScope.config = config;

    CKEDITOR.plugins.addExternal('alaToolbar', config.contextPath + '/static/js/ckeditor/plugins/alaToolbar/');

    $rootScope.richTextSmall = {
        language: 'en-au',
        'skin': 'moono',
        removeButtons: '',
        removePlugins: '',
        height: 50,
        toolbar: [
            { name: 'basicstyles', items: [ 'Bold', 'Italic', 'Underline' ] }
        ]
    };

    $rootScope.richTextSimpleToolbar = {
        language: 'en-au',
        'skin': 'moono',
        removeButtons: '',
        removePlugins: '',
        toolbar: [
            { name: 'basicstyles', items: [ 'Bold', 'Italic', 'Underline' ] }
        ]
    };

    $rootScope.richTextFullToolbar = {
        language: 'en-au',
        'skin': 'moono',
        removeButtons: '',
        removePlugins: '',
        extraPlugins: 'symbol,sourcedialog,alaToolbar',
        toolbar: [
            { name: 'basicstyles', items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript' ] },
            { name: 'clipboard', items: ['PasteText', '-', 'Undo', 'Redo' ] },
            { name: 'paragraph', items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent' ] },
            { name: 'links', items: [ 'Link', 'Unlink' ] },
            { name: 'insert', items: [ 'Table', 'HorizontalRule', 'Symbol', 'Male', 'Female', 'PlusMinus', 'Endash' ] },
            { name: 'colors', items: [ 'TextColor', 'BGColor' ] },
            { name: 'tools', items: [ 'Maximize', '-', 'Sourcedialog' ] },
            { name: 'styles', items: [ 'Styles', 'Format', 'Font', 'FontSize' ] }
        ]
    };
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