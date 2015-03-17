<%@ page import="au.org.ala.web.AuthService" %>
<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${opus.title} | Profile collections</title>
    <script type="text/javascript"
            src="http://markusslima.github.io/bootstrap-filestyle/1.0.6/js/bootstrap-filestyle.min.js"></script>
    <style type="text/css">
    .bootstrap-filestyle label {
        margin-bottom: 8px;
    }
    </style>
</head>

<body>

<div ng-app="profileEditor" ng-controller="OpusController as opusCtrl">

    <div class="pull-right">
        <button class="btn"
                onclick="javascript:alert('Not implemented - through to users edits')">Logged in: ${currentUser}</button>
        <a href="${request.contextPath}/opus/{{opusCtrl.opus.uuid}}" class="btn" target="_self" ng-show="opusCtrl.opus.uuid">Public View</a>
    </div>

    <div style="margin-top:20px;">
        <p class="lead">
            Configure your profile collection, uploading existing datasets to be incorporated in your profile.
        </p>
    </div>

    <div ng-show="messages.length">
        <alert ng-repeat="message in messages" type="{{message.type}}">{{message.msg}}</alert>
    </div>

    <g:include controller="opus" action="editOpusDetailsPanel"/>

    <g:include controller="opus" action="editAccessControlPanel"/>

    <g:include controller="opus" action="editStylingPanel"/>

    <g:include controller="opus" action="editMapConfigPanel"/>

    <g:include controller="opus" action="taxaUploadPanel"/>

    <g:include controller="opus" action="occurrenceUploadPanel"/>

    <g:include controller="opus" action="phyloUploadPanel"/>

    <g:include controller="opus" action="keyUploadPanel"/>

    <g:include controller="opus" action="editImageSourcesPanel"/>

    <g:include controller="opus" action="editRecordSourcesPanel"/>

    <g:include controller="opus" action="editVocabPanel"/>

</div>

</body>

</html>