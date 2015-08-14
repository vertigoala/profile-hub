/**
 * Map controller
 */
profileEditor.controller('MapController', function ($scope, profileService, util, config, messageService, $http, leafletData) {
    var self = this;

    var mapBaseLayerAttribution = "Map data &copy; <a href=\"http://openstreetmap.org\">OpenStreetMap</a> contributors, <a href=\"http://creativecommons.org/licenses/by-sa/2.0/\">CC-BY-SA</a>, Imagery © <a href=\"http://mapbox.com\">Mapbox</a>";

    self.layers = {
        baselayers: {
            xyz: {
                name: 'Street',
                url: 'https://{s}.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token={token}',
                type: 'xyz',
                maxZoom: 18,
                layerParams: {
                    attribution: mapBaseLayerAttribution,
                    id: config.map.mapId,
                    token: config.map.accessKey
                }
            }
        },
        overlays: {}
    };
    self.events = {
        map: {
            enable: ['click'],
            logic: 'emit'
        }
    };
    self.center = {};

    self.init = function (biocacheWMSUrl, biocacheInfoUrl) {
        self.biocacheInfoUrl = biocacheInfoUrl;
        self.biocacheWMSUrl = biocacheWMSUrl;

        self.opusId = util.getEntityId("opus");
        self.profileId = util.getEntityId("profile");

        messageService.info("Loading map...");
        var future = profileService.getProfile(self.opusId, self.profileId);

        future.then(function (data) {
                self.profile = data.profile;
                self.opus = data.opus;

                var occurrenceQuery = self.constructQuery();

                var wmsLayer = biocacheWMSUrl + occurrenceQuery;

                angular.extend(self, {
                    center: {
                        lat: self.opus.mapDefaultLatitude,
                        lng: self.opus.mapDefaultLongitude,
                        zoom: self.opus.mapZoom
                    },
                    layers: {
                        overlays: {
                            wms: {
                                name: self.profile.scientificName,
                                url: wmsLayer,
                                type: "wms",
                                visible: true,
                                layerOptions: {
                                    layers: 'ALA:occurrences',
                                    format: 'image/png',
                                    transparent: true,
                                    attribution: self.opus.mapAttribution,
                                    id: "bla",
                                    bgcolor: "0x000000",
                                    outline: "true",
                                    ENV: "color:" + self.opus.mapPointColour + ";name:circle;size:4;opacity:1"
                                }
                            }
                        }
                    }
                });

            },
            function () {
                messageService.alert("An error occurred while retrieving the map information.");
            }
        );
    };

    $scope.$on('leafletDirectiveMap.click', function(event, args){
        var url = self.biocacheInfoUrl
            + self.constructQuery()
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

    self.constructQuery = function () {
        var result = "";
        if (self.profile && self.opus) {
            var query = constructExcludedRankList() + "q=";
            if (self.profile.guid && self.profile.guid != "null") {
                query = query + encodeURIComponent("lsid:" + self.profile.guid);
            } else {
                query = query + encodeURIComponent(self.profile.scientificName);
            }

            var occurrenceQuery = query;

            if (self.opus.recordSources) {
                occurrenceQuery = query + encodeURIComponent(" AND (data_resource_uid:" + self.opus.recordSources.join(" OR data_resource_uid:") + ")")
            }

            result = occurrenceQuery;
        }

        return result;
    };

    function constructExcludedRankList() {
        var exclusionList = "";

        if (self.opus.excludeRanksFromMap && self.opus.excludeRanksFromMap.length > 0) {
            exclusionList = "fq=-(";

            angular.forEach(self.opus.excludeRanksFromMap, function(rank, index) {
                if (index > 0) {
                    exclusionList += " OR ";
                }
                exclusionList += "rank:" + rank;
            });

            exclusionList += ")&"
        }

        return exclusionList;
    }

});