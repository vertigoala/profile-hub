modules = {
    profiles {
        resource url:"css/profiles.css"
        dependsOn "jquery"
        dependsOn "angular"
        dependsOn "leaflet"
        resource url:"js/angular/Profiles.js"
        resource url:"js/angular/utils/Utils.js"
        resource url:"js/angular/utils/Filters.js"
        resource url:"js/angular/services/ProfileService.js"
        resource url:"js/angular/services/MessageService.js"
        resource url:"js/angular/controllers/OpusController.js"
        resource url:"js/angular/controllers/UserAccessController.js"
        resource url:"js/angular/controllers/SearchController.js"
        resource url:"js/angular/controllers/AttributesController.js"
        resource url:"js/angular/controllers/BhlLinksController.js"
        resource url:"js/angular/controllers/LinksController.js"
        resource url:"js/angular/controllers/ImagesController.js"
        resource url:"js/angular/controllers/ListsController.js"
        resource url:"js/angular/controllers/TaxonController.js"
        resource url:"js/angular/controllers/MapController.js"
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
}