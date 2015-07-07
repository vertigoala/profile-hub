modules = {
    profiles {
        resource url:"css/nsl.css"
        resource url:"css/theme.css"
        resource url:"css/profiles.css"
        resource url:"css/colour.css"
        dependsOn "jquery"
        dependsOn "bootstrap3"
        dependsOn "angular"
        dependsOn "leaflet"
        dependsOn "angular_bootstrap_colourpicker"
        dependsOn "angular_loading_bar"
        dependsOn "fontawesome"
        dependsOn "richText"
        dependsOn "google_diff"
        dependsOn "jspath"
        dependsOn "angular_scroll"
        dependsOn "ng_file_upload"
        dependsOn "checklist_model"
        resource url:"js/angular/profiles.js"
        resource url:"js/angular/utils/Filters.js"
        resource url:"js/angular/utils/Utils.js"
        resource url:"js/angular/directives/keyplayer.js"
        resource url:"js/angular/directives/profileName.js"
        resource url:"js/angular/directives/vocabularyEditor.js"
        resource url:"js/angular/directives/profileComparison.js"
        resource url:"js/angular/services/ProfileService.js"
        resource url:"js/angular/services/MessageService.js"
        resource url:"js/angular/services/NavService.js"
        resource url:"js/angular/controllers/CreateProfileController.js"
        resource url:"js/angular/controllers/OpusController.js"
        resource url:"js/angular/controllers/ProfileController.js"
        resource url:"js/angular/controllers/UserAccessController.js"
        resource url:"js/angular/controllers/SearchController.js"
        resource url:"js/angular/controllers/AttributesController.js"
        resource url:"js/angular/controllers/BhlLinksController.js"
        resource url:"js/angular/controllers/LinksController.js"
        resource url:"js/angular/controllers/SpecimenController.js"
        resource url:"js/angular/controllers/ImagesController.js"
        resource url:"js/angular/controllers/ListsController.js"
        resource url:"js/angular/controllers/TaxonController.js"
        resource url:"js/angular/controllers/MapController.js"
        resource url:"js/angular/controllers/PublicationController.js"
        resource url:"js/angular/controllers/GlossaryController.js"
        resource url:"js/angular/controllers/CommentController.js"
        resource url:"js/angular/controllers/ExportController.js"
        resource url:"js/angular/controllers/AboutController.js"
        resource url:"js/angular/controllers/ReportController.js"
    }

    angular {
        resource url:"thirdparty/angular/angular-1.3.13.min.js"
        resource url:"thirdparty/angular/ui-bootstrap-tpls-0.12.0.js"
        dependsOn 'bootstrap3'
    }

    bootstrap3 {
        resource url:"thirdparty/bootstrap/css/bootstrap3.3.4.min.css"
        resource url:"thirdparty/bootstrap/js/bootstrap.min.js"
    }

    leaflet {
        resource url:"thirdparty/leaflet/leaflet-0.7.3.js"
        resource url:"thirdparty/angular-leaflet/angular-leaflet-directive.min.js"
        resource url:"thirdparty/leaflet/leaflet.css"
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

    richText {
        resource url:"thirdparty/textAngular/textAngular-1.3.11.min.js"
        resource url:"thirdparty/textAngular/textAngular-1.3.11.css"
        resource url:"thirdparty/textAngular/textAngular-rangy-1.3.11.min.js"
        resource url:"thirdparty/textAngular/textAngular-sanitize-1.3.11.min.js"
        dependsOn "fontawesome"
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
}