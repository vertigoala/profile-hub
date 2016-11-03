// requires that application.js is loaded beforehand

// maps
// dependsOn underscore, jquery, font-awesome, uri
//= require onImpressions/jquery.onimpression.js
//= require jquery.inview-1.1.2/jquery.inview.min.js
// Leaflet must be under the path 'leaflet' because leaflet-control-geocoder use require('leaflet') which gets
// interpreted by the asset pipeline as a sort of inline //= require, which means it will load the version from
// the image plugin unless overridden
//= require leaflet
//= require Leaflet.draw-0.2.4/leaflet.draw.js
// TODO Add src.js to plugin
//= require Leaflet.Coordinates-0.1.5/Leaflet.Coordinates-0.1.5.src.js
//= require Leaflet.EasyButton-1.2.0/easy-button.js
// The Geocoder control would go here but it doesn't play nicely with the asset-pipeline and we don't need it in this app.
//= require leaflet-control-geocoder-1.5.1/Control.Geocoder.js
//= require Leaflet.markercluster-0.4.0-hotfix.1/leaflet.markercluster.js
//= require Leaflet.loading-0.1.16/Control.Loading.js
//= require Leaflet.Sleep/Leaflet.Sleep.js
//= require turf-2.0.2/turf.min.js
//= require handlebars-4.0.5/handlebars.js
//= require Map.js
//= require OccurrenceMap.js
//= require layers/SmartWmsLayer.js
//= require controls/Checkbox.js
//= require controls/Slider.js
//= require controls/TwoStepSelector.js
//= require controls/Select.js
//= require controls/Legend.js
//= require controls/Radio.js

// images
// prerequisites are jquery, leaflet, leaflet-draw, leafleft-loading, font-awesome
//= require ala-image-viewer.js
//= require img-gallery/lib/slider-pro/js/jquery.sliderPro.custom.js
//= require img-gallery/js/img-gallery.js
