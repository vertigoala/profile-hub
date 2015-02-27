/**
 * Map controller
 */
profileEditor.controller('MapController', function ($scope, profileService, util, messageService, $http) {

    var mapBaseLayerAttribution = "Map data &copy; <a href=\"http://openstreetmap.org\">OpenStreetMap</a> contributors, <a href=\"http://creativecommons.org/licenses/by-sa/2.0/\">CC-BY-SA</a>, Imagery © <a href=\"http://mapbox.com\">Mapbox</a>";

    $scope.init = function (biocacheWMSUrl, biocacheInfoUrl) {
        $scope.biocacheInfoUrl = biocacheInfoUrl;
        $scope.biocacheWMSUrl = biocacheWMSUrl;

        messageService.info("Loading map..");
        var future = profileService.getProfile(util.getPathItem(util.LAST));

        future.then(function (data) {
                $scope.profile = data.profile;
                $scope.opus = data.opus;

                var occurrenceQuery = $scope.constructQuery();

                var wmsLayer = biocacheWMSUrl + occurrenceQuery;
                var speciesLayer = L.tileLayer.wms(wmsLayer, {
                    layers: 'ALA:occurrences',
                    format: 'image/png',
                    transparent: true,
                    attribution: $scope.opus.mapAttribution,
                    bgcolor: "0x000000",
                    outline: "true",
                    ENV: "color:" + $scope.opus.mapPointColour + ";name:circle;size:4;opacity:1"
                });
                var speciesLayers = new L.LayerGroup();

                speciesLayer.addTo(speciesLayers);
                $scope.map = L.map('map', {
                    center: [$scope.opus.mapDefaultLatitude, $scope.opus.mapDefaultLongitude],
                    zoom: $scope.opus.mapZoom,
                    layers: [speciesLayers]
                });

                var streetView = L.tileLayer($scope.opus.mapBaseLayer, {
                    maxZoom: 18,
                    attribution: mapBaseLayerAttribution,
                    id: 'examples.map-i875mjb7'
                }).addTo($scope.map);

                var baseLayers = {
                    "Street": streetView
                };

                var layerTitle = $scope.profile.scientificName;

                var overlays = {};
                overlays[layerTitle] = speciesLayer;

                L.control.layers(baseLayers, overlays).addTo($scope.map);

                $scope.map.on('click', function (event) {
                    $scope.onMapClick(event, occurrenceQuery)
                });
            },
            function () {
                messageService.alert("An error occurred while retrieving the map information.");
            }
        );
    };

    $scope.constructQuery = function () {
        var query;
        if ($scope.profile.guid && $scope.profile.guid != "null") {
            query = "lsid:" + $scope.profile.guid;
        } else {
            query = $scope.profile.scientificName;
        }

        var occurrenceQuery = query;

        if ($scope.opus.recordSources) {
            occurrenceQuery = query + " AND (data_resource_uid:" + $scope.opus.recordSources.join(" OR data_resource_uid:") + ")"
        }

        return encodeURIComponent(occurrenceQuery);
    };

    $scope.onMapClick = function (clickEvent, occurrenceQuery) {
        var url = $scope.biocacheInfoUrl + "?"
            + occurrenceQuery
            + "&zoom=6"
            + "&lat=" + clickEvent.latlng.lat
            + "&lon=" + clickEvent.latlng.lng
            + "&radius=20&format=json"
            + "&callback=JSON_CALLBACK";

        var future = $http.jsonp(url);
        future.success(function (response) {
            L.popup()
                .setLatLng(clickEvent.latlng)
                .setContent("Occurrences at this point: " + response.count)
                .openOn($scope.map);
        });
        future.error(function () {
                messageService.alert("Unable to find occurrences for the specified location.");
            }
        );
    };
});