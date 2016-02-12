// Karma configuration
// Generated on Mon Feb 23 2015 15:47:55 GMT+1100 (AEDT)

module.exports = function (config) {
    config.set({

        // base path that will be used to resolve all patterns (eg. files, exclude)
        basePath: '',

        plugins: [
            'karma-chrome-launcher',
            'karma-jasmine',
            'karma-ng-html2js-preprocessor',
            'karma-coverage',
            'karma-firefox-launcher',
            'karma-phantomjs-launcher'
        ],


        // frameworks to use
        // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
        frameworks: ['jasmine'],

        // preprocess matching files before serving them to the browser
        // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
        preprocessors: {
            'web-app/js/angular/**/*.js': ['coverage'],
            'web-app/templates/*.html': ['ng-html2js']
        },

        ngHtml2JsPreprocessor: {
            cacheIdFromPath: function(filepath) {
                // The URL defined in the template is /static/templates/ (because grails deploys anything under web-app to /static).
                // This function rewrites the requested URL to the actual relative path to the template files
                return filepath.replace(/web\-app\/templates\/(.*)\.html/, "/static/templates/$1.html");
            },

            // setting this option will create only a single module that contains templates
            // from all the files, so you can load them all with module('foo')
            moduleName: 'templates'
        },

        // list of files / patterns to load in the browser
        files: [
            'https://rawgit.com/AtlasOfLivingAustralia/ala-map-plugin/master/web-app/vendor/jquery-2.1.4/jquery-2.1.4.min.js',
            'https://rawgit.com/AtlasOfLivingAustralia/ala-map-plugin/master/web-app/vendor/leaflet-0.7.7/leaflet.js',
            'https://rawgit.com/AtlasOfLivingAustralia/ala-map-plugin/master/web-app/vendor/Leaflet.Coordinates-0.1.5/Leaflet.Coordinates-0.1.5.min.js',
            'https://rawgit.com/AtlasOfLivingAustralia/ala-map-plugin/master/web-app/vendor/Leaflet.loading-0.1.16/Control.Loading.js',
            'https://rawgit.com/AtlasOfLivingAustralia/ala-map-plugin/master/web-app/js/layers/SmartWmsLayer.js',
            'https://rawgit.com/AtlasOfLivingAustralia/ala-map-plugin/master/web-app/js/Map.js',
            'web-app/thirdparty/modernizr/modernizer.js',
            'web-app/thirdparty/angular/angular-1.3.13.min.js',
            'test/js/thirdparty/angular-mocks-1.3.13.js',
            'web-app/thirdparty/angular/ui-bootstrap-tpls-0.12.0.js',
            'web-app/thirdparty/angular-bootstrap-colorpicker-3.0.11/js/bootstrap-colorpicker-module.min.js',
            'web-app/thirdparty/ng-file-upload/ng-file-upload-shim-5.0.7.min.js',
            'web-app/thirdparty/ng-file-upload/ng-file-upload-5.0.7.min.js',
            'web-app/thirdparty/angular-leaflet/angular-leaflet-directive.min.js',
            'web-app/thirdparty/angular-loading-bar/loading-bar-0.7.1.min.js',
            'web-app/thirdparty/ckeditor/ng-ckeditor-0.2.1.min.js',
            'web-app/thirdparty/ckeditor/ckeditor.js',
            'web-app/thirdparty/google-diff-match-patch/diff_match_patch.js',
            'web-app/thirdparty/angular-scroll/angular-scroll.min.js',
            'web-app/thirdparty/checklist-model/checklist-model-0.2.4.js',
            'web-app/thirdparty/angular-truncate/truncate.js',
            'web-app/thirdparty/angular-inview/angular-inview-1.5.6.js',
            'web-app/thirdparty/ngStorage/ngStorage-0.3.10.min.js',
            'web-app/thirdparty/underscore/underscore-1.8.3.min.js',
            'web-app/js/angular/profiles.js',
            'web-app/js/angular/utils/*.js',
            'web-app/js/angular/services/*.js',
            'web-app/js/angular/controllers/*.js',
            'web-app/templates/*.html',
            'web-app/js/angular/directives/*.js',
            'test/js/specs/MockConfigModule.js',
            'test/js/specs/**/*.js'
        ],


        // list of files to exclude
        exclude: [],


        // test results reporter to use
        // possible values: 'dots', 'progress'
        // available reporters: https://npmjs.org/browse/keyword/karma-reporter
        reporters: ['progress', 'coverage'],


        // web server port
        port: 9876,


        // enable / disable colors in the output (reporters and logs)
        colors: true,


        // level of logging
        // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
        logLevel: config.LOG_DEBUG,


        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: true,


        // start these browsers
        // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
        browsers: ['Chrome','Firefox','PhantomJS'],


        // Continuous Integration mode
        // if true, Karma captures browsers, runs the tests and exits
        singleRun: true
    });
};
