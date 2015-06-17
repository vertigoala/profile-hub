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

    <div class="btn-group padding-bottom-1 pull-right">
        <div class="row">
            <g:if test="${params.isOpusEditor}">
                <button ng-controller="ProfileController as profileCtrl" class="btn btn-default"
                        ng-click="profileCtrl.createProfile(opusCtrl.opusId)"><i class="fa fa-plus"></i> Add new profile</button>
            </g:if>
            <g:if test="${params.isOpusAdmin}">
                <a href="${request.contextPath}/opus/{{opusCtrl.opusId}}/update" target="_self"
                   class="btn btn-default" ng-hide="!config.readonly"><i class="fa fa-edit"></i> Edit configuration</a>
            </g:if>
        </div>
    </div>

    <tabset>
        <tab heading="Browse" class="font-xxsmall">
            <g:include controller="opus" action="browsePanel" params="[opusId: params.opusId]"/>
        </tab>
        <tab heading="Identify" class="font-xxsmall" ng-show="opusCtrl.opus.keybaseKeyId">
            <alert type="warning"
                   ng-show="!opusCtrl.opus.keybaseKeyId">No key has been configured for this collection.</alert>

            <div key-player key-id="opusCtrl.opus.keybaseKeyId"
                 ng-show="opusCtrl.opus.keybaseKeyId"
                 keybase-url="${grailsApplication.config.keybase.key.lookup}"
                 profile-url="http://${request.serverName}${request.serverPort ? ":" + request.serverPort : ""}${request.contextPath}/opus/{{opusCtrl.opus.shortName ? opusCtrl.opus.shortName : opusCtrl.opus.uuid}}/profile"></div>
        </tab>
    </tabset>

    <script type="text/ng-template" id="createProfile.html">
    <div class="modal-header">
        <h4 class="modal-title">Create a new profile</h4>
    </div>

    <div class="modal-body">
        <alert type="danger" class="error" ng-show="createProfileCtrl.error">{{createProfileCtrl.error}}</alert>

        <div class="form-horizontal">
            <div class="form-group">
                <label for="scientificName" class="col-sm-3 control-label">Scientific Name</label>

                <div class="col-sm-8">
                    <input id="scientificName" type="text" ng-model="createProfileCtrl.scientificName" class="form-control"
                           required ng-enter="createProfileCtrl.ok()" placeholder="e.g Acacia abbatiana"/>
                </div>
            </div>
        </div>

        <div class="form-horizontal">
            <div class="form-group">
                <label for="nameAuthor" class="col-sm-3 control-label">Author</label>

                <div class="col-sm-8">
                    <input id="nameAuthor" type="text" ng-model="createProfileCtrl.nameAuthor" class="form-control"
                           ng-enter="createProfileCtrl.ok()" placeholder="e.g. Pedley"/>
                </div>
            </div>
        </div>
    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" ng-click="createProfileCtrl.ok()"
                ng-disabled="!createProfileCtrl.scientificName">OK</button>
        <button class="btn btn-default" ng-click="createProfileCtrl.cancel()">Cancel</button>
    </div>
    </script>
</div>

</body>

</html>