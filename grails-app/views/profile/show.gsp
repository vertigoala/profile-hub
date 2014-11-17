<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Welcome to Grails</title>
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
</head>

<body>

<a class="btn btn-mini pull-right" href="http://localhost:8081/profile-service/profile/${profile.uuid}">JSON</a>
<g:if test="${!edit}">
    <g:link class="btn btn-mini pull-right" mapping="editProfile"  params="[uuid:profile.uuid]"><i class="icon-edit"></i>&nbsp;Edit</g:link>
</g:if>
<g:else>
    <g:link class="btn btn-mini pull-right" mapping="viewProfile"  params="[uuid:profile.uuid]">Public view</g:link>
</g:else>
<h1><g:link mapping="viewOpus" params="${[uuid: profile.opusId]}">${profile.opusName}</g:link> - ${profile.scientificName?:'empty'}</h1>

<div class="row-fluid">

    <div class="span8">
    <g:each in="${profile.attributes}" var="attribute">
        <div class="bs-docs-example" id="browse_attributes" data-content="${attribute.title}">
            <g:if test="${edit}">
                <g:textArea  class="field span12" rows="10" name="${attribute.title}" value="${attribute.text}" />
            </g:if>
            <g:else>
                <blockquote style="border-left:none;">
                    <p>${attribute.text}</p>
                    <small>
                        Contributed by
                        <cite title="Contributors to this text">
                            <g:each in="${attribute.contributor}" var="c">
                                ${c}
                            </g:each>
                        </cite>
                    </small>
                </blockquote>
            </g:else>
        </div>
    </g:each>
    </div>

    <div class="span4">
        <div id="map" style="height: 400px; "> </div>
        <a class="btn" href="http://avh.ala.org.au/occurrences/search?q=${occurrenceQuery}">View in AVH</a>
    </div>
</div>

<div>
<g:if test="${profile.links}">
<div class="bs-docs-example" id="browse_links" data-content="Links">
    <ul>
       <g:each in="${profile.links}" var="link">
        <li><a href="${link.url}">${link.title}</a>${link.description ? ' - ' + link.description : ''}</li>
       </g:each>
    </ul>
</div>
</g:if>


<g:if test="${records}">

</g:if>


<div class="bs-docs-example hide" id="browse_images" data-content="Images" >

</div>



<script>

    $(function() {
        addTaxonMap();
        addImages();
    });

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
            attribution: "Australian Virtual Herbarium (CHAH)",
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


</body>

</html>