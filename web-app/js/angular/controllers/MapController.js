/**
 * Map controller
 */
profileEditor.controller('MapController', function ($scope, profileService, util, config, messageService, $http) {
    var self = this;

    var biocacheWMSUrl = config.biocacheServiceUrl + "ws/mapping/wms/reflect?";
    var biocacheInfoUrl = config.biocacheServiceUrl + "ws/occurrences/info?";
    var biocacheBoundsUrl = config.biocacheServiceUrl + "ws/mapping/bounds.json?";

    self.autoZoom = false;
    self.showingEditorView = true;

    self.init = function () {

        self.loading = true;
        self.editingMap = false;

        self.opusId = util.getEntityId("opus");
        self.profileId = util.getEntityId("profile");

        var future = profileService.getProfile(self.opusId, self.profileId);

        self.wmsLayer = null;

        future.then(function (data) {
                self.profile = data.profile;
                self.opus = data.opus;
                self.autoZoom = self.opus.mapConfig.autoZoom;

                self.map = new ALA.Map("occurrenceMap", {
                    zoomToObject: self.autoZoom,
                    maxAutoZoom: self.opus.mapConfig.maxAutoZoom,
                    drawControl: false,
                    singleMarker: false,
                    useMyLocation: false,
                    showFitBoundsToggle: true,
                    allowSearchLocationByAddress: false,
                    allowSearchRegionByAddress: false,
                    draggableMarkers: false,
                    showReset: false,
                    zoom: self.opus.mapConfig.mapZoom,
                    center: [self.opus.mapConfig.mapDefaultLatitude, self.opus.mapConfig.mapDefaultLongitude]
                });

                self.legend = new L.Control.Legend({
                    id: "legend",
                    position: "bottomright",
                    items: [],
                    collapse: true,
                    legendListClass: "legend-container-short"
                });
                self.map.addControl(self.legend);

                self.updateWMSLayer(self.profile.occurrenceQuery);
                self.updateLegend(self.profile.occurrenceQuery);

                self.map.registerListener("click", self.showOccurrenceDetails);

                self.loading = false;
            },
            function () {
                messageService.alert("An error occurred while retrieving the map information.");
                self.loading = false;
            }
        );
    };

    self.updateWMSLayer = function (occurrenceQuery) {
        if (self.wmsLayer != null) {
            self.map.removeLayer(self.wmsLayer);
        }

        var wmsUrl = biocacheWMSUrl + occurrenceQuery;

        var queryParams = URI.parseQuery(occurrenceQuery);
        var env = "color:" + self.opus.mapConfig.mapPointColour + ";name:circle;size:4;opacity:1";
        if (!_.isUndefined(queryParams.colourBy) && !_.isEmpty(queryParams.colourBy)) {
            env += ";colormode:" + queryParams.colourBy
        }

        self.map.addWmsLayer(undefined, {
            wmsLayerUrl: wmsUrl,
            layers: 'ALA:occurrences',
            format: 'image/png',
            attribution: self.opus.mapConfig.mapAttribution,
            outline: "true",
            transparent: false,
            opacity: 1,
            ENV: env,
            boundsUrl: biocacheBoundsUrl + occurrenceQuery,
            callback: self.setBounds
        });
    };

    self.updateLegend = function(occurrenceQuery) {
        var colourBy = URI.parseQuery(occurrenceQuery).colourBy;
        if (!_.isUndefined(colourBy) && !_.isEmpty(colourBy)) {
            var promise = profileService.getBiocacheLegend(occurrenceQuery, colourBy);
            promise.then(function(legendItems) {
                if (!_.isUndefined(legendItems)) {
                    legendItems.forEach (function (item) {
                        item.name = ALA.OccurrenceMapUtils.formatFacetName(item.name);
                    });
                    self.legend.setItems(legendItems);
                } else {
                    self.legend.clearItems();
                    self.legend.hide();
                }
            });
        }
    };

    self.setBounds = function () {
        self.map.fitBounds();
    };

    self.showOccurrenceDetails = function (clickEvent) {
        var url = biocacheInfoUrl
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

    self.hasEditorCustomisations = function() {
        var hasCustomisations = false;
        if (!_.isUndefined(self.profile)) {
            var baseQuery = extractBaseQuery(self.profile.occurrenceQuery);
            hasCustomisations = baseQuery != self.profile.occurrenceQuery;
        }
        return hasCustomisations;
    };

    self.toggleEditorCustomisations = function() {
        if (self.showingEditorView) {
            self.updateWMSLayer(extractBaseQuery(self.profile.occurrenceQuery));
            self.showingEditorView = false;
        } else {
            self.updateWMSLayer(self.profile.occurrenceQuery);
            self.showingEditorView = true;
        }
    };

    self.saveMapConfiguration = function () {
        self.profile.occurrenceQuery = self.editableMap.getQueryString();

        var promise = profileService.updateProfile(self.opusId, self.profileId, self.profile);
        promise.then(function () {
            messageService.info("Map configuration has been successfully updated.");

            self.updateWMSLayer(self.profile.occurrenceQuery);
            self.toggleEditingMap();
            setTimeout(self.map.redraw, 500);
        }, function () {
            messageService.alert("An error occurred while updating the map configuration.");
        });
    };

    self.resetToDefaultMapConfig = function () {
        var confirm = util.confirm("This will remove all customisations from the map and return the configuration to the default for this collection. Are you sure you wish to proceed?");
        confirm.then(function () {
            // the default map config is just the q= portion of the url
            self.editableMap.setQueryString(extractBaseQuery(self.profile.occurrenceQuery));
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

    // Removes everything other than the q= portion of the biocache query
    function extractBaseQuery(queryString) {
        if (_.isUndefined(queryString)) {
            return null;
        }

        var queryParams = URI.parseQuery(queryString);

        return "q=" + queryParams.q;
    }

    function createEditableMap() {
        if (!config.readonly) {
            var occurrenceQuery = self.profile.occurrenceQuery;

            self.editableMap = new ALA.OccurrenceMap("editOccurrenceMap",
                config.biocacheServiceUrl,
                occurrenceQuery,
                {
                    mapOptions: {
                        zoomToObject: false,
                        showFitBoundsToggle: true,
                        zoom: self.opus.mapConfig.mapZoom + 1, // the edit map panel is bigger than the view, so increase the zoom
                        center: [self.opus.mapConfig.mapDefaultLatitude, self.opus.mapConfig.mapDefaultLongitude]
                    },
                    point: {
                        colour: self.opus.mapConfig.mapPointColour,
                        mapAttribution: self.opus.mapConfig.mapAttribution
                    }
                }
            );

            self.editableMap.map.subscribe(function () {
                $scope.MapForm.$setDirty();
            });
            setTimeout(self.editableMap.map.redraw, 500);
        }
    }
});