modules = {
    profiles {
        dependsOn "jquery"
        dependsOn "angular"
        dependsOn "leaflet"
        resource url:"js/angular/Profiles.js"
        resource url:"js/angular/utils/Utils.js"
        resource url:"js/angular/services/ProfileService.js"
        resource url:"js/angular/services/MessageService.js"
        resource url:"js/angular/controllers/AttributesController.js"
        resource url:"js/angular/controllers/BhlLinksController.js"
        resource url:"js/angular/controllers/LinksController.js"
        resource url:"js/angular/controllers/ImagesController.js"
        resource url:"js/angular/controllers/ListsController.js"
        resource url:"js/angular/controllers/ClassificationController.js"
        resource url:"css/profiles.css"
    }

    angular {
        resource url:"https://ajax.googleapis.com/ajax/libs/angularjs/1.3.13/angular.min.js"
        resource url:"http://angular-ui.github.io/bootstrap/ui-bootstrap-tpls-0.12.0.js"
        resource url:"http://netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css"
    }

    leaflet {
        resource url:"http://leafletjs.com/dist/leaflet.js"
        resource url:"http://leafletjs.com/dist/leaflet.css"
    }
}