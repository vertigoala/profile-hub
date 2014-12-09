
function addLists(){

    $.ajax({
        url: PROFILE.listsUrl,
        jsonp: "callback",
        dataType: "jsonp",
        data: { format: 'json' },
        success: function( response ) {
            if(response instanceof Array && response.length > 0) {
                console.log("number of list entries: " + response.length);
                for(var i=0; i< response.length; i++){
                    $('#browse_lists ul').append('<li><a href="' + PROFILE.listUrl + response[i].dataResourceUid +'">' + response[i].list.listName + '</a></li>');
                }
                $('#browse_lists').show();
            }
        },
        error: function(jqXHR, textStatus, errorThrown){
            console.log("Error collecting lists JSON - " + errorThrown);
        }
    });
}

function addImages(){

    $.ajax({
        url: PROFILE.biocacheUrl,
        jsonp: "callback",
        dataType: "jsonp",
        data: {
            q: PROFILE.imagesQuery,
            fq: "multimedia:Image",
            format: 'json'
        },
        success: function( response ) {
            if(response.totalRecords > 0) {
                console.log("number of records with images: " + response.totalRecords);

                var firstImage = response.occurrences[0];

                $('#firstImage').append('<div class="imgConXXX"><a href="'+ PROFILE.recordUrl + firstImage.uuid+'"><img src="'+firstImage.largeImageUrl+'"/></a> <div class="meta">' + firstImage.dataResourceName + '</div></div>');
                $('#firstImage').show();

                $.each(response.occurrences, function( key, record ) {
                    $('#browse_images').append('<div class="imgCon"><a href="'+ PROFILE.recordUrl + record.uuid+'"><img src="'+record.largeImageUrl+'"/></a> <div class="meta">' + record.dataResourceName + '</div></div>');
                });
                $('#browse_images').show();
            }
        },
        error: function(jqXHR, textStatus, errorThrown){
            console.log("Error completing images - JSON - " + errorThrown);
        }
    });
}

function addTaxonMap(){

    var speciesLayer = L.tileLayer.wms(PROFILE.wmsLayer, {
        layers: 'ALA:occurrences',
        format: 'image/png',
        transparent: true,
        attribution: PROFILE.mapAttribution,
        bgcolor: "0x000000",
        outline: "true",
        ENV: "color:" + PROFILE.mapPointColour   + ";name:circle;size:4;opacity:1"
    });

    var speciesLayers = new L.LayerGroup();
    speciesLayer.addTo(speciesLayers);

    var map = L.map('map', {
        center: PROFILE.mapCenter,
        zoom: PROFILE.mapZoom,
        layers: [speciesLayers]
    });

    var streetView = L.tileLayer(PROFILE.mapBaseLayer, {
        maxZoom: 18,
        attribution: PROFILE.mapBaseLayerAttribution,
        id: 'examples.map-i875mjb7'
    }).addTo(map);

    var baseLayers = {
        "Street": streetView
    };

    var layerTitle =  "Layer for " + PROFILE.scientificName;

    var overlays = {
        layerTitle: speciesLayer
    };

    L.control.layers(baseLayers, overlays).addTo(map);

    map.on('click', onMapClick);
}

function onMapClick(e) {
    $.ajax({
        url: "http://biocache.ala.org.au/ws/occurrences/info",
        jsonp: "callback",
        dataType: "jsonp",
        data: {
            q: "${occurrenceQuery}",
            zoom: "6",
            lat: e.latlng.lat,
            lon: e.latlng.lng,
            radius:20,
            format: "json"
        },
        success: function( response ) {
            var popup = L.popup()
                .setLatLng(e.latlng)
                .setContent("<h3>Test</h3>Occurrences at this point: " + response.count)
                .openOn(map);
        }
    });
}

var profileEditor = angular.module('profileEditor', ['ui.bootstrap'])
    .controller('BHLLinksEditor', ['$scope', function($scope) {

        $scope.bhl = [];

        $.ajax({
            type:"GET",
            url: PROFILE.profileServiceUrl + "/profile/" + PROFILE.uuid,
            success: function( data ) {
                $scope.bhl = data.bhl;
                for(var i=0; i<$scope.bhl.length; i++){
                    $scope.bhl[i].thumbnail = PROFILE.bhlThumbUrl + extractPageId($scope.bhl[i].url);
                }
                $scope.$apply();
            },
            error: function(jqXHR, textStatus, errorThrown){
                console.log("There was a problem retrieving profile..." + textStatus);
            }
        })

        function extractPageId(url){
            console.log("URL " + url);
            var anchorIdx = url.lastIndexOf("#");
            if(anchorIdx > 0) {
                url = url.substring(0, anchorIdx - 1);
            }
            console.log("URL - stripped: " + url);

            var lastSlash = url.lastIndexOf("/");
            var pageId = url.substring(lastSlash + 1);
            console.log("URL - pageId " + pageId);
            return pageId;
        }

        $scope.hasThumbnail = function(idx){
            return $scope.bhl[idx].thumbnail !== undefined && $scope.bhl[idx].thumbnail != '';
        }

        $scope.updateThumbnail = function(idx){
            console.log("Updating...");
            var url = $scope.bhl[idx].url.trim();
            if(url != ""){
                //remove any anchors
                console.log("URL " + url);
                var pageId = extractPageId(url);
                $scope.bhl[idx].thumbnail = PROFILE.bhlThumbUrl + pageId;

                $.ajax({
                    type:"GET",
                    url: PROFILE.bhlLookupUrl + "/" + pageId,
                    success: function( data ) {
                        $scope.bhl[idx].fullTitle = data.Result.FullTitle;
                        $scope.bhl[idx].edition = data.Result.Edition;
                        $scope.bhl[idx].publisherName = data.Result.PublisherName;
                        $scope.bhl[idx].doi = data.Result.Doi;
                        $scope.$apply();
                    },
                    error: function(jqXHR, textStatus, errorThrown){
                        console.log("There was a problem retrieving profile..." + textStatus);
                    }
                })
            }
        }
        $scope.addLink = function(){
            $scope.bhl.unshift(
                {   url:"",
                    description: "",
                    title: "",
                    thumbnail: ""
                });
        }
        $scope.deleteLink = function(idx){
            $scope.bhl.splice(idx, 1);
        }
        $scope.saveLinks = function(){
            //ajax post
            //alert("Saving BHL links");
            $.ajax({
                type: "POST",
                url: PROFILE.bhlUpdateUrl + "/" + PROFILE.uuid,
                dataType: "json",
                contentType: 'application/json',
                data: JSON.stringify({ profileUuid: PROFILE.uuid, links: $scope.bhl }),
                success: function( data ) {
                    $scope.$apply();
                },
                error: function(jqXHR, textStatus, errorThrown){
                    alert("Errored " + textStatus);
                    $scope.$apply();
                }
            });
        }
    }])
    .controller('LinksEditor', ['$scope', function($scope) {
        $scope.links = [];

        $.ajax({
            type:"GET",
            url: PROFILE.profileServiceUrl + "/profile/" + PROFILE.uuid,
            success: function( data ) {
                $scope.links = data.links;
                $scope.$apply();
            },
            error: function(jqXHR, textStatus, errorThrown){
                console.log("There was a problem retrieving profile..." + textStatus);
            }
        })

        $scope.addLink = function(){
            $scope.links.unshift({uuid:"", url:"http://", description:"Add description", title:"Title"});
        }
        $scope.deleteLink = function(idx){
            $scope.links.splice(idx, 1);
        }
        $scope.saveLinks = function(){
            //ajax post
            $.ajax({
                type: "POST",
                url: PROFILE.linksUpdateUrl  + "/" + PROFILE.uuid,
                dataType: "json",
                contentType: 'application/json',
                data: JSON.stringify({ profileUuid: PROFILE.uuid, links: $scope.links }),
                success: function( data ) {
                    $scope.$apply();
                },
                error: function(jqXHR, textStatus, errorThrown){
                    alert("Errored " + textStatus);
                    $scope.$apply();
                }
            });
        }
    }])
    .controller('AttributeEditor', ['$scope', function($scope) {

        $scope.readonly = !PROFILE.edit;
        $scope.attributes = [];
        $scope.attributeTitles = [];
        $scope.isSaving = false;

        $.ajax({
            type:"GET",
            url: PROFILE.profileServiceUrl + "/profile/" + PROFILE.uuid,
            success: function( data ) {
                $scope.attributes = data.attributes;
                $scope.$apply();
            },
            error: function(jqXHR, textStatus, errorThrown){
                console.log("There was a problem retrieving profile..." + textStatus);
            }
        })

        $.ajax({
            type:"GET",
            url: PROFILE.profileServiceUrl + "/vocab/" + PROFILE.vocabUuid,
            success: function( data ) {
                $scope.attributeTitles = data.terms;
            },
            error: function(jqXHR, textStatus, errorThrown){
                console.log("There was a problem retrieving profile..." + textStatus);
            }
        });

        $scope.revertAttribute = function(attributeIdx, auditIdx){
            $scope.attributes[attributeIdx].title = $scope.attributes[attributeIdx].audit[auditIdx].object.title;
            $scope.attributes[attributeIdx].text = $scope.attributes[attributeIdx].audit[auditIdx].object.text;
            $scope.$apply();
        }
        $scope.showAudit = function(idx){
            $.ajax({
                type:"GET",
                url: PROFILE.profileServiceUrl + "/audit/object/" + $scope.attributes[idx].uuid,
                success: function( data ) {
                    console.log( data);
                    $scope.attributes[idx].audit = data;
                    $scope.$apply();
                },
                error: function(jqXHR, textStatus, errorThrown){
                    console.log("There was a problem retrieving profile..." + textStatus);
                }
            })
        }
        $scope.deleteAttribute = function(idx){
            var confirmed = window.confirm("Are you sure?")
            if(confirmed){
                if($scope.attributes[idx].uuid !== ""){
                    var attribute =  $scope.attributes[idx];
                    //delete with ajax
                    $.ajax({
                        type: "DELETE",
                        url: PROFILE.deleteAttributeUrl + "/" + attribute.uuid + "?profileUuid=" + PROFILE.uuid,
                        dataType: "json",
                        data: {
                            uuid: attribute.uuid,
                            profileUuid: "${profile.uuid}"
                        },
                        success: function( response ) {
                            $scope.attributes.splice(idx, 1);
                            console.log("deleting attributes: " + $scope.attributes.length);
                            $scope.$apply();
                        },
                        error: function(jqXHR, textStatus, errorThrown){
                            $scope.attributes[idx].status = "There was a problem deleting...";
                        }
                    });
                } else {
                    $scope.attributes.splice(idx, 1);
                    console.log("Local delete only deleting attributes: " + $scope.attributes.length);
                }
            }
        }
        $scope.addAttribute = function(){
            $scope.attributes.unshift(
                {"uuid":"", "title":"Description", "text":"My description....", contributor: []}
            );
            console.log("adding attributes: " + $scope.attributes.length);
        }

        $scope.addImage = function(){
            alert("Not implemented yet. Would upload to biocache & store image in image service");
        }

        $scope.isSaving = function(idx){
            return $scope.attributes[idx].saving;
        }

        $scope.saveAttribute = function(idx, attributeForm) {
            console.log("Saving attribute " + idx);
            var attribute = $scope.attributes[idx];
            $scope.attributes[idx].saving = true;

            //ajax post
            $.ajax({
                type: "POST",
                url: PROFILE.updateAttribute + "/" + attribute.uuid,
                dataType: "json",
                data: {
                    profileUuid:"${profile.uuid}",
                    uuid:attribute.uuid,
                    title:attribute.title,
                    text:attribute.text
                },

                success: function( data ) {
                    $scope.attributes[idx].saving = false;
                    $scope.attributes[idx].status = "Last saved " + new Date();
                    console.log("uuid before save: " + $scope.attributes[idx].uuid )
                    $scope.attributes[idx].uuid = data.uuid;
                    console.log("uuid after save: " + $scope.attributes[idx].uuid )
                    attributeForm.$setPristine();
                    $scope.$apply();
                },
                error: function(jqXHR, textStatus, errorThrown){
                    $scope.attributes[idx].saving = false;
                    $scope.attributes[idx].status = "There was a problem saving...";
                    $scope.$apply();
                }
            });
        };
    }]);