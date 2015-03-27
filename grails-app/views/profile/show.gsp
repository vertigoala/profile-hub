<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile</title>

    <r:require module="profiles"/>
</head>

<body>

<div id="container" ng-app="profileEditor" ng-controller="ProfileController as profileCtrl" ng-init="profileCtrl.loadProfile()">
    <div class="row-fluid" ng-cloak>
        <div class="span6">
            <ol class="breadcrumb" role="navigation">
                <li><i class="fa fa-arrow-left"></i><span class="divider"/><a href="${request.contextPath}/opus/{{profileCtrl.opus.uuid}}" target="_self">Return to {{profileCtrl.opus.title}}</a>
            </ol>
        </div>
        <g:render template="../layouts/login"/>
    </div>

    <div class="row-fluid" ng-cloak>
        <div class="span8">
            <h1>{{profileCtrl.profile.scientificName | default:"New Profile"}}</h1>
        </div>
        <div class="span4">
            <div class="pull-right vertical-pad">
                <g:if test="${params.isOpusEditor}">
                    <a href="${request.contextPath}/opus/{{profileCtrl.opusId}}/profile/{{profileCtrl.profileId}}/update" target="_self" class="btn btn-warning" ng-hide="!profileCtrl.readonly()"><i class="icon-edit icon-white"></i> Edit</a>
                </g:if>
                <a href="${request.contextPath}/opus/{{profileCtrl.opusId}}/profile/{{profileCtrl.profileId}}" target="_self" class="btn btn-success" ng-show="!profileCtrl.readonly()"><i class="icon-eye-open icon-white"></i> Public View</a>

                <a href="${request.contextPath}/opus/{{profileCtrl.opusId}}/profile/{{profileCtrl.profileId}}/json" target="_self" class="btn btn-info">JSON</a>

                <g:if test="${params.isOpusAdmin}">
                    <button class="btn btn-danger" ng-click="profileCtrl.deleteProfile()" target="_self" ng-hide="profileCtrl.readonly() || !profileCtrl.profileId"><i class="icon-remove icon-white"></i> Delete this profile</button>
                </g:if>
            </div>
        </div>
    </div>


    <div class="row-fluid">

        <div class="span8">
            <div ng-show="messages.length" ng-cloak>
                <alert ng-repeat="message in messages" type="{{message.type}}">{{message.msg}}</alert>
            </div>

            <g:include controller="profile" action="attributesPanel" params="[opusId: params.opusId]"/>
            <g:include controller="profile" action="linksPanel" params="[opusId: params.opusId]"/>
            <g:include controller="profile" action="bhlLinksPanel" params="[opusId: params.opusId]"/>
            <g:include controller="profile" action="classificationPanel" params="[opusId: params.opusId]"/>
            <g:include controller="profile" action="taxonPanel" params="[opusId: params.opusId]"/>
            <g:include controller="profile" action="imagesPanel" params="[opusId: params.opusId]"/>
        </div>

        <div class="span4">
            <g:include controller="profile" action="mapPanel" params="[opusId: params.opusId]"/>
            <g:include controller="profile" action="listsPanel" params="[opusId: params.opusId]"/>
        </div>
    </div>
</div>
</body>

</html>