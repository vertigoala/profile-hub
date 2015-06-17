<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile</title>

    <r:require module="profiles"/>

</head>

<body>

<div id="container" ng-controller="ProfileController as profileCtrl"
     ng-init="profileCtrl.loadProfile()">
    <a name="top"></a>

    <ol class="breadcrumb" ng-cloak>
        <li><a class="font-xxsmall" href="${request.contextPath}/">Profile Collections</a></li>
        <li><a class="font-xxsmall"
               href="${request.contextPath}/opus/{{profileCtrl.opus.shortName ? profileCtrl.opus.shortName : profileCtrl.opus.uuid}}">{{profileCtrl.opus.title}}</a>
        </li>
        <li class="font-xxsmall active">{{profileCtrl.profile.scientificName}}</li>
    </ol>

    <div ng-show="messages.length" ng-cloak>
        <alert ng-repeat="message in messages" type="{{message.type}}">{{message.msg}}</alert>
    </div>

    <div class="row" ng-cloak>
        <alert type="warning"
               ng-if="profileCtrl.profile.privateMode"><span
                class="fa fa-lock"></span>&nbsp;&nbsp;You are viewing a profile that is currently in draft. These changes will not be visible to public users until the profile is completed and the draft is released.
        </alert>
    </div>

    <div class="row margin-bottom-1">
        <div class="col-md-9" ng-cloak>
            <h2 class="heading-large inline"><span
                    class="scientific-name">{{profileCtrl.profile.scientificName | default:"Loading..."}}</span> <span
                    class="inline-sub-heading">{{profileCtrl.profile.nameAuthor}}</span></h2>
        </div>

        <div class="col-md-3" ng-cloak>
            <div class="btn-group pull-right">
                <div class="col-md-6">
                    <a href="${request.contextPath}/opus/{{profileCtrl.opusId}}/profile/{{profileCtrl.profile.scientificName}}"
                       target="_self" class="btn btn-default" ng-show="!profileCtrl.readonly()"><i
                            class="fa fa-eye"></i> Public View</a>
                </div>

                <div class="btn-group col-md-6">
                    <div class="dropdown">
                        <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1"
                                data-toggle="dropdown" aria-expanded="true">
                            Options
                            <span class="fa fa-angle-double-down"></span>
                        </button>

                        <ul class="dropdown-menu dropdown-menu-right"
                            role="menu"
                            aria-labelledby="optionsDropdown">
                            <li role="presentation">
                                <a href="${request.contextPath}/opus/{{profileCtrl.opusId}}/profile/{{profileCtrl.profileId}}/json"
                                   target="_blank"><span class="fa fa-file-text-o"></span>&nbsp;&nbsp;Export as JSON
                                </a>
                            </li>
                            <li role="presentation" ng-controller="ExportController as exportCtrl">
                                <a href="" ng-click="exportCtrl.exportPdf()"
                                   target="_blank"><span class="fa fa-file-pdf-o"></span>&nbsp;&nbsp;Export as PDF
                                </a>
                            </li>
                            <g:if test="${params.isOpusEditor}">
                                <li class="divider"></li>
                                <li role="presentation">
                                    <a href="${request.contextPath}/opus/{{profileCtrl.opusId}}/profile/{{profileCtrl.profile.scientificName}}/update"
                                       target="_self" ng-hide="!profileCtrl.readonly()"><span
                                            class="fa fa-edit"></span>&nbsp;&nbsp;Edit</a>
                                </li>
                                <li role="presentation"
                                    ng-if="!profileCtrl.readonly() && !profileCtrl.profile.privateMode">
                                    <a href="" ng-click="profileCtrl.toggleDraftMode()"><span
                                            class="fa fa-lock"></span>&nbsp;&nbsp;Lock for major revision</a>
                                </li>
                                <li role="presentation"
                                    ng-if="!profileCtrl.readonly() && profileCtrl.profile.privateMode">
                                    <a href="" ng-click="profileCtrl.toggleDraftMode()"><span
                                            class="fa fa-unlock"></span>&nbsp;&nbsp;Publish draft changes</a>
                                </li>
                                <li role="presentation"
                                    ng-if="!profileCtrl.readonly() && profileCtrl.profile.privateMode">
                                    <a href="" ng-click="profileCtrl.discardDraftChanges()"><span
                                            class="fa fa-times-circle"></span>&nbsp;&nbsp;Discard draft changes</a>
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
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="row margin-bottom-1" ng-if="profileCtrl.commonNames.length > 0" ng-cloak>
        <div class="col-md-12">
            Commonly known as {{profileCtrl.commonNames.join(', ')}}
        </div>
    </div>

    <div class="row margin-bottom-1" ng-repeat="author in profileCtrl.profile.authorship | filter:{category: 'Author'}:true" ng-cloak>
        <div class="col-md-12">
            {{author.text}}
        </div>
    </div>

    <div class="row margin-bottom-1" ng-cloak>
        <g:include controller="profile" action="mapPanel" params="[opusId: params.opusId]"/>
    </div>

    <g:if test="${!profile.privateMode || (params.currentUser && params.isOpusReviewer)}">
        <div class="row">
            <div class="col-md-12" ng-cloak>
                <tabset>
                    <tab heading="Details">
                        <div class="col-md-2 margin-bottom-1">
                            <ul class="nav nav-stacked" id="sidebar" ng-cloak>
                                <h4 class="font-xxsmall heading-underlined"><strong>Page index</strong></h4>
                                <li ng-repeat="item in nav | orderBy:'label'">
                                    <a du-smooth-scroll="{{profileCtrl.readonly() ? 'view_' : 'edit_'}}{{item.key}}"
                                       target="_self" class="font-xxsmall">{{item.label}}</a>
                                </li>
                            </ul>
                        </div>

                        <div class="col-md-10">
                            <g:include controller="profile" action="attributesPanel" params="[opusId: params.opusId]"/>
                            <g:include controller="profile" action="linksPanel" params="[opusId: params.opusId]"/>
                            <g:include controller="profile" action="bhlLinksPanel" params="[opusId: params.opusId]"/>
                            <g:include controller="profile" action="specimenPanel" params="[opusId: params.opusId]"/>
                            <g:include controller="profile" action="classificationPanel"
                                       params="[opusId: params.opusId]"/>
                            <g:include controller="profile" action="bibliographyPanel"
                                       params="[opusId: params.opusId]"/>
                            <g:include controller="profile" action="listsPanel" params="[opusId: params.opusId]"/>
                            <g:include controller="profile" action="taxonPanel" params="[opusId: params.opusId]"/>
                            <g:include controller="profile" action="imagesPanel" params="[opusId: params.opusId]"/>
                            <g:include controller="profile" action="publicationsPanel"
                                       params="[opusId: params.opusId]"/>
                            <g:include controller="profile" action="authorPanel" params="[opusId: params.opusId]"/>
                            <g:if test="${params.isOpusReviewer}">
                                <g:include controller="profile" action="commentsPanel"
                                           params="[opusId: params.opusId]"/>
                            </g:if>
                        </div>

                    </tab>
                    <tab heading="Nomenclature">
                        <ng-include src="profileCtrl.nslUrl"
                        ng-if="profileCtrl.profile.nslNameIdentifier">Loading...</ng-include>
                        <alert type="warning"
                               ng-if="!profileCtrl.profile.nslNameIdentifier">No matching name was found at <a
                                href="https://biodiversity.org.au/nsl/services/">https://biodiversity.org.au/nsl/services/</a> for this profile
                        </alert>
                    </tab>
                    <tab heading="Key" ng-show="profileCtrl.opus.keybaseProjectId">
                        <div key-player key-id="profileCtrl.profile.keybaseKey"
                             ng-show="profileCtrl.profile.keybaseKey"
                             keybase-url="${grailsApplication.config.keybase.key.lookup}"
                             profile-url="http://${request.serverName}${request.serverPort ? ":" + request.serverPort : ""}${request.contextPath}/opus/{{profileCtrl.opus.shortName ? profileCtrl.opus.shortName : profileCtrl.opus.uuid}}/profile">></div>
                        <alert type="warning"
                               ng-show="!profileCtrl.profile.keybaseKey">There is no key available for {{profileCtrl.profile.scientificName}}.</alert>
                    </tab>
                </tabset>
            </div>
        </div>
    </g:if>

    <div class="row margin-top-1" ng-if="profileCtrl.opus.copyrightText">
        <div class="col-md-12">
            <div ta-bind ng-model="profileCtrl.opus.copyrightText" class="small text-center"></div>
        </div>
    </div>

    <a href="#top" du-smooth-scroll target="_self" class="font-xxsmall float-bottom-left"><span class="fa fa-arrow-up">&nbsp;Scroll to top</span></a>

    <!-- template for the popup displayed when Export as PDF is selected -->
    <script type="text/ng-template" id="exportPdf.html">
    <div class="modal-header">
        <h4 class="modal-title">Export as PDF</h4>
    </div>

    <div class="modal-body">
        <p>
            Select the items you wish to include in the PDF.
        </p>

        <div ng-repeat="o in pdfCtrl.options | orderBy:'name'">
            <div class="radio"></div>
            <label for="{{o.id}}" class="inline-label">
                <input id="{{o.id}}" type="checkbox" name="o.name" ng-model="o.selected" ng-false-value="false">
                {{o.name}}
            </label>
        </div>
    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" ng-click="pdfCtrl.ok()">OK</button>
        <button class="btn btn-default" ng-click="pdfCtrl.cancel()">Cancel</button>
    </div>
    </script>
</div>
</body>

</html>


