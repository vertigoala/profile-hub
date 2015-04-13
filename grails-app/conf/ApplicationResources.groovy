modules = {
    profiles {
        resource url:"css/profiles.css"
        dependsOn "jquery"
        dependsOn "bootstrap_fileupload"
        dependsOn "angular"
        dependsOn "leaflet"
        dependsOn "angular_bootstrap_colourpicker"
        dependsOn "angular_loading_bar"
        dependsOn "fontawesome"
        dependsOn "richText"
        resource url:"js/angular/profiles.js"
        resource url:"js/angular/utils/Filters.js"
        resource url:"js/angular/utils/Utils.js"
        resource url:"js/angular/services/ProfileService.js"
        resource url:"js/angular/services/MessageService.js"
        resource url:"js/angular/controllers/OpusController.js"
        resource url:"js/angular/controllers/ProfileController.js"
        resource url:"js/angular/controllers/UserAccessController.js"
        resource url:"js/angular/controllers/SearchController.js"
        resource url:"js/angular/controllers/AttributesController.js"
        resource url:"js/angular/controllers/BhlLinksController.js"
        resource url:"js/angular/controllers/LinksController.js"
        resource url:"js/angular/controllers/ImagesController.js"
        resource url:"js/angular/controllers/ListsController.js"
        resource url:"js/angular/controllers/TaxonController.js"
        resource url:"js/angular/controllers/MapController.js"
        resource url:"js/angular/controllers/VocabController.js"
        resource url:"js/angular/controllers/PublicationController.js"
        resource url:"js/angular/controllers/GlossaryController.js"
    }

    angular {
        resource url:"thirdparty/angular/angular-1.3.13.min.js"
        resource url:"thirdparty/angular/ui-bootstrap-tpls-0.12.0.js"
        resource url:"thirdparty/bootstrap/css/bootstrap-3.1.1.min.css"
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

}