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
    </style>
    <link rel="stylesheet" href="http://leafletjs.com/dist/leaflet.css" />
    <script src="http://leafletjs.com/dist/leaflet.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
</head>

<body>

<a class="btn btn-mini pull-right" href="http://localhost:8081/profile-service/profile/${profile.uuid}">JSON</a>

<h1>${profile.opusName} - ${profile.scientificName?:'empty'}</h1>

<div class="row-fluid">

    <div class="span8">
    <g:each in="${profile.attributes}" var="attribute">
        <div class="bs-docs-example" id="browse_species_images" data-content="${attribute.title}">
            <p>${attribute.text} </p>
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
<div class="bs-docs-example" id="browse_species_images" data-content="Links">
    <ul>
       <g:each in="${profile.links}" var="link">
        <li><a href="${link.url}">${link.title}</a>${link.description ? ' - ' + link.description : ''}</li>
       </g:each>
    </ul>
</div>
</g:if>


<g:if test="${images}">

</g:if>

<g:if test="${records}">

</g:if>


<script>

    $(function() {

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
    });

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