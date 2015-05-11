<%@ page import="au.org.ala.web.AuthService" %>
<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile collections</title>
    <script type="text/javascript"
            src="http://markusslima.github.io/bootstrap-filestyle/1.0.6/js/bootstrap-filestyle.min.js"></script>
    <style type="text/css">
    .bootstrap-filestyle label {
        margin-bottom: 8px;
    }
    </style>
</head>

<body>

<div ng-app="profileEditor" ng-controller="OpusController as opusCtrl" ng-init="opusCtrl.loadOpus()">
    <div class="row-fluid" ng-cloak>
        <div class="span6">
            <ol class="breadcrumb" role="navigation">
                <li><i class="fa fa-arrow-left"></i><span class="divider"></span><a href="${request.contextPath}/" target="_self">View all profile collections</a></li>
            </ol>
        </div>
        <g:render template="../layouts/login"/>
    </div>

    <div class="row-fluid" ng-cloak>
        <div class="span8">
            <div style="margin-top:20px;">
                <p class="lead">
                    Configure your profile collection, uploading existing datasets to be incorporated in your profile.
                </p>
            </div>
        </div>
        <div class="span4" ng-cloak>
            <div class="pull-right">
                <a href="${request.contextPath}/opus/{{opusCtrl.opus.shortName ? opusCtrl.opus.shortName : opusCtrl.opus.uuid}}" class="btn btn-success" target="_self" ng-show="opusCtrl.opus.uuid"><i class="icon-eye-open icon-white"></i> Public View</a>
                <g:if test="${params.isOpusAdmin}">
                    <button ng-click="opusCtrl.deleteOpus()" class="btn btn-danger" target="_self" ng-show="opusCtrl.opus.uuid"><i class="icon-remove icon-white"></i> Delete this collection</button>
                </g:if>
            </div>
        </div>
    </div>

    <div ng-show="messages.length" ng-cloak>
        <alert ng-repeat="message in messages" type="{{message.type}}">{{message.msg}}</alert>
    </div>

    <g:include controller="opus" action="editOpusDetailsPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="editAccessControlPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="editStylingPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="editMapConfigPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="taxaUploadPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="occurrenceUploadPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="phyloUploadPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="editKeyConfigPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="keyUploadPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="editImageSourcesPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="editRecordSourcesPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="editApprovedListsPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="editSupportingOpusPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="editVocabPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="editAuthorshipPanel" params="[opusId: params.opusId]"/>

    <g:include controller="opus" action="editGlossaryPanel" params="[opusId: params.opusId]"/>
</div>

</body>

</html>