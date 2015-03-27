<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile collections</title>
</head>

<body>

<div ng-app="profileEditor" ng-controller="OpusController as opusCtrl" ng-init="opusCtrl.loadOpus()">
    <div class="row-fluid" ng-cloak>
        <div class="span6">
            <ol class="breadcrumb" role="navigation">
                <li><i class="fa fa-arrow-left"></i><span class="divider"/><a href="${request.contextPath}/" target="_self">View all profile collections</a></li>
            </ol>
        </div>
        <g:render template="../layouts/login"/>
    </div>

    <div class="row-fluid" ng-if="opusCtrl.readonly">
        <div class="span8">
            <div id="opusInfo" ng-cloak style="margin-top:20px;">
                <p class="lead">
                    {{opusCtrl.dataResource.pubDescription}}
                </p>
            </div>

            <g:include controller="opus" action="searchPanel" params="[opusId: params.opusId]"/>
        </div>

        <div class="span4">

            <g:include controller="opus" action="opusSummaryPanel" params="[opusId: params.opusId]"/>
        </div>
    </div>

    <div class="row-fluid" ng-if="!opusCtrl.readonly">
        <button class="btn pull-right" ng-click="opusCtrl.toggleViewEdit()">Public View</button>
    </div>
</div>
</body>

</html>