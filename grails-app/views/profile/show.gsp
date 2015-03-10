<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${profile.scientificName} | ${profile.opusName}</title>

    <r:require module="profiles"/>
</head>

<body>

<div id="container" ng-app="profileEditor">
    <ol class="breadcrumb" role="navigation">
        <li><i class="fa fa-arrow-left"></i></span><span class="divider"><g:link mapping="viewOpus" params="[opusId:opus.uuid]" target="_self">Return to ${profile.opusName}</g:link></li>
    </ol>

    <div class="pull-right" style="margin-top:20px;">

        <g:if test="${!edit}">
            <g:link class="btn btn" mapping="editProfile" params="[profileId: profile.uuid]" target="_self"><i
                    class="icon-edit"></i>&nbsp;Edit</g:link>
        </g:if>
        <g:else>
            <button class="btn"
                    onclick="alert('Not implemented - through to users edits')">Logged in: ${currentUser}</button>
            <g:link class="btn" mapping="viewProfile" params="[profileId: profile.uuid]"
                    target="_self">Public view</g:link>
        </g:else>
        <g:link class="btn" mapping="getProfile" params="[profileId: profile.uuid]" target="_self">JSON</g:link>
    </div>

    <h1>${profile.scientificName ?: 'empty'}</h1>

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