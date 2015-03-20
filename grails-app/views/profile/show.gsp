<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${profile?.scientificName ?: "New Profile"} | ${profile?.opusName}</title>

    <r:require module="profiles"/>
</head>

<body>

<div id="container" ng-app="profileEditor" ng-controller="ProfileController as profileCtrl">
    <ol class="breadcrumb" role="navigation">
        <li><i class="fa fa-arrow-left"></i><span class="divider"/><a href="${request.contextPath}/opus/{{profileCtrl.opus.uuid}}" target="_self">Return to {{profileCtrl.opus.title}}</a>
    </ol>

    <div class="row-fluid">

        <div class="span8">
            <h1>{{profileCtrl.profile.scientificName | default:"New Profile"}}<button class="btn btn-link fa fa-remove fa-2x red pull-right" style="padding-top:15px" ng-click="profileCtrl.deleteProfile()" target="_self" ng-hide="profileCtrl.readonly() || !profileCtrl.profileId"> Delete this profile</button></h1>
        </div>
        <div class="span4">
            <div class="pull-right vertical-pad">
                <a href="${request.contextPath}/profile/edit/{{profileCtrl.profileId}}" target="_self" class="btn" ng-hide="!profileCtrl.readonly()"><i class="icon-edit"></i> Edit</a>

                <button class="btn" ng-hide="profileCtrl.readonly()" onclick="alert('Not implemented - through to users edits')">Logged in: ${currentUser}</button>

                <a href="${request.contextPath}/profile/{{profileCtrl.profileId}}" target="_self" class="btn" ng-show="!profileCtrl.readonly()">Public View</a>

                <a href="${request.contextPath}/profile/json/{{profileCtrl.profileId}}" target="_self" class="btn">JSON</a>
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