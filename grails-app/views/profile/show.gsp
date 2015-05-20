<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile</title>

    <r:require module="profiles"/>

</head>

<body>

<div id="container" ng-app="profileEditor" ng-controller="ProfileController as profileCtrl"
     ng-init="profileCtrl.loadProfile()">
    <div class="row-fluid" ng-cloak>
        <div class="span6">
            <ol class="breadcrumb" role="navigation">
                <li><i class="fa fa-arrow-left"></i><span class="divider"/><a
                        href="${request.contextPath}/opus/{{profileCtrl.opus.shortName ? profileCtrl.opus.shortName : profileCtrl.opus.uuid}}"
                        target="_self">Return to {{profileCtrl.opus.title}}</a>
            </ol>
        </div>
        <g:render template="../layouts/login"/>
    </div>

    <div class="row-fluid" ng-cloak>
        <alert type="warning"
               ng-if="profileCtrl.profile.privateMode">This profile is not available for public users.</alert>
    </div>

    <div class="row-fluid" ng-cloak>
        <div class="span8">
            <h1>{{profileCtrl.profile.scientificName | default:"Loading..."}} <span class="inline-sub-heading">{{profileCtrl.profile.nameAuthor}}</span></h1>
            <g:if test="${!profile.privateMode || (params.currentUser && params.isOpusReviewer)}">
                <div ng-repeat="author in profileCtrl.profile.authorship | filter:{category: 'Author'}:true">
                    <i>By {{author.text}}</i>
                </div>
                <g:if test="${grailsApplication.config.feature.publication == 'true'}">
                    <div ng-controller="PublicationController as pubCtrl" ng-show="pubCtrl.mostRecentPublication()">
                        Based on <i>{{pubCtrl.mostRecentPublication().title}}</i> by {{pubCtrl.mostRecentPublication().authors}} ({{pubCtrl.mostRecentPublication().publicationDate | date:"dd/MM/yyyy"}})
                    </div>
                </g:if>
            </g:if>
        </div>

        <g:if test="${!profile.privateMode || (params.currentUser && params.isOpusReviewer)}">
            <div class="span4" ng-cloak>
                <div class="pull-right vertical-pad">
                    <a href="${request.contextPath}/opus/{{profileCtrl.opusId}}/profile/{{profileCtrl.profile.scientificName}}"
                       target="_self" class="btn btn-success" ng-show="!profileCtrl.readonly()"><i
                            class="icon-eye-open icon-white"></i> Public View</a>

                    <span class="dropdown">
                        <a class="dropdown-toggle btn btn-info"
                           id="optionsDropdown"
                           role="button"
                           data-toggle="dropdown"
                           href="#"><span class="fa fa-angle-double-down"></span> Options</a>
                        <ul class="dropdown-menu dropdown-menu-right"
                            role="menu"
                            aria-labelledby="optionsDropdown">
                            <li role="presentation">
                                <a href="${request.contextPath}/opus/{{profileCtrl.opusId}}/profile/{{profileCtrl.profileId}}/json"
                                   target="_blank"><span class="fa fa-file-text-o"></span>&nbsp;&nbsp;Export as JSON</a>
                            </li>
                            <li role="presentation" ng-controller="ExportController as exportCtrl">
                                <a href="" ng-click="exportCtrl.exportPdf()"
                                   target="_blank"><span class="fa fa-file-pdf-o"></span>&nbsp;&nbsp;Export as PDF</a>
                            </li>
                            <g:if test="${params.isOpusEditor}">
                                <li class="divider"></li>
                                <li role="presentation">
                                    <a href="${request.contextPath}/opus/{{profileCtrl.opusId}}/profile/{{profileCtrl.profile.scientificName}}/update"
                                       target="_self" ng-hide="!profileCtrl.readonly()"><span
                                            class="fa fa-edit"></span>&nbsp;&nbsp;Edit</a>
                                </li>
                                <li role="presentation" style="margin-left: 20px" ng-if="!profileCtrl.readonly()">
                                    Draft Mode
                                    <div class="btn-group">
                                        <label class="btn btn-mini"
                                               ng-class="profileCtrl.profile.privateMode ? 'btn-warning' : ''"
                                               ng-model="profileCtrl.profile.privateMode" btn-radio="true"
                                               ng-change="profileCtrl.saveProfile()">On</label>
                                        <label class="btn btn-mini"
                                               ng-class="profileCtrl.profile.privateMode ? '' : 'btn-success'"
                                               ng-model="profileCtrl.profile.privateMode" btn-radio="false"
                                               ng-change="profileCtrl.saveProfile()">Off</label>
                                    </div>
                                </li>
                            </g:if>
                            <g:if test="${params.isOpusAdmin}">
                                <li class="divider" ng-hide="profileCtrl.readonly()"></li>
                                <li role="presentation">
                                    <a href="" ng-click="profileCtrl.deleteProfile()" target="_self"
                                       ng-hide="profileCtrl.readonly() || !profileCtrl.profileId"><span
                                            class="fa fa-trash-o"></span>&nbsp;&nbsp;Delete this profile</a>
                                </li>
                            </g:if>
                        </ul>
                    </span>
                </div>
            </div>
        </g:if>
    </div>


    <g:if test="${!profile.privateMode || (params.currentUser && params.isOpusReviewer)}">
        <div class="row-fluid">
            <div class="span8" ng-cloak>
                <tabset>
                    <tab heading="Details">
                        <div ng-show="messages.length" ng-cloak>
                            <alert ng-repeat="message in messages" type="{{message.type}}">{{message.msg}}</alert>
                        </div>
                        <g:include controller="profile" action="attributesPanel" params="[opusId: params.opusId]"/>
                        <g:include controller="profile" action="linksPanel" params="[opusId: params.opusId]"/>
                        <g:include controller="profile" action="bhlLinksPanel" params="[opusId: params.opusId]"/>
                        <g:include controller="profile" action="specimenPanel" params="[opusId: params.opusId]"/>
                        <g:include controller="profile" action="classificationPanel" params="[opusId: params.opusId]"/>
                        <g:include controller="profile" action="taxonPanel" params="[opusId: params.opusId]"/>
                        <g:include controller="profile" action="bibliographyPanel" params="[opusId: params.opusId]"/>
                        <g:include controller="profile" action="imagesPanel" params="[opusId: params.opusId]"/>
                        <g:if test="${params.isOpusReviewer}">
                            <g:include controller="profile" action="commentsPanel" params="[opusId: params.opusId]"/>
                        </g:if>
                    </tab>
                    <tab heading="Nomenclature">
                        <ng-include src="profileCtrl.nslUrl" ng-if="profileCtrl.profile.nslNameIdentifier">Loading...</ng-include>
                        <alert type="warning" ng-if="!profileCtrl.profile.nslNameIdentifier">No matching name was found at <a href="https://biodiversity.org.au/nsl/services/">https://biodiversity.org.au/nsl/services/</a> for this profile</alert>
                    </tab>
                    <tab heading="Key" ng-show="profileCtrl.opus.keybaseProjectId">
                        <div key-player key-id="profileCtrl.profile.keybaseKey"
                             ng-show="profileCtrl.profile.keybaseKey"
                             keybase-url="${grailsApplication.config.keybase.key.lookup}"
                             profile-url="http://${request.serverName}${request.serverPort ? ":" + request.serverPort : ""}${request.contextPath}/opus/{{profileCtrl.opus.shortName ? profileCtrl.opus.shortName : profileCtrl.opus.uuid}}/profile">></div>
                        <alert type="warning" ng-show="!profileCtrl.profile.keybaseKey">There is no key available for {{profileCtrl.profile.scientificName}}.</alert>
                    </tab>
            </div>

            <div class="span4">
                <g:include controller="profile" action="mapPanel" params="[opusId: params.opusId]"/>
                <g:if test="${grailsApplication.config.feature.publication == 'true'}">
                    <g:include controller="profile" action="publicationsPanel" params="[opusId: params.opusId]"/>
                </g:if>
                <g:include controller="profile" action="listsPanel" params="[opusId: params.opusId]"/>
                <g:include controller="profile" action="authorPanel" params="[opusId: params.opusId]"/>
            </div>
        </div>
        </tabset>

    </g:if>

<!-- template for the popup displayed when Export as PDF is selected -->
<script type="text/ng-template" id="exportPdf.html">
    <div class="modal-header">
        <h3 class="modal-title">Export as PDF</h3>
    </div>

    <div class="modal-body">
        <p>
            Select the items you wish to include in the PDF.
        </p>

        <div ng-repeat="o in pdfCtrl.options | orderBy:'name'">
            <label for="{{o.id}}" class="inline-label">
                <input id="{{o.id}}" type="checkbox" name="o.name" ng-model="o.selected" ng-false-value="false">
                {{o.name}}
            </label>
        </div>
    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" ng-click="pdfCtrl.ok()" >OK</button>
        <button class="btn btn-warning" ng-click="pdfCtrl.cancel()">Cancel</button>
    </div>
    </script>
</div>
</body>

</html>


