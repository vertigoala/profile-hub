modules = {
    profiles {
        resource url:"css/nsl.css"
        resource url:"css/theme.css"
        resource url:"css/profiles.css"
        resource url:"css/colour.css"
        resource url:"css/tree.css"
        resource url:"css/sidebar.css"
        dependsOn "jquery"
        dependsOn "bootstrap3"
        dependsOn "angular"
        dependsOn "angular_bootstrap_colourpicker"
        dependsOn "angular_loading_bar"
        dependsOn "fontawesome"
        dependsOn "ckeditor"
        dependsOn "google_diff"
        dependsOn "jspath"
        dependsOn "angular_scroll"
        dependsOn "ng_file_upload"
        dependsOn "checklist_model"
        dependsOn "angular_inview"
        dependsOn "ng_storage"
        dependsOn "angular_truncate"
        dependsOn "underscore"
        dependsOn "modernizr3"
        dependsOn "keybase"
        dependsOn "keybase_overrides"
        resource url:"js/angular/profiles.js"
        resource url:"js/angular/utils/Filters.js"
        resource url:"js/angular/utils/Utils.js"
        resource url:"js/angular/directives/Misc.js"
        resource url:"js/angular/directives/keepalive.js"
        resource url:"js/angular/directives/keyplayer.js"
        resource url:"js/angular/directives/profileName.js"
        resource url:"js/angular/directives/vocabularyEditor.js"
        resource url:"js/angular/directives/profileComparison.js"
        resource url:"js/angular/directives/nomenclature.js"
        resource url:"js/angular/directives/publication.js"
        resource url:"js/angular/directives/link.js"
        resource url:"js/angular/directives/saveButton.js"
        resource url:"js/angular/directives/saveAll.js"
        resource url:"js/angular/directives/taxonomy.js"
        resource url:"js/angular/directives/profileSideBar.js"
        resource url:"js/angular/directives/fallbackDatePicker.js"
        resource url:"js/angular/directives/delegatedSearch.js"
        resource url:"js/angular/directives/missingImage.js"
        resource url:"js/angular/directives/markupText.js"
        resource url:"js/angular/directives/imageUpload.js"
        resource url:"js/angular/directives/closeModal.js"
        resource url:"js/angular/services/ProfileService.js"
        resource url:"js/angular/services/MessageService.js"
        resource url:"js/angular/services/NavService.js"
        resource url:"js/angular/services/ProfileComparisonService.js"
        resource url:"js/angular/controllers/ImageUploadController.js"
        resource url:"js/angular/controllers/CreateProfileController.js"
        resource url:"js/angular/controllers/AttachmentController.js"
        resource url:"js/angular/controllers/OpusController.js"
        resource url:"js/angular/controllers/ProfileController.js"
        resource url:"js/angular/controllers/UserAccessController.js"
        resource url:"js/angular/controllers/BrowseController.js"
        resource url:"js/angular/controllers/SearchController.js"
        resource url:"js/angular/controllers/AttributeImageController.js"
        resource url:"js/angular/controllers/AttributesController.js"
        resource url:"js/angular/controllers/BhlLinksController.js"
        resource url:"js/angular/controllers/LinksController.js"
        resource url:"js/angular/controllers/SpecimenController.js"
        resource url:"js/angular/controllers/ImagesController.js"
        resource url:"js/angular/controllers/ListsController.js"
        resource url:"js/angular/controllers/MapController.js"
        resource url:"js/angular/controllers/PublicationController.js"
        resource url:"js/angular/controllers/GlossaryController.js"
        resource url:"js/angular/controllers/CommentController.js"
        resource url:"js/angular/controllers/ExportController.js"
        resource url:"js/angular/controllers/AboutController.js"
        resource url:"js/angular/controllers/ReportController.js"
        resource url:"js/angular/controllers/StatisticsController.js"
        resource url:"js/angular/controllers/AlertController.js"
        resource url:"js/angular/controllers/DoiController.js"
        resource url:"js/angular/controllers/ShareRequestController.js"
        resource url:"js/angular/controllers/UserDetailsController.js"
        resource url:"js/angular/controllers/AdminController.js"
    }

    angular {
        resource url:"thirdparty/angular/angular-1.3.13.min.js"
        resource url:"thirdparty/angular/angular-sanitize-1.3.13.min.js"
        resource url:"thirdparty/angular/ui-bootstrap-tpls-0.12.0.js"
        dependsOn 'bootstrap3'
    }

    bootstrap3 {
        resource url:"thirdparty/bootstrap/css/bootstrap3.3.4.min.css"
        resource url:"thirdparty/bootstrap/js/bootstrap.min.js"
    }

    angular_bootstrap_colourpicker {
        resource url:"thirdparty/angular-bootstrap-colorpicker-3.0.11/js/bootstrap-colorpicker-module.min.js"
        resource url:"thirdparty/angular-bootstrap-colorpicker-3.0.11/css/colorpicker.min.css"
    }

    angular_loading_bar {
        resource url:"thirdparty/angular-loading-bar/loading-bar-0.7.1.min.js"
        resource url:"thirdparty/angular-loading-bar/loading-bar-0.7.1.min.css"
    }

    fontawesome {
        resource url:"thirdparty/font-awesome-4.3.0/css/font-awesome.min.css"
    }

    bootstrap_fileupload {
        resource url:"thirdparty/bootstrap-fileupload/bootstrap-fileupload.min.js"
        resource url:"thirdparty/bootstrap-fileupload/bootstrap-fileupload.min.css"
    }

    google_diff {
        resource url:"thirdparty/google-diff-match-patch/diff_match_patch.js"
    }

    jspath {
        resource url:"thirdparty/jspath/jspath.min.js"
    }

    angular_scroll {
        resource url:"thirdparty/angular-scroll/angular-scroll.min.js"
    }

    ng_file_upload {
        resource url:"thirdparty/ng-file-upload/ng-file-upload-shim-5.0.7.min.js"
        resource url:"thirdparty/ng-file-upload/ng-file-upload-5.0.7.min.js"
    }

    checklist_model {
        resource url:"thirdparty/checklist-model/checklist-model-0.2.4.js"
    }

    ckeditor {
        defaultBundle false

        resource url:"thirdparty/ckeditor/ckeditor.js", disposition: 'head', exclude: 'hashandcache'
        resource url:"thirdparty/ckeditor/ng-ckeditor-0.2.1.css"
        resource url:"thirdparty/ckeditor/ng-ckeditor-0.2.1.min.js"
    }

    angular_inview {
        resource url:"thirdparty/angular-inview/angular-inview-1.5.6.js"
    }

    ng_storage {
        resource url:"thirdparty/ngStorage/ngStorage-0.3.10.min.js"
    }

    angular_truncate {
        resource url:"thirdparty/angular-truncate/truncate.js"
    }

    underscore {
        resource url:"thirdparty/underscore/underscore-1.8.3.min.js"
    }

    modernizr3 {
        resource url:"thirdparty/modernizr/modernizer.js", disposition:'head'
    }

    keybase {
        resource url:"thirdparty/ala-keyplayer/jquery.keybase.key.js"
        resource url:"thirdparty/ala-keyplayer/keybase.player.css"
    }

    keybase_overrides {
        dependsOn "keybase"
        resource url:"css/keybase-override.css"
    }

    // not using the resource that comes with the plugin as it causes issues with the bootstrap styles being used
    images_plugin {
        dependsOn 'jquery', 'leaflet', 'leaflet-draw', 'leaflet-loading', 'font-awesome'
        resource url: [plugin: "images-client-plugin", dir: 'js', file: 'ala-image-viewer.js']
        resource url: [plugin: "images-client-plugin", dir: 'js/img-gallery/lib/slider-pro/css', file: 'slider-pro.css']
        resource url: [plugin: "images-client-plugin", dir: 'js/img-gallery/css', file: 'img-gallery.css']

        resource url: [plugin: "images-client-plugin", dir: 'js/img-gallery/lib/slider-pro/js', file: 'jquery.sliderPro.custom.js']
        resource url: [plugin: "images-client-plugin", dir: 'js/img-gallery/js', file: 'img-gallery.js']
    }
}
