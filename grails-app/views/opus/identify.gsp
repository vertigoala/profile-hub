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
    %{-- A hack to get key player initialised--}%
    <div class="row" ng-show="opusCtrl.opus.keybaseKeyId && !opusCtrl.initialiseKeyplayer()">
        <script type="text/ng-template" id="keyplayer.html">
        <key-player key-id="opusCtrl.opus.keybaseKeyId" style="display: block"
                    keybase-url="${g.createLink(controller:'keybase', action:'keyLookup', absolute: true)}"
                    profile-url="${request.scheme}://${request.serverName}${request.serverPort ? ":" + request.serverPort : ""}${request.contextPath}/opus/{{opusCtrl.opus.shortName ? opusCtrl.opus.shortName : opusCtrl.opus.uuid}}/profile"></key-player>
        </script>
        <ng-include src="opusCtrl.keybaseTemplateUrl"/>
    </div>
</div>
</body>

</html>