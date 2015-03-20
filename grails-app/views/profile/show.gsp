<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${profile?.scientificName ?: "New Profile"} | ${profile?.opusName}</title>

    <r:require module="profiles"/>
</head>

<body>

<div id="container" ng-app="profileEditor" ng-controller="ProfileController as profileCtrl">
    <div class="row-fluid">
        <div class="span6">
            <ol class="breadcrumb" role="navigation">
                <li><i class="fa fa-arrow-left"></i><span class="divider"/><a href="${request.contextPath}/opus/{{profileCtrl.opus.uuid}}" target="_self">Return to {{profileCtrl.opus.title}}</a>
            </ol>
        </div>
        <div class="span6">
            <div class="pull-right">
                <a href="#"onclick="javascript:alert('Not implemented - through to users edits')" ng-hide="!config.currentUser">Logged in: {{config.currentUser}}</a>
            </div>
        </div>
    </div>

    <div class="row-fluid">
        <div class="span8">
            <h1>{{profileCtrl.profile.scientificName | default:"New Profile"}}</h1>
        </div>
        <div class="span4">
            <div class="pull-right vertical-pad">
                <a href="${request.contextPath}/profile/edit/{{profileCtrl.profileId}}" target="_self" class="btn btn-warning" ng-hide="!profileCtrl.readonly()"><i class="icon-edit icon-white"></i> Edit</a>

                <a href="${request.contextPath}/profile/{{profileCtrl.profileId}}" target="_self" class="btn btn-success" ng-show="!profileCtrl.readonly()"><i class="icon-eye-open icon-white"></i> Public View</a>

                <a href="${request.contextPath}/profile/json/{{profileCtrl.profileId}}" target="_self" class="btn btn-info">JSON</a>

                <button class="btn btn-danger" ng-click="profileCtrl.deleteProfile()" target="_self" ng-hide="profileCtrl.readonly() || !profileCtrl.profileId"><i class="icon-remove icon-white"></i> Delete this profile</button>
            </div>
        </div>
    </div>


    <div class="row-fluid">

        <div class="span8">
            <div ng-show="messages.length">
                <alert ng-repeat="message in messages" type="{{message.type}}">{{message.msg}}</alert>
            </div>

            <g:include controller="profile" action="attributesPanel"/>
            <g:include controller="profile" action="linksPanel"/>
            <g:include controller="profile" action="bhlLinksPanel"/>
            <g:include controller="profile" action="classificationPanel"/>
            <g:include controller="profile" action="taxonPanel"/>
            <g:include controller="profile" action="imagesPanel"/>
        </div>

        <div class="span4">
            <g:include controller="profile" action="mapPanel"/>
            <g:include controller="profile" action="listsPanel"/>
        </div>
    </div>
</div>
</body>

</html>