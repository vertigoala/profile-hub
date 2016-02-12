/**
 * Map controller
 */
profileEditor.controller('MapController', function ($scope, profileService, util, config, messageService, $http) {
    var self = this;

    self.init = function (biocacheWMSUrl, biocacheInfoUrl) {
        self.biocacheInfoUrl = biocacheInfoUrl;
        self.biocacheWMSUrl = biocacheWMSUrl;

        self.opusId = util.getEntityId("opus");
        self.profileId = util.getEntityId("profile");

        var future = profileService.getProfile(self.opusId, self.profileId);

        future.then(function (data) {
                self.profile = data.profile;
                self.opus = data.opus;

                var occurrenceQuery = self.constructQuery();

                var wmsLayer = biocacheWMSUrl + occurrenceQuery;

                self.map = new ALA.Map("occurrenceMap", {
                    drawControl: false,
                    singleMarker: false,
                    useMyLocation: false,
                    allowSearchLocationByAddress: false,
                    allowSearchRegionByAddress: false,
                    draggableMarkers: false,
                    showReset: false,
                    zoom: self.opus.mapZoom,
                    center: [self.opus.mapDefaultLatitude, self.opus.mapDefaultLongitude]
                });

                var layer = L.tileLayer.smartWms(wmsLayer, {
                    layers: 'ALA:occurrences',
                    format: 'image/png',
                    attribution: self.opus.mapAttribution,
                    outline: "true",
                    ENV: "color:" + self.opus.mapPointColour + ";name:circle;size:4;opacity:1"
                });
                layer.setZIndex(99);

                self.map.addLayer(layer, {});

                self.map.registerListener("click", self.showOccurrenceDetails);
            },
            function () {
                messageService.alert("An error occurred while retrieving the map information.");
            }
        );
    };

    self.showOccurrenceDetails = function (clickEvent) {
        var url = self.biocacheInfoUrl
            + self.constructQuery()
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
                .openOn(self.map.getMapImpl());
        });
        future.error(function () {
            messageService.alert("Unable to find occurrences for the specified location.");
        });
    };

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

            angular.forEach(self.opus.excludeRanksFromMap, function (rank, index) {
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