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

    <div class="row">
        <div class="col-md-9" ng-cloak>
            <h2 class="heading-large inline"><span
                    data-ng-bind-html="profileCtrl.formatName() | default:'Loading...'"></span></h2>
            <button class="btn btn-link fa fa-edit" ng-click="profileCtrl.editName()"
                    ng-show="!profileCtrl.readonly()">&nbsp;Edit name</button>
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
                                <a href=""
                                   ng-click="exportCtrl.exportPdf(profileCtrl.profile.rank, profileCtrl.profile.scientificName)"
                                   target="_blank"><span class="fa fa-file-pdf-o"></span>&nbsp;&nbsp;Export as PDF
                                </a>
                            </li>
                        %{--<li role="presentation">--}%
                        %{--<a href="" ng-click="profileCtrl.compareWithOtherProfile()"><span--}%
                        %{--class="fa fa-camera-retro"></span>&nbsp;&nbsp;Compare with another profile</a>--}%
                        %{--</li>--}%
                            <g:if test="${params.isOpusEditor}">
                                <li class="divider"></li>
                                <li role="presentation">
                                    <a href="${request.contextPath}/opus/{{profileCtrl.opusId}}/profile/{{profileCtrl.profile.scientificName}}/update"
                                       target="_self" ng-hide="!profileCtrl.readonly()"><span
                                            class="fa fa-edit"></span>&nbsp;&nbsp;Edit</a>
                                </li>
                                <li role="presentation"
                                    ng-if="!profileCtrl.readonly()">
                                    <a href="" ng-click="profileCtrl.toggleAudit()"><span
                                            class="fa fa-history"></span>&nbsp;&nbsp;{{profileCtrl.showProfileAudit ? 'Hide ' : 'Show '}} revision history
                                    </a>
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

    <div class="row margin-bottom-1" ng-if="profileCtrl.profile.nslProtologue" ng-cloak>
        <div class="col-md-12">
            <div class="citation zero-margin" data-ng-bind-html="profileCtrl.profile.nslProtologue"></div>
        </div>
    </div>

    <div class="row margin-bottom-1" ng-if="profileCtrl.commonNames.length > 0" ng-cloak>
        <div class="col-md-12">
            <h4 class="zero-margin">{{profileCtrl.commonNames.join(', ')}}</h4>
        </div>
    </div>

    <div class="row margin-bottom-1"
         ng-repeat="author in profileCtrl.profile.authorship | filter:{category: 'Author'}:true" ng-cloak>
        <div class="col-md-12">
            {{author.text}}
        </div>
    </div>


    <g:include controller="profile" action="editNamePanel" params="[opusId: params.opusId]"/>

    <g:include controller="profile" action="auditHistoryPanel" params="[opusId: params.opusId]"/>

    <g:if test="${!edit}">
        <g:include controller="profile" action="nomenclaturePanel" params="[opusId: params.opusId]"/>
    </g:if>

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
                            <g:if test="${edit}">
                                <g:include controller="profile" action="nomenclaturePanel" params="[opusId: params.opusId]"/>
                            </g:if>
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
                    <tab heading="Key" ng-show="profileCtrl.opus.keybaseProjectId">
                        <div key-player key-id="profileCtrl.profile.keybaseKey"
                             ng-show="profileCtrl.profile.keybaseKey"
                             keybase-url="${grailsApplication.config.keybase.key.lookup}"
                             keybase-web-url="${grailsApplication.config.keybase.web.url}"
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

    <a href="#top" du-smooth-scroll target="_self" class="font-xxsmall float-bottom-left"><span
            class="fa fa-arrow-up">&nbsp;Scroll to top</span></a>

    <!-- template for the popup displayed when Export as PDF is selected -->
    <script type="text/ng-template" id="exportPdf.html">
    <div ng-form="PDFForm">
        <div class="modal-header">
            <h4 class="modal-title">Export as PDF</h4>
        </div>

        <div class="modal-body">
            <p>
                Select the items you wish to include in the PDF.
            </p>

            <div class="row">
                <div ng-repeat="o in pdfCtrl.options | orderBy:'name'">
                    <div class="radio">
                        <label for="{{o.id}}" class="inline-label">
                            <input id="{{o.id}}" type="checkbox" name="o.name" ng-model="o.selected"
                                   ng-false-value="false">
                            {{o.name}}
                        </label>
                    </div>
                </div>

                <div class="col-md-12"
                     ng-show="(pdfCtrl.options | filter:{id: 'children'})[0].selected && pdfCtrl.childCount > pdfCtrl.ASYNC_THRESHOLD">
                    <hr class="col-md-11"/>

                    <p>Producing this PDF may take some time. Please enter your email address, and you will be notified when the file is ready for download.</p>

                    <div class="form-group">
                        <label for="email">Email address</label>
                        <input id="email" type="text" class="form-control" ng-required="true" ng-model="pdfCtrl.email"
                               required="true">
                    </div>
                </div>
            </div>
        </div>

        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="pdfCtrl.ok()"
                    ng-disabled="((pdfCtrl.options | filter:{id: 'children'})[0].selected && PDFForm.$invalid && pdfCtrl.childCount > pdfCtrl.ASYNC_THRESHOLD) || pdfCtrl.loading">
                <i class="fa fa-spin fa-spinner" ng-show="pdfCtrl.loading">&nbsp;&nbsp;</i>OK</button>
            <button class="btn btn-default" ng-click="pdfCtrl.cancel()">Cancel</button>
        </div>
    </div>
    </script>

    <script type="text/ng-template" id="profileComparisonPopup.html">
    <div class="modal-header">
        <h4 class="modal-title">Comparison</h4>
    </div>

    <div class="modal-body">
            <div class="form-inline padding-bottom-2">
                <div class="form-group">
                    <label for="comparison" class="control-label">Select the profile to compare to</label>

                        <input id="comparison" type="text"
                               class="form-control" size="50"
                               autocomplete="off" value="bla"
                               ng-model="compareCtrl.right.scientificName" typeahead-editable="false"
                               typeahead="profile.uuid as profile.scientificName for profile in compareCtrl.search($viewValue) | filter:$viewValue | limitTo:10"
                               typeahead-on-select="compareCtrl.selectProfile($item)"/>
                    </div>
            </div>

        <div ng-if="compareCtrl.loading""><span class="fa fa-spin fa-spinner"></span>&nbsp;Loading...</div>

        <div ng-if="compareCtrl.right">
            <profile-comparison left="compareCtrl.left"
                                left-title="{{compareCtrl.left.scientificName}}"
                                right="compareCtrl.right"
                                right-title="{{compareCtrl.right.scientificName}}"
                                show-everything="true"></profile-comparison>
        </div>
    </div>

    <div class="modal-footer">
        <button class="btn btn-default" ng-click="compareCtrl.close()">Close</button>
    </div>
    </script>

</div>
</body>

</html>



