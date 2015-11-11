<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
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
            <button ng-controller="ProfileController as profileCtrl" class="btn btn-default"
                    ng-click="profileCtrl.createProfile(opusCtrl.opusId)"><i class="fa fa-plus"></i> Add new profile
            </button>
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
        <tab heading="Identify" class="font-xxsmall" ng-show="opusCtrl.opus.keybaseKeyId">
            <alert type="warning"
                   ng-show="!opusCtrl.opus.keybaseKeyId">No key has been configured for this collection.</alert>

            <div key-player key-id="opusCtrl.opus.keybaseKeyId"
                 ng-show="opusCtrl.opus.keybaseKeyId"
                 keybase-url="${grailsApplication.config.keybase.key.lookup}"
                 keybase-web-url="${grailsApplication.config.keybase.web.url}"
                 profile-url="http://${request.serverName}${request.serverPort ? ":" + request.serverPort : ""}${request.contextPath}/opus/{{opusCtrl.opus.shortName ? opusCtrl.opus.shortName : opusCtrl.opus.uuid}}/profile"></div>
        </tab>
        <g:if test="${params.isOpusEditor}">
            <tab heading="Reports" class="font-xxsmall">
                <g:include controller="opus" action="reportPanel" params="[opusId: params.opusId]"/>
            </tab>
        </g:if>
    </tabset>

    <script type="text/ng-template" id="createProfile.html">
    <div class="modal-header">
        <h4 class="modal-title">Create a new profile</h4>
    </div>

    <div class="modal-body">
        <alert type="danger" class="error" ng-repeat="error in createProfileCtrl.errors">{{error}}</alert>

        <profile-name name="createProfileCtrl.scientificName" valid="createProfileCtrl.validName" manually-matched-guid="createProfileCtrl.manuallyMatchedGuid" mode="create"></profile-name>

        <div class="modal-footer">

            <button class="btn btn-primary" ng-click="createProfileCtrl.ok()"
                    ng-disabled="!createProfileCtrl.validName">Create profile</button>
            <button class="btn btn-default" ng-click="createProfileCtrl.cancel()">Cancel</button>
        </div>
    </div>
    </script>
</div>

</body>

</html>