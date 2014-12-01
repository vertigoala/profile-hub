<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${profile.scientificName} | ${profile.opusName}</title>
    <style type="text/css">
    /* Base class */
    .bs-docs-example {
        position: relative;
        margin: 15px 0;
        padding: 50px 15px 14px;
        *padding-top: 19px;
        background-color: #fff;
        border: 1px solid #ddd;
        -webkit-border-radius: 4px;
        -moz-border-radius: 4px;
        border-radius: 4px;
    }

    /* Echo out a label for the example */
    .bs-docs-example:after {
        /* content: "Example"; */
        content: attr(data-content);
        position: absolute;
        top: -1px;
        left: -1px;
        padding: 6px 12px 8px 12px;
        font-size: 18px;
        font-weight: bold;
        background-color: #f5f5f5;
        border: 1px solid #ddd;
        color: #666;
        -webkit-border-radius: 4px 0 4px 0;
        -moz-border-radius: 4px 0 4px 0;
        border-radius: 4px 0 4px 0;
    }

    .attribute-header-input { font-size: 36px; font-weight: bold; padding: 6px 12px 8px 12px;}
    .attribute-edit { background-color: #f5f5f5; margin-top: 10px;}

    /* Remove spacing between an example and it's code */
    .bs-docs-example + .prettyprint {
        padding-top: 15px;
    }

    /* Gallery styling */
    .imgCon {
        display: inline-block;
        /* margin-right: 8px; */
        text-align: center;
        line-height: 1.3em;
        background-color: #DDD;
        color: #DDD;
        font-size: 12px;
        /*text-shadow: 2px 2px 6px rgba(255, 255, 255, 1);*/
        /* padding: 5px; */
        /* margin-bottom: 8px; */
        margin: 2px 4px 2px 0;
        position: relative;
    }
    .imgCon img {
        height: 200px;
        min-width: 180px;
    }
    .imgCon .meta {
        opacity: 0.6;
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        overflow: hidden;
        text-align: left;
        padding: 4px 5px 2px 5px;
        background-color: #DDD;
        color: #000;
        width:100%;
        font-weight:bold;
    }
    .imgCon .brief {
        color: black;
        background-color: white;
    }
    .imgCon .detail {
        color: white;
        background-color: black;
        opacity: 0.7;
    }

    </style>
    <link rel="stylesheet" href="http://leafletjs.com/dist/leaflet.css" />
    <script src="http://leafletjs.com/dist/leaflet.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.3/angular.min.js" type="text/javascript" ></script>
    <script src="//angular-ui.github.io/bootstrap/ui-bootstrap-tpls-0.12.0.js"></script>
</head>

<body>

<div id="container" ng-app="profileEditor">
<div class="pull-right" style="margin-top:20px;">

<g:if test="${!edit}">
    <g:link class="btn btn" mapping="editProfile"  params="[uuid:profile.uuid]"><i class="icon-edit"></i>&nbsp;Edit</g:link>
</g:if>
<g:else>
    <button class="btn" onclick="javascript:alert('Not implemented - through to users edits')">Logged in: ${currentUser}</button>
    <g:link class="btn" mapping="viewProfile"  params="[uuid:profile.uuid]">Public view</g:link>
</g:else>
<a class="btn btn" href="${grailsApplication.config.profile.service.url}/profile/${profile.uuid}">JSON</a>
</div>

<h1>${profile.scientificName?:'empty'}</h1>

<div class="row-fluid">

    <div class="span8">
        <div ng-controller="AttributeEditor">
            <div class="ng-show" ng-show="!readonly">
                <button ng-click="addAttribute()" class="btn"><i class="icon icon-plus"></i>Add attribute</button>
                <button ng-click="addImage()" class="btn"><i class="icon icon-plus"></i>Add image</button>
            </div>
            <div ng-repeat="attribute in attributes">
                <div class="well attribute-edit" id="browse_attributes_edit" class=" ng-show" ng-show="!readonly">
                    <g:textField typeahead="attributeTitle.name for attributeTitle in attributeTitles | filter:$viewValue" class="form-control attribute-header-input" ng-model="attribute.title" name="title" value="title"/>
                    <g:textArea class="field span12" rows="10" ng-model="attribute.text" name="text" />
                    <div class="row-fluid">
                        <span class="span8"><span class="pull-left">{{ attribute.status }}</span></span>
                        <span class="span4">
                            <button class="btn btn-danger pull-right" ng-click="deleteAttribute($index)"> Delete </button>
                            &nbsp;
                            <button class="btn btn pull-right" ng-click="saveAttribute($index)">
                                <span ng-show="!isSaving($index)" id="saved">Save</span>
                                <span ng-show="isSaving($index)" id="saving">Saving....</span>
                            </button>
                        </span>
                    </div>
                </div>
                <div class="bs-docs-example" id="browse_attributes" data-content="{{ attribute.title }}" class="ng-show" ng-show="readonly">
                    <blockquote style="border-left:none;" >
                        <p>{{ attribute.text }}</p>
                        <small>
                        Contributed by
                        <cite title="Contributors to this text">
                            {{ attribute.contributor.join(', ') }}
                        </cite>
                        </small>
                    </blockquote>
                </div>
            </div>
        </div>

        <g:if test="${profile.links && !edit}">
            <div class="bs-docs-example" id="browse_links" data-content="Links">
                <ul>
                    <g:each in="${profile.links}" var="link">
                        <li><a href="${link.url}">${link.title}</a>${link.description ? ' - ' + link.description : ''}</li>
                    </g:each>
                </ul>
                <g:if test="${edit}">
                <a class="btn" href="javascript:alert('not implemented yet')"><i class="icon icon-plus"> </i> Add link</a>
                </g:if>
            </div>
        </g:if>
        <g:elseif test="${edit}">
            <div ng-controller="LinksEditor" class="bs-docs-example" id="browse_links" data-content="Links">
                <div style="margin-bottom: 10px;">
                    <button class="btn" ng-click="saveLinks()">Save changes</button>
                    <button class="btn" ng-click="addLink()"><i class="icon icon-plus"> </i> Add new link</button>
                </div>
                <table class="table table-striped">
                    <tr ng-repeat="link in links">
                        <td>
                            <label>URL</label>
                            <input type="text" class="input-xxlarge" value="{{link.url}}"/><br/>
                            <label>Title</label>
                            <input type="text" class="input-xxlarge" value="{{link.title}}"/><br/>
                            <label>Description</label>
                            <textarea rows="3" class="input-xxlarge">{{link.description}}</textarea>
                        </td>
                        <td><button class="btn" ng-click="deleteLink($index)"><i class="icon icon-minus"></i> Remove</button></td>
                    </tr>
                </table>
            </div>
        </g:elseif>

        <g:if test="${edit}">
        <div ng-controller="BHLLinksEditor" class="bs-docs-example" id="browse_bhllinks" data-content="Biodiversity Heritage Library references">

            <p class="lead">
                Add links to the biodiversity heritage library. Links should be of the form:
                <b>http://biodiversitylibrary.org/page/29003916</b>
            </p>

            <div style="margin-bottom: 10px;">
                <button class="btn" ng-click="saveLinks()">Save changes</button>
                <button class="btn" ng-click="addLink()"><i class="icon icon-plus"> </i> Add new reference</button>
            </div>

            <table class="table table-striped">
                <tr ng-repeat="link in bhl">
                    <td>
                        <input type="hidden" name="link.uuid" value="{{link.uuid}}"/>
                        <label>URL</label>
                        <input type="text" class="input-xxlarge" ng-model="link.url" value="{{link.url}}"  ng-change="updateThumbnail($index)"/><br/>
                        <label>Title</label>
                        <input type="text" class="input-xxlarge" ng-model="link.title"  value="{{link.title}}"/><br/>
                        <label>Description</label>
                        <textarea rows="3" class="input-xxlarge" ng-model="link.description">{{link.description}}</textarea>
                        <br/>
                        <cite ng-show="hasThumbnail($index)">
                            <span><b>BHL metadata</b></span><br/>
                            <span>
                                Title: {{link.fullTitle}}
                            </span>
                            <br/>
                            <span>
                                Edition: {{link.edition}}
                            </span>
                            <br/>
                            <span>
                                Publisher: {{link.publisherName}}
                            </span>
                            <br/>
                            <span>
                               DOI: <a href="http://dx.doi.org/{{link.bhlDoi}}">{{link.doi}}</a>
                            </span>
                        </cite>
                    </td>
                    <td>
                        <button class="btn" ng-click="deleteLink($index)"><i class="icon icon-minus"></i> Remove</button>
                        <br/>
                        <div ng-show="hasThumbnail($index)">
                            <a href="{{link.url}}" target="_blank">
                                <img ng-model="link.thumbnail" style="margin-top:20px; max-height:200px;" src="{{link.thumbnail}}" alt="{{link.title}}" class="img-rounded"/>
                            </a>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
        </g:if>
        <g:elseif test="${profile.bhl}">
            <div ng-controller="BHLLinksEditor" class="bs-docs-example" id="browse_bhllinks" data-content="Biodiversity Heritage Library references">
                <table class="table table-striped">
                    <tr ng-repeat="link in bhl">
                        <td>
                            <span>
                                Title: {{link.title}}
                            </span>
                            <br/>
                            <span>
                                Description: {{link.description}}
                            </span>
                            <br/>

                            <cite ng-show="hasThumbnail($index)">
                                <span>
                                    Title: {{link.fullTitle}}
                                </span>
                                <br/>
                                <span>
                                    Edition: {{link.edition}}
                                </span>
                                <br/>
                                <span>
                                    Publisher: {{link.publisherName}}
                                </span>
                                <br/>
                                <span>
                                    DOI: <a href="http://dx.doi.org/{{link.bhlDoi}}">{{link.doi}}</a>
                                </span>
                            </cite>
                        </td>
                        <td>
                            <div ng-show="hasThumbnail($index)">
                                <a href="{{link.url}}" target="_blank">
                                    <img ng-model="link.thumbnail" src="{{link.thumbnail}}" alt="{{link.title}}" class="img-rounded"/>
                                </a>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </g:elseif>


        <g:if test="${classification}">
            <div class="bs-docs-example" id="browse_taxonomy" data-content="Taxonomy from ${speciesProfile.taxonConcept.infoSourceName}">
                <ul>
                    <g:each in="${classification}" var="taxon">
                        <li><g:link mapping="viewProfile" params="${[uuid: taxon.guid]}">${taxon.rank.capitalize()}: ${taxon.scientificName}</g:link></li>
                    </g:each>
                </ul>
            </div>
        </g:if>

        <g:if test="${speciesProfile && speciesProfile.taxonName}">
            <div class="bs-docs-example" id="browse_names" data-content="Nomenclature">
                <ul style="list-style: none; margin-left:0px;">
                    <li>
                        <blockquote style="border-left:none;">
                            <p>${speciesProfile.taxonName.nameComplete} ${speciesProfile.taxonName.authorship}</p>
                        </blockquote>
                     </li>
                    <g:each in="${speciesProfile.synonyms}" var="synonym">
                        <li>
                            <blockquote  style="border-left:none;">
                            <p>${synonym.nameString}</p>
                            <cite>- ${synonym.referencedIn}</cite>
                            </blockquote>
                        </li>
                    </g:each>
                </ul>
            </div>
        </g:if>

        <div class="bs-docs-example hide" id="browse_images" data-content="Images">
        </div>


    </div>

    <div class="span4">
        <div id="map" style="height: 400px; margin-top:10px;"> </div>
        <a class="btn" href="${opus.biocacheUrl}/occurrences/search?q=${occurrenceQuery}">View in ${opus.biocacheName}</a>
        <div id="firstImage" class="hide" style="margin-top:15px;"></div>
        <div class="bs-docs-example hide" id="browse_lists" data-content="Conservation & sensitivity lists">
            <ul></ul>
        </div>
    </div>
</div>

<div>
</div>

<script>

    $(function() {
        addTaxonMap();
        addImages();
        addLists();
    });

    function addLists(){

        $.ajax({
            url: "http://lists.ala.org.au/ws/species/${profile.guid}",
            jsonp: "callback",
            dataType: "jsonp",
            data: {
                format: 'json'
            },
            success: function( response ) {
                if(response.length > 0) {
                    console.log("number of list entries: " + response.length);
                    for(var i=0; i< response.length; i++){
                        $('#browse_lists ul').append('<li><a href="http://lists.ala.org.au/speciesListItem/list/' + response[i].dataResourceUid +'">' + response[i].list.listName + '</a></li>');
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
            url: "http://biocache.ala.org.au/ws/occurrences/search.json",
            jsonp: "callback",
            dataType: "jsonp",
            data: {
                q: "${imagesQuery}",
                fq: "multimedia:Image",
                format: 'json'
            },
            success: function( response ) {
                if(response.totalRecords > 0) {
                    console.log("number of records with images: " + response.totalRecords);

                    var firstImage = response.occurrences[0];

                    $('#firstImage').append('<div class="imgConXXX"><a href="http://biocache.ala.org.au/occurrences/'+firstImage.uuid+'"><img src="'+firstImage.largeImageUrl+'"/></a> <div class="meta">' + firstImage.dataResourceName + '</div></div>');
                    $('#firstImage').show();


                    $.each(response.occurrences, function( key, record ) {
                        $('#browse_images').append('<div class="imgCon"><a href="http://biocache.ala.org.au/occurrences/'+record.uuid+'"><img src="'+record.largeImageUrl+'"/></a> <div class="meta">' + record.dataResourceName + '</div></div>');
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

        //add an occurrence layer for macropus
        var acacia = L.tileLayer.wms("http://biocache.ala.org.au/ws/mapping/wms/reflect?q=${occurrenceQuery}", {
            layers: 'ALA:occurrences',
            format: 'image/png',
            transparent: true,
            attribution: "${opus.mapAttribution}",
            bgcolor: "0x000000",
            outline: "true",
            ENV: "color:FF9900;name:circle;size:4;opacity:1"
        });

        var speciesLayers = new L.LayerGroup();
        acacia.addTo(speciesLayers);

        var map = L.map('map', {
            center: [-23.6, 133.6],
            zoom: 3,
            layers: [speciesLayers]
        });

        var streetView = L.tileLayer('https://{s}.tiles.mapbox.com/v3/{id}/{z}/{x}/{y}.png', {
            maxZoom: 18,
            attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, ' +
                    '<a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, ' +
                    'Imagery © <a href="http://mapbox.com">Mapbox</a>',
            id: 'examples.map-i875mjb7'
        }).addTo(map);

        var baseLayers = {
            "Street": streetView
        };

        var overlays = {
            "${profile.scientificName}": acacia
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
</script>

<r:script>

    var profileEditor = angular.module('profileEditor', ['ui.bootstrap'])
        .controller('BHLLinksEditor', ['$scope', function($scope) {

            $scope.bhl = [];

            $.ajax({
                type:"GET",
                url: "${grailsApplication.config.profile.service.url}/profile/${profile.uuid}",
                success: function( data ) {
                     $scope.bhl = data.bhl;
                     $scope.$apply();
                },
                error: function(jqXHR, textStatus, errorThrown){
                    console.log("There was a problem retrieving profile..." + textStatus);
                }
            })


            $scope.hasThumbnail = function(idx){
                $scope.updateThumbnail(idx);
                if($scope.bhl[idx].thumbnail != ''){
                    return true;
                } else {
                    return false;
                }
            }

            $scope.updateThumbnail = function(idx){
                console.log("Updating...");
                var url = $scope.bhl[idx].url.trim();
                if(url != ""){
                    //remove any anchors
                    console.log("URL " + url);
                    var anchorIdx = url.lastIndexOf("#");
                    if(anchorIdx > 0) {
                        url = url.substring(0, anchorIdx - 1);
                    }
                    console.log("URL -stripped " + url);
                    //TODO trailing slash ?

                    var lastSlash = url.lastIndexOf("/");
                    var pageId = url.substring(lastSlash + 1);
                    console.log("URL - pageId " + pageId);
                    $scope.bhl[idx].thumbnail = "http://biodiversitylibrary.org/pagethumb/" + pageId;

                    $.ajax({
                        type:"GET",
                        url: "${createLink(controller: "BHL", action:"pageLookup")}/" + pageId,
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

//                    $scope.$apply();
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
                alert("Saving BHL links");
                $.ajax({
                    type: "POST",
                    url: "${createLink(controller: 'profile', action: 'updateBHLLinks')}/${profile.uuid}",
                    dataType: "json",
                    contentType: 'application/json',
                    data: JSON.stringify({ profileUuid: "${profile.uuid}", links: $scope.bhl }),
                    success: function( data ) {
                        alert("Saved!");
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
                url: "${grailsApplication.config.profile.service.url}/profile/${profile.uuid}",
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
                    url: "${createLink(controller: 'profile', action: 'updateLinks')}/${profile.uuid}",
                    dataType: "json",
                    contentType: 'application/json',
                    data: JSON.stringify({ profileUuid: "${profile.uuid}", links: $scope.links }),
                    success: function( data ) {
                        alert("Saved!");
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

            $scope.readonly = ${!edit};
            $scope.attributes = [];
            $scope.attributeTitles = [];
            $scope.isSaving = false;

            $.ajax({
                type:"GET",
                url: "${grailsApplication.config.profile.service.url}/profile/${profile.uuid}",
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
                url: "${grailsApplication.config.profile.service.url}/vocab/${opus.attributeVocabUuid}",
                success: function( data ) {
                     $scope.attributeTitles = data.terms;
                },
                error: function(jqXHR, textStatus, errorThrown){
                    console.log("There was a problem retrieving profile..." + textStatus);
                }
            });


            $scope.deleteAttribute = function(idx){
                var confirmed = window.confirm("Are you sure?")
                if(confirmed){
                    if($scope.attributes[idx].uuid !== ""){
                        var attribute =  $scope.attributes[idx];
                        //delete with ajax
                        $.ajax({
                            type: "DELETE",
                            url: "${createLink(mapping: "deleteAttribute")}/" + attribute.uuid +"?profileUuid=${profile.uuid}",
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
//                        $scope.$apply();
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

            $scope.saveAttribute = function(idx) {
                console.log("Saving attribute " + idx);
                var attribute = $scope.attributes[idx];
                $scope.attributes[idx].saving = true;

                //ajax post
                $.ajax({
                    type: "POST",
                    url: "${createLink(controller:"profile", action:"updateAttribute")}/" + attribute.uuid,
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
</r:script>


</body>

</html>