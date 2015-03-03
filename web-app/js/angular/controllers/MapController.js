/**
 * Map controller
 */
profileEditor.controller('MapController', function ($scope, profileService, util, messageService, $http, leafletData) {

    var mapBaseLayerAttribution = "Map data &copy; <a href=\"http://openstreetmap.org\">OpenStreetMap</a> contributors, <a href=\"http://creativecommons.org/licenses/by-sa/2.0/\">CC-BY-SA</a>, Imagery © <a href=\"http://mapbox.com\">Mapbox</a>";

    $scope.layers = {
        baselayers: {
            xyz: {
                name: 'Street',
                url: 'https://{s}.tiles.mapbox.com/v3/{id}/{z}/{x}/{y}.png',
                type: 'xyz',
                maxZoom: 18,
                layerParams: {
                    attribution: mapBaseLayerAttribution,
                    id: 'examples.map-i875mjb7'
                }
            }
        },
        overlays: {}
    };
    $scope.events = {
        map: {
            enable: ['click'],
            logic: 'emit'
        }
    };
    $scope.center = {};

    $scope.init = function (biocacheWMSUrl, biocacheInfoUrl) {
        $scope.biocacheInfoUrl = biocacheInfoUrl;
        $scope.biocacheWMSUrl = biocacheWMSUrl;

        messageService.info("Loading map...");
        var future = profileService.getProfile(util.getPathItem(util.LAST));

        future.then(function (data) {
                $scope.profile = data.profile;
                $scope.opus = data.opus;

                var occurrenceQuery = $scope.constructQuery();

                var wmsLayer = biocacheWMSUrl + occurrenceQuery;

                angular.extend($scope, {
                    center: {
                        lat: $scope.opus.mapDefaultLatitude,
                        lng: $scope.opus.mapDefaultLongitude,
                        zoom: $scope.opus.mapZoom
                    },
                    layers: {
                        overlays: {
                            wms: {
                                name: $scope.profile.scientificName,
                                url: wmsLayer,
                                type: "wms",
                                visible: true,
                                layerOptions: {
                                    layers: 'ALA:occurrences',
                                    format: 'image/png',
                                    transparent: true,
                                    attribution: $scope.opus.mapAttribution,
                                    id: "bla",
                                    bgcolor: "0x000000",
                                    outline: "true",
                                    ENV: "color:" + $scope.opus.mapPointColour + ";name:circle;size:4;opacity:1"
                                }
                            }
                        }
                    }
                });

            },
            function () {
                messageService.alert("An error occurred while retrieving the map information.");
            });
    };

    $scope.$on('leafletDirectiveMap.click', function(event, args){
        console.log(args.leafletEvent.latlng);
        var url = $scope.biocacheInfoUrl + "?"
            + $scope.constructQuery()
            + "&zoom=6"
            + "&lat=" + args.leafletEvent.latlng.lat
            + "&lon=" + args.leafletEvent.latlng.lng
            + "&radius=20&format=json"
            + "&callback=JSON_CALLBACK";

        var future = $http.jsonp(url);
        future.success(function (response) {
            leafletData.getMap().then(function(map) {
                L.popup()
                    .setLatLng(args.leafletEvent.latlng)
                    .setContent("Occurrences at this point: " + response.count)
                    .openOn(map);
            });
        });
        future.error(function () {
                messageService.alert("Unable to find occurrences for the specified location.");
            }
        );
    });

    $scope.constructQuery = function () {
        var result = "";
        if ($scope.profile && $scope.opus) {
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

            result = encodeURIComponent(occurrenceQuery);
        }

        return result;
    };

});