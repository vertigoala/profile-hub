var profileEditor = angular.module('profileEditor', ['app.config', 'ui.bootstrap', 'colorpicker.module', 'angular-loading-bar', 'duScroll', 'ngFileUpload', 'checklist-model', 'ngCkeditor', 'angular-inview', 'ngStorage', 'truncate']);

profileEditor.config(function ($logProvider) {
    $logProvider.debugEnabled(false);

});

profileEditor.config(['cfpLoadingBarProvider', function (cfpLoadingBarProvider) {
    cfpLoadingBarProvider.includeSpinner = false;
}]);

profileEditor.run(function ($rootScope, config) {
    $rootScope.config = config;

    CKEDITOR.plugins.addExternal('alaToolbar', config.contextPath + '/static/js/ckeditor/plugins/alaToolbar/');
    // use HTML4 elements for Jasper compatibility.
    CKEDITOR.config.coreStyles_bold = { element: 'b', overrides: 'strong' };
    CKEDITOR.config.coreStyles_italic = { element: 'i', overrides: 'em' };
    CKEDITOR.config.coreStyles_strike = { element: 'strike', overrides: 's' };

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
        extraPlugins: 'symbol,alaToolbar',
        toolbar: [
            { name: 'basicstyles', items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript' ] },
            { name: 'clipboard', items: ['PasteText', '-', 'Undo', 'Redo' ] },
            { name: 'paragraph', items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent' ] },
            { name: 'links', items: [ 'Link', 'Unlink' ] },
            { name: 'insert', items: [ 'HorizontalRule', 'Symbol', 'Male', 'Female', 'PlusMinus', 'Times', 'Endash', 'Degree' ] },
            { name: 'colors', items: [ 'TextColor', 'BGColor' ] },
            { name: 'tools', items: [ 'Maximize', '-' ] },
            { name: 'styles', items: [ 'Styles', 'Format' ] }
        ]
    };
});

