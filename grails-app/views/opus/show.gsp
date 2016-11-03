<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile collections</title>
</head>

<body>

<div ng-controller="OpusController as opusCtrl" ng-init="opusCtrl.loadOpus()">
    <!-- Breadcrumb -->
    <ol class="breadcrumb" ng-cloak>
        <li><a class="font-xxsmall" href="${request.contextPath}/">Profile Collections</a></li>
        <li class="font-xxsmall active">{{opusCtrl.opus.title}}</li>
    </ol><!-- End Breadcrumb -->

    <h1 class="hidden">Welcome to the eFlora website</h1><!-- Show the H1 on each page -->

<g:include controller="opus" action="opusSummaryPanel" params="[opusId: params.opusId]"/>

    <div class="pull-right">
        <g:if test="${params.isOpusEditor}">
            <div class="btn-group" ng-controller="ProfileController as profileCtrl" >
                <button id="addProfile" class="btn btn-default" ng-click="profileCtrl.createProfile(opusCtrl.opusId, false)">
                    <span class="fa fa-plus">&nbsp;</span>Add a new profile
                </button>
                <button class="btn btn-default dropdown-toggle" data-toggle="dropdown" target="_self">
                    <span class="caret"></span>
                </button>
                <ul class="dropdown-menu">
                    <li>
                        <a target="_self" id="duplicateProfile" ng-click="profileCtrl.createProfile(opusCtrl.opusId, true)">
                            <span class="fa fa-copy">&nbsp;</span>Copy an existing profile
                        </a>
                    </li>
                </ul>
            </div>
        </g:if>
        <g:if test="${params.isOpusAdmin}">
            <a href="${request.contextPath}/opus/{{opusCtrl.opusId}}/update" target="_self"
               class="btn btn-default" ng-hide="!config.readonly"><i class="fa fa-edit"></i> Edit configuration</a>
        </g:if>
    </div>

    <tabset>
        <tab heading="Search" class="font-xxsmall">
            <g:include controller="opus" action="searchPanel" params="[opusId: params.opusId]"/>
        </tab>
        <tab heading="Browse" class="font-xxsmall">
            <g:include controller="opus" action="browsePanel" params="[opusId: params.opusId]"/>
        </tab>
        <tab heading="Identify" class="font-xxsmall" ng-show="opusCtrl.opus.keybaseKeyId" select="opusCtrl.initialiseKeyplayer()">
            <div class="row">
                <script type="text/ng-template" id="keyplayer.html">
                    <key-player key-id="opusCtrl.opus.keybaseKeyId" style="display: block"
                        keybase-url="${g.createLink(controller:'keybase', action:'keyLookup', absolute: true)}"
                        profile-url="${request.scheme}://${request.serverName}${request.serverPort ? ":" + request.serverPort : ""}${request.contextPath}/opus/{{opusCtrl.opus.shortName ? opusCtrl.opus.shortName : opusCtrl.opus.uuid}}/profile"></key-player>
                </script>
                <ng-include src="opusCtrl.keybaseTemplateUrl"/>
            </div>
        </tab>
        <tab heading="Documents" class="font-xxsmall">
            <p:help help-id="opus.documents" float="false" show="${params.isOpusEditor}"/>
            <g:render template="../common/attachments" model="[hideHeading: true]"/>
        </tab>
        <g:if test="${params.isOpusEditor}">
            <tab heading="Reports" class="font-xxsmall">
                <g:include controller="opus" action="reportPanel" params="[opusId: params.opusId]"/>
            </tab>
            <g:if test="${opus.usePrivateRecordData}">
                <tab heading="Data" class="font-xxsmall">
                    <g:render template="../opus/data"/>
                </tab>
            </g:if>
        </g:if>
    </tabset>
</div>

</body>

</html>