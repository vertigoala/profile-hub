var profileEditor = angular.module('profileEditor', ['app.config', 'ngSanitize', 'ui.bootstrap', 'colorpicker.module', 'angular-loading-bar', 'duScroll', 'ngFileUpload', 'checklist-model', 'ngCkeditor', 'angular-inview', 'ngStorage', 'truncate']);

profileEditor.config(function ($logProvider) {
    $logProvider.debugEnabled(false);
});

profileEditor.config(['cfpLoadingBarProvider', function (cfpLoadingBarProvider) {
    cfpLoadingBarProvider.includeSpinner = false;
}]);

profileEditor.run(function ($rootScope, config) {
    $rootScope.config = config;

    CKEDITOR.plugins.addExternal('alaToolbar', config.contextPath + '/static/js/ckeditor/plugins/alaToolbar/');

    CKEDITOR.plugins.addExternal('ngImage', config.contextPath + '/static/js/ckeditor/plugins/ngImage/');

    // override the default ckeditor stylesheet with ours
    CKEDITOR.config.contentsCss = [config.mainCssFile, config.bootstrapCssFile];

    // use HTML4 elements for Jasper compatibility.
    CKEDITOR.config.coreStyles_bold = { element: 'b', overrides: 'strong' };
    CKEDITOR.config.coreStyles_italic = { element: 'i', overrides: 'em' };
    CKEDITOR.config.coreStyles_strike = { element: 'strike', overrides: 's' };

    // CKEditor uses 'Allowed Content Filtering' (ACF), which defines what html elements are allowed in the text, and
    // what attributes and classes those elements can have. The 'extraAllowedContent' config item lets you add extra
    // allowed elements on top of the defaults.
    // Any element, class or attribute not explicitly listed will be removed by the editor.
    // The format is tag(css classes comma-separated)[attributes].
    //
    // Image tags are not allowed unless the images ckeditor plugin in included, but we don't want to use that because
    // we need to use our own angular controllers/directives to manage images for the whole profile.
    CKEDITOR.config.extraAllowedContent = 'img(thumbnail,inline-attribute-image,small,medium,large,pull-left,pull-right)[src,class,alt]';

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
        extraPlugins: 'symbol,alaToolbar,ngImage',
        toolbar: [
            { name: 'basicstyles', items: [ 'Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript' ] },
            { name: 'clipboard', items: ['PasteText', '-', 'Undo', 'Redo' ] },
            { name: 'paragraph', items: [ 'NumberedList', 'BulletedList', '-', 'Outdent', 'Indent' ] },
            { name: 'links', items: [ 'Link', 'Unlink' ] },
            { name: 'image', items: [ 'ngImage' ] },
            { name: 'insert', items: [ 'HorizontalRule', 'Symbol', 'Male', 'Female', 'PlusMinus', 'Times', 'Endash', 'Degree' ] },
            { name: 'colors', items: [ 'TextColor', 'BGColor' ] },
            { name: 'tools', items: [ 'Maximize' ] },
            { name: 'styles', items: [ 'Styles', 'Format' ] }
        ]
    };
});

