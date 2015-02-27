modules = {
    profiles {
        dependsOn "jquery"
        dependsOn "angular"
        dependsOn "leaflet"
        resource url:"js/angular/Profiles.js"
        resource url:"js/angular/utils/Utils.js"
        resource url:"js/angular/utils/Filters.js"
        resource url:"js/angular/services/ProfileService.js"
        resource url:"js/angular/services/MessageService.js"
        resource url:"js/angular/controllers/AttributesController.js"
        resource url:"js/angular/controllers/BhlLinksController.js"
        resource url:"js/angular/controllers/LinksController.js"
        resource url:"js/angular/controllers/ImagesController.js"
        resource url:"js/angular/controllers/ListsController.js"
        resource url:"js/angular/controllers/TaxonController.js"
        resource url:"js/angular/controllers/MapController.js"
        resource url:"css/profiles.css"
    }

    angular {
        resource url:"js/thirdparty/angular-1.3.13.min.js"
        resource url:"js/thirdparty/ui-bootstrap-tpls-0.12.0.js"
        resource url:"css/bootstrap-3.1.1.min.css"
    }

    leaflet {
        resource url:"js/thirdparty/leaflet-0.7.2.js"
        resource url:"css/leaflet.css"
    }
}