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
        <li class="font-xxsmall active">{{profileCtrl.isArchived() ? profileCtrl.profile.archivedWithName : profileCtrl.profile.scientificName}}</li>
    </ol>

    <div class="row" ng-cloak>
        <alert type="warning"
               ng-if="profileCtrl.profile.privateMode"><span
                class="fa fa-lock"></span>&nbsp;&nbsp;You are viewing a profile that is currently in draft. These changes will not be visible to public users until the profile is completed and the draft is released.
        </alert>

        <alert type="warning"
               ng-if="profileCtrl.isArchived()">
            <p>
                This profile has been archived with the following explanation:
            </p>
            <p class="archive-comment">
                {{profileCtrl.profile.archiveComment}}
            </p>
            <p>
                If you need to reference this profile, you can do so using the last published version:
                <div class="archived-publication">
                    <publication data="profileCtrl.profile.publications[0]" opus-id="profileCtrl.opusId" profile-id="profileCtrl.profileId">
                    </publication>
                </div>
            </p>
            <p>
                Archived profiles will not appear in search results. Use the following URL to create a direct link to this profile:
                <br/>
                http://${request.serverName}${request.serverPort ? ":" + request.serverPort : ""}${request.contextPath}/opus/{{profileCtrl.opus.uuid}}/profile/{{profileCtrl.profile.uuid}}
            </p>
        </alert>
    </div>

    <div class="row">
        <div class="col-md-9" ng-cloak>
            <h2 class="heading-large inline"><span
                    data-ng-bind-html="profileCtrl.formatName() | default:'Loading...'"></span></h2>
            <button class="btn btn-link fa fa-edit" ng-click="profileCtrl.editName()"
                    ng-show="!profileCtrl.readonly()">&nbsp;Edit name</button>
        </div>

        <div class="col-md-3 text-right" ng-cloak>
            <g:render template="optionsMenu"/>
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

    <g:if test="${!profile.archivedDate}">
        <g:include controller="profile" action="editNamePanel" params="[opusId: params.opusId]"/>

        <g:include controller="profile" action="auditHistoryPanel" params="[opusId: params.opusId]"/>

        <g:if test="${!edit}">
            <g:include controller="profile" action="nomenclaturePanel" params="[opusId: params.opusId]"/>
        </g:if>

        <div class="row margin-bottom-1" ng-cloak>
            <g:include controller="profile" action="mapPanel" params="[opusId: params.opusId]"/>
        </div>
    </g:if>

    <g:if test="${!profile.privateMode || (params.currentUser && params.isOpusReviewer)}">
        <div class="row">
            <div class="col-md-12" ng-cloak>
                <tabset ng-class="profileCtrl.opus.keybaseProjectId ? '' : 'single-tabbed-panel'">
                    <tab heading="Details">
                        <div class="row">
                            <div class="col-md-2 margin-bottom-1">
                                <ul class="nav nav-stacked" id="sidebar" ng-cloak>
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
                                <g:if test="${!profile.archivedDate}">
                                    <g:include controller="profile" action="listsPanel" params="[opusId: params.opusId]"/>
                                    <g:include controller="profile" action="taxonPanel" params="[opusId: params.opusId]"/>
                                    <g:include controller="profile" action="imagesPanel" params="[opusId: params.opusId]"/>
                                </g:if>
                                <g:include controller="profile" action="publicationsPanel"
                                           params="[opusId: params.opusId]"/>
                                <g:include controller="profile" action="authorPanel" params="[opusId: params.opusId]"/>
                                <g:if test="${params.isOpusReviewer}">
                                    <g:include controller="profile" action="commentsPanel"
                                               params="[opusId: params.opusId]"/>
                                </g:if>
                            </div>
                        </div>
                    </tab>
                    <tab heading="Key" ng-show="profileCtrl.opus.keybaseProjectId">
                        <div class="row">
                        <div key-player key-id="profileCtrl.profile.keybaseKey"
                             ng-show="profileCtrl.profile.keybaseKey"
                             keybase-url="${grailsApplication.config.keybase.key.lookup}"
                             keybase-web-url="${grailsApplication.config.keybase.web.url}"
                             profile-url="http://${request.serverName}${request.serverPort ? ":" + request.serverPort : ""}${request.contextPath}/opus/{{profileCtrl.opus.shortName ? profileCtrl.opus.shortName : profileCtrl.opus.uuid}}/profile"></div>
                            <div class="col-md-12">

                                <p class="small padding-top-1" ng-show="profileCtrl.profile.keybaseKey">The key displayed here is the closest match that could be found for the profile in the <a href="${grailsApplication.config.keybase.web.url}" target="_blank">KeyBase</a> project for this collection.
                                Matching is based on the taxonomic rank: if no key can be found for the profile's rank, then the parent rank will be used, and so on until a match is found. This matching depends on the structure of the keys in KeyBase.</p>
                            </div>
                        </div>
                        <alert type="warning"
                            ng-show="!profileCtrl.profile.keybaseKey">There is no key available for {{profileCtrl.profile.scientificName}}.</alert>
                    </tab>
                </tabset>
            </div>
        </div>
    </g:if>

    <div class="row margin-top-1">
        <div class="col-md-12 col-xs-12 col-lg-12 small text-center" ng-cloak>
            <p><span ng-show="profileCtrl.opus.copyrightText">&copy; {{ profileCtrl.opus.copyrightText }}.</span> <a href="${request.contextPath}/opus/{{profileCtrl.opus.shortName ? profileCtrl.opus.shortName : profileCtrl.opus.uuid}}/about##copyright" target="_blank">Copyright Notice</a>.</p>
        </div>
    </div>

    <a href="#top" du-smooth-scroll target="_self" class="font-xxsmall float-bottom-left"><span
            class="fa fa-arrow-up">&nbsp;Scroll to top</span></a>

    <g:render template="exportPdfPopup"/>
    <g:render template="profileComparisonPopup"/>

</div>
</body>

</html>



