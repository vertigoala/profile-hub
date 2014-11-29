<%@ page import="au.org.ala.web.AuthService" %>
<!DOCTYPE html>
<html>
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.3/angular.min.js" type="text/javascript" ></script>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${opus.title} | Profile collections</title>
    <script type="text/javascript" src="http://markusslima.github.io/bootstrap-filestyle/1.0.6/js/bootstrap-filestyle.min.js"> </script>
    <style type="text/css">
    .bootstrap-filestyle label { margin-bottom:8px; }
    </style>
</head>
<body>

<div ng-app="opusEditor">

    <div class="pull-right">
        <button class="btn" onclick="javascript:alert('Not implemented - through to users edits')">Logged in: ${currentUser}</button>
        <g:link class="btn " mapping="viewOpus"  params="[uuid:opus.uuid]">Public view</g:link>
    </div>

    <div style="margin-top:20px;">
    <p class="lead">
        Configure your profile collection, uploading existing datasets to be incorporated in your profile.
    </p>
    </div>

    <div id="opusInfo" class="well">
        <h4>Styling</h4>
        <p>
            <label>Banner image:</label>
            <input type="text" class="input-xxlarge" name="bannerUrl" value="${opus.bannerUrl}"/>
        </p>
        <p>
            <label>Logo:</label>
            <input type="text" class="input-xxlarge" name="logoUrl" value="${opus.logoUrl}"/>
        </p>
        <a class="btn" href="javascript:alert('Not implemented yet')">Save</a>
    </div>

    <div id="opusInfo" class="well">
        <h4>Description</h4>
        <p>
            ${dataResource.pubDescription?:'No description available.'}
        </p>
        <h4>Rights</h4>
        <p>
            ${dataResource.rights?:'No rights statement available.'}
        </p>
        <h4>Citation</h4>
        <p>
            ${dataResource.citation?:'No citation statement available.'}
        </p>
    </div>

    <div id="opusInfo" class="well">
        <h3>Map configuration</h3>
        <p>
            <label>Attribution:</label>
            <input type="text" class="input-xxlarge" name="mapAttribution" value="${opus.mapAttribution}"/>
        </p>
        <p>
            <label>Biocache instance to link to:</label>
            <input type="text" class="input-xxlarge" name="biocacheUrl" value="${opus.biocacheUrl}"/>
        </p>
        <p>
            <label>Biocache instance name:</label>
            <input type="text" class="input-xxlarge" name="biocacheName" value="${opus.biocacheName}"/>
        </p>
        <a class="btn" href="javascript:alert('Not implemented yet')">Save</a>
    </div>


    <g:if test="${opus.enableTaxaUpload}">
    <div class="well" ng-controller="TaxaController">
        <h3>Upload taxa</h3>
        <p>Click below to upload your own list of taxa in CSV format. A profile page will be created for each scientific name uploaded.<br/>
            This list can in include recognised scientific names and/or operational taxonomic unit (OTUs).
            Recognised names will be linked the Australian National Checklists.
        </p>
        <br/>
        <div>
            <input type="file" id="taxaUploadFile" name="taxaUploadFile" />
        </div>
        <br/>
        <button class="btn" ng-click="taxaUpload()"><i class="icon-upload"></i> Upload taxa</button>
    </div>
    </g:if>

    <g:if test="${opus.enableOccurrenceUpload}">
    <div class="well">
        <h3>Occurrence datasets</h3>
        <p>Click below to upload your own occurrence data. This will upload data into the <b>sandbox</b>
        (separate from main Atlas index).
        <br/>You can <b>upload multiple datasets</b> and
            <b>overlay with data from the Atlas</b> index on the maps on profile pages.
        </p>
        <a href="javascript:alert('Not implemented yet!');" class="btn"><i class="icon-upload" ></i> Upload</a>
    </div>
    </g:if>

    <g:if test="${opus.enablePhyloUpload}">
    <div class="well">
        <h3>Phylogenetic trees</h3>
        <p>Click below to upload your nexus tree or select from existing trees stored in the or Treebase.
            Phylogenetic tree will then be included on profile pages.
        </p>
        <a href="javascript:alert('Not implemented yet!');" class="btn"><i class="icon-upload" onclick="alert('Not implemented yet!');"></i> Upload</a>
    </div>
    </g:if>

    <g:if test="${opus.enableKeyUpload}">
    <div class="well">
        <h3>Keys</h3>
        <p>Click below to upload your keys or select keys from Keybase to be visible on profile pages</p>
        <a href="javascript:alert('Not implemented yet!');" class="btn"><i class="icon-upload" onclick="javascript:alert('Not implemented yet!');"></i> Upload</a>
    </div>
    </g:if>

    <div class="well">
        <h3>Approved Image sources</h3>
        <p>Configure the image sources to be included in your profile pages. These are image data resources accessible via Atlas API's.</p>
        <ul>
            <g:each in="${opus.imageSources}" var="imageSource">
                <li><a href="http://collections.ala.org.au/public/show/${imageSource}">${dataResources[imageSource]}</a></li>
            </g:each>
        </ul>
        <a href="javascript:alert('Not implemented yet!');" class="btn"><i class="" onclick="alert('Not implemented yet!');"></i> Save</a>
    </div>

    <div class="well">
        <h3>Approved Specimen/Observation sources</h3>
        <p>Configure the record sources to be included in your profile pages. This will set what data is used on maps.
            These are data resources accessible via Atlas API's.
        </p>
        <table>
            <g:each in="${opus.recordSources}" var="recordSource">
                <tr>
                    <td><a href="http://collections.ala.org.au/public/show/${recordSource}">${dataResources[recordSource]}</a></td>
                    <td><button class="btn btn-mini btn-danger"><i class="icon-minus icon-white"></i></button></td>
                </tr>
            </g:each>
        </table>
        <a href="javascript:alert('Not implemented yet!');" class="btn"><i class="" onclick="alert('Not implemented yet!');"></i> Save</a>
    </div>

    <div class="well">
        <h3>
            Attribute vocabulary
        </h3>
        <p>Select a vocabulary to use, add new terms to an existing vocabulary.</p>
        <ul>
            <li>${vocab}</li>
        </ul>
        <a href="javascript:alert('Not implemented yet!');" class="btn"><i class="icon-edit" onclick="alert('Not implemented yet!');"></i> Update vocabulary</a>
    </div>
</div>

<r:script>
    var opusEditor = angular.module('opusEditor', [])
        .controller('TaxaController', ['$scope', function($scope) {
            $scope.taxaUpload = function(){
                console.log("Taxa upload....");
                var file = document.getElementById('taxaUploadFile').files[0];
	            var formData = new FormData();
                formData.append("taxaUploadFile", file);
                formData.append("opusId", "${opus.uuid}");

                //send you binary data via $http or $resource or do anything else with it
                $.ajax({
                    url: '${grailsApplication.config.profile.service.url}/opus/taxaUpload',
                    type: 'POST',
                    data: formData,
                    cache: false,
                    dataType: 'json',
                    processData: false, // Don't process the files
                    contentType: false, // Set content type to false as jQuery will tell the server its a query string request
                    success: function(data, textStatus, jqXHR){
                        alert("Successful upload - Loaded:" + data.taxaCreated + ", lines skipped: " + data.linesSkipped + ", already exists: " + data.alreadyExists);
                    },
                    error: function(jqXHR, textStatus, errorThrown){
                        // Handle errors here
                        alert("error upload - " + textStatus);
                        console.log('ERRORS: ' + textStatus);
                        console.log(errorThrown)
                        // STOP LOADING SPINNER
                    }
                });
            }

            $(":file").filestyle();
    }]);
</r:script>


</body>

</html>