<!DOCTYPE html>
<html>
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.3/angular.min.js" type="text/javascript" ></script>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${opus.title} | Profile collections</title>

</head>
<body>

<div ng-app="searchProfiles">

    <h1>${opus.title}</h1>

    <div class="pull-right">
        <g:link class="btn pull-right" mapping="viewOpus"  params="[uuid:opus.uuid]">Public view</g:link>
    </div>

    <p class="lead">
        Configure your profile collection, uploading existing datasets to be incorporated in your profile.
    </p>

    <div id="opusInfo" class="well">
        <h2>Metadata - edited in the <a href="http://collections.ala.org.au/public/show/${opus.dataResourceUid}">collectory</a></h2>
        <h4>Description</h4>
        <p>
            ${dataResource.pubDescription?:'No description  available.'}
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

    <g:if test="${opus.enableTaxaUpload}">
    <div class="well">
        <h3>Upload taxa</h3>
        <p>Click below to upload your own list of taxa in CSV format. A profile page will be created for each scientific name uploaded.<br/>
            This list can in include recognised scientific names and/or operational taxonomic unit (OTUs).
            Recognised names will be linked the Australian National Checklists.
        </p>
        <a href="javascript:alert('Not implemented yet!');" class="btn"><i class="icon-upload" ></i> Upload</a>
    </div>
    </g:if>

    <g:if test="${opus.enableOccurrenceUpload}">
    <div class="well">
        <h3>Occurrence datasets</h3>
        <p>Click below to upload your own occurrence data</p>
        <a href="javascript:alert('Not implemented yet!');" class="btn"><i class="icon-upload" ></i> Upload</a>
    </div>
    </g:if>

    <g:if test="${opus.enablePhyloUpload}">
    <div class="well">
        <h3>Phylogenetic trees</h3>
        <p>Click below to upload your nexus tree or select from existing trees stored in the or Treebase</p>
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
        <p>Configure the image sources to be included in your profile pages</p>
        <ul>
            <g:each in="${opus.imageSources}" var="imageSource">
                <li><a href="http://collections.ala.org.au/public/show/${imageSource}">${dataResources[imageSource]}</a></li>
            </g:each>
        </ul>
    </div>

    <div class="well">
        <h3>Approved Specimen/Observation sources</h3>
        <p>Configure the record sources to be included in your profile pages. This will set what data is used on maps</p>
        <ul>
            <g:each in="${opus.recordSources}" var="recordSource">
                <li><a href="http://collections.ala.org.au/public/show/${recordSource}">${dataResources[recordSource]}</a></li>
            </g:each>
        </ul>
    </div>

    <div class="well">
        <h3>
            Attribute vocabulary
        </h3>
        <ul>
            <li>${vocab}</li>
        </ul>
    </div>
</div>


</body>

</html>