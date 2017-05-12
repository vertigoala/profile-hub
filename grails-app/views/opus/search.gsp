<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile search | Atlas of Living Australia</title>

</head>

<body>
<div ng-controller="OpusController as opusCtrl" ng-init="opusCtrl.loadOpus()">
    <!-- Breadcrumb -->
    <ol class="breadcrumb" ng-cloak>
        <li><a class="font-xxsmall" href="${request.contextPath}/">Profile Collections</a></li>
        <li><a class="font-xxsmall"
               href="${request.contextPath}/opus/{{opusCtrl.urlSuffix}}">{{opusCtrl.opus.title}}</a></li>
    </ol><!-- End Breadcrumb -->

    <h1 class="hidden">Welcome to the eFlora website</h1><!-- Show the H1 on each page -->

    <g:include controller="opus" action="opusSummaryPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="searchPanel" params="[opusId: params.opusId]"/>
</div>
</body>

</html>