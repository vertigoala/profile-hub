var profiles = {
    urls: {
        biocacheOccurrenceSearchUrl: null,
        biocacheOccurrenceRecordUrl: null,
        biocacheOccurrenceInfoUrl: null,
        biocacheWMSUrl: null
    },

    maps: {
        mapBaseLayerAttribution: "Map data &copy; <a href=\"http://openstreetmap.org\">OpenStreetMap</a> contributors, <a href=\"http://creativecommons.org/licenses/by-sa/2.0/\">CC-BY-SA</a>, Imagery © <a href=\"http://mapbox.com\">Mapbox</a>"
    },

    init: function (options) {
        profiles.urls = options.urls;
        profiles.urls.biocacheSearchUrl = options.urls.biocacheBaseUrl + options.urls.biocacheSearchPath;
        profiles.urls.biocacheRecordUrl = options.urls.biocacheBaseUrl + options.urls.biocacheRecordPath;
        profiles.urls.biocacheInfoUrl = options.urls.biocacheBaseUrl + options.urls.biocacheInfoPath;
        profiles.urls.biocacheWMSUrl = options.urls.biocacheBaseUrl + options.urls.biocacheWmsPath;
    },


    addTaxonMap: function (opus, profile, occurrenceQuery) {
        opus = $.parseJSON(opus)
        profile = $.parseJSON(profile)

        var wmsLayer = profiles.urls.biocacheWMSUrl + occurrenceQuery;

        var speciesLayer = L.tileLayer.wms(wmsLayer, {
            layers: 'ALA:occurrences',
            format: 'image/png',
            transparent: true,
            attribution: opus.mapAttribution,
            bgcolor: "0x000000",
            outline: "true",
            ENV: "color:" + opus.mapPointColour + ";name:circle;size:4;opacity:1"
        });

        var speciesLayers = new L.LayerGroup();
        speciesLayer.addTo(speciesLayers);

        var map = L.map('map', {
            center: [opus.mapDefaultLatitude, opus.mapDefaultLongitude],
            zoom: opus.mapZoom,
            layers: [speciesLayers]
        });

        var streetView = L.tileLayer(opus.mapBaseLayer, {
            maxZoom: 18,
            attribution: profiles.maps.mapBaseLayerAttribution,
            id: 'examples.map-i875mjb7'
        }).addTo(map);

        var baseLayers = {
            "Street": streetView
        };

        var layerTitle = profile.scientificName;

        var overlays = {};
        overlays[layerTitle] = speciesLayer;

        L.control.layers(baseLayers, overlays).addTo(map);

        map.on('click', function (event) {
            profiles.onMapClick(event, occurrenceQuery)
        });
    },

    onMapClick: function (e, occurrenceQuery) {
        console.log(profiles.urls.biocacheInfoUrl + occurrenceQuery)
        $.ajax({
            url: profiles.urls.biocacheInfoUrl,
            jsonp: "callback",
            dataType: "jsonp",
            data: {
                q: occurrenceQuery,
                zoom: "6",
                lat: e.latlng.lat,
                lon: e.latlng.lng,
                radius: 20,
                format: "json"
            },
            success: function (response) {
                var popup = L.popup()
                    .setLatLng(e.latlng)
                    .setContent("<h3>Test</h3>Occurrences at this point: " + response.count)
                    .openOn(map);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("Error completing images - JSON - " + errorThrown + ", status = " + jqXHR.text);
            }
        })
    }
};


var profileEditor = angular.module('profileEditor', ['ui.bootstrap']);

profileEditor.config(function ($locationProvider) {
    // This disables 'hashbang' mode and removes the need to specify <base href="/my-base"> in the views.
    // This makes AngularJS take control of all links on the page: if you do not want Angular to control a particular
    // link, add target="_self".
    $locationProvider.html5Mode({enabled: true, requireBase: false});
});