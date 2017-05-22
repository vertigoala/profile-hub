<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile search | Atlas of Living Australia</title>

</head>

<body>
<div ng-controller="OpusController as opusCtrl" ng-init="opusCtrl.loadOpus()">

    <h1 class="hidden">Welcome to the eFlora website</h1><!-- Show the H1 on each page -->

    <g:include controller="opus" action="opusSummaryPanel" params="[opusId: params.opusId]"/>

    <p:help help-id="opus.documents" float="false" show="${params.isOpusEditor}"/>
    <g:render template="../common/attachments" model="[hideHeading: true]"/>
</div>
</body>

</html>