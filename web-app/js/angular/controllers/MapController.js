/**
 * Map controller
 */
profileEditor.controller('MapController', function ($scope, profileService, util, config, messageService, $http) {
    var self = this;

    self.init = function (biocacheWMSUrl, biocacheInfoUrl) {
        self.loading = true;
        self.editingMap = false;

        self.biocacheInfoUrl = biocacheInfoUrl;
        self.biocacheWMSUrl = biocacheWMSUrl;

        self.opusId = util.getEntityId("opus");
        self.profileId = util.getEntityId("profile");

        var future = profileService.getProfile(self.opusId, self.profileId);

        future.then(function (data) {
                self.profile = data.profile;
                self.opus = data.opus;

                var occurrenceQuery = self.profile.occurrenceQuery;
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

                self.loading = false;
            },
            function () {
                messageService.alert("An error occurred while retrieving the map information.");
                self.loading = false;
            }
        );
    };

    self.showOccurrenceDetails = function (clickEvent) {
        var url = self.biocacheInfoUrl
            + self.profile.occurrenceQuery
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

    self.saveMapConfiguration = function () {
        var queryString = self.editableMap.getQueryString();

        if (queryString != self.profile.occurrenceQuery) {
            self.profile.occurrenceQuery = queryString;
            var promise = profileService.updateProfile(self.opusId, self.profileId, self.profile);
            promise.then(function () {
                messageService.info("Map configuration has been successfully updated.");

                self.toggleEditingMap();
            }, function () {
                messageService.alert("An error occurred while updating the map configuration.");
            });
        }
    };

    self.resetToDefaultMapConfig = function () {
        var confirm = util.confirm("This will remove all customisations from the map and return the configuration to the default for this collection. Are you sure you wish to proceed?");
        confirm.then(function () {
            // the default map config is just the q= portion of the url
            var query = self.profile.occurrenceQuery;

            query = query.replace(/q=(.*)fq=/g, "q=$1");

            self.editableMap.setQueryString(query);
        });
    };

    self.undoAllMapChanges = function () {
        var confirm = util.confirm("This will remove all customisations you have made since beginning to edit the map configuration. Are you sure you wish to proceed?");
        confirm.then(function () {
            self.editableMap.setQueryString(self.profile.occurrenceQuery);
        });
        $scope.MapForm.$setPristine();
    };

    self.toggleEditingMap = function () {
        if (self.editingMap) {
            self.editableMap.setQueryString(self.profile.occurrenceQuery);
            self.editingMap = false;
            $scope.MapForm.$setPristine();
        } else {
            self.editingMap = true;
            if (_.isUndefined(self.editableMap)) {
                createEditableMap();
            }
        }
    };

    self.redrawEditableMap = function() {
        if (self.editingMap && !_.isUndefined(self.editableMap)) {
            self.editableMap.map.redraw();
        }
    };

    function createEditableMap() {
        if (!config.readonly) {
            var occurrenceQuery = self.profile.occurrenceQuery;

            self.editableMap = new ALA.OccurrenceMap("editOccurrenceMap",
                self.opus.biocacheUrl,
                occurrenceQuery,
                {
                    mapOptions: {
                        zoomToObject: false,
                        zoom: self.opus.mapZoom,
                        center: [self.opus.mapDefaultLatitude, self.opus.mapDefaultLongitude]
                    },
                    point: {
                        colour: self.opus.mapPointColour,
                        mapAttribution: self.opus.mapAttribution
                    }
                }
            );

            self.editableMap.map.subscribe(function () {
                $scope.MapForm.$setDirty();
            });
        }
    }
});