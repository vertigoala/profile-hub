<div class="row">
    <div class="col-md-9" ng-cloak>
        <h2 class="heading-large inline"><span
                data-ng-bind-html="profileCtrl.formatName() | default:'Loading...' | sanitizeHtml"></span></h2>

        <div class="margin-bottom-1 inline-block small" ng-show="profileCtrl.opus">
            <a href="${grailsApplication.config.bie.base.url}/species/{{ profileCtrl.profile.guid }}"
               ng-show="profileCtrl.profile.guid" title="View this taxon in the Atlas of Living Australia"
               class="padding-left-1" target="_blank"><span class="fa fa-search">&nbsp;</span>ALA</a>
            <a href="${grailsApplication.config.nsl.base.url}services/apni-format/display/{{ profileCtrl.profile.nslNameIdentifier }}"
               ng-show="profileCtrl.profile.nslNameIdentifier"
               title="View this name in the National Species List" class="padding-left-1" target="_blank"><span
                    class="fa fa-search">&nbsp;</span>NSL <span
                    ng-show="profileCtrl.nslNameStatus">[{{profileCtrl.nslNameStatus}}]</span></a>
        </div>
    </div>

    <div class="col-md-3 text-right" ng-cloak>
        <g:render template="optionsMenu"/>
    </div>

</div>

<div class="row margin-bottom-1" ng-show="profileCtrl.nslProtologue" ng-cloak>
    <div class="col-md-12">
        <div class="citation zero-margin" data-ng-bind-html="profileCtrl.nslProtologue | sanitizeHtml"></div>
    </div>
</div>

<div class="row margin-bottom-1" ng-show="profileCtrl.profile.otherNames.length > 0" ng-cloak>
    <div class="col-md-12">
        <h4 class="zero-margin">{{profileCtrl.profile.otherNames.join(', ')}}</h4>
    </div>
</div>

<g:if test="${!profile.archivedDate}">
    <g:include controller="profile" action="nomenclaturePanel" params="[opusId: params.opusId]"/>
</g:if>

<g:if test="${!profile.privateMode || (params.currentUser && params.isOpusReviewer)}">
    <div class="row">
        <div class="col-md-12" ng-cloak>
            <tabset>
                <tab heading="Profile" class="font-xxsmall" ng-show="managedTabCtrl.hasContent()" managed-tab>
                    <div class="row" ng-controller="ImagesController as imageCtrl" ng-init="imageCtrl.init('${edit}')">
                        <div ng-class="(profileCtrl.profile.mapSnapshot || imageCtrl.primaryImage || profileCtrl.primaryAudio || profileCtrl.primaryVideo) ? 'col-md-9' : 'col-md-12'">
                            <g:render template="attributes"/>
                            <g:render template="specimens"/>
                            <g:if test="${!profile.archivedDate}">
                                <g:render template="taxon"/>
                            </g:if>
                        </div>
                        <div class="side col-md-3" ng-if="profileCtrl.profile.mapSnapshot || imageCtrl.primaryImage || profileCtrl.primaryVideo || profileCtrl.primaryAudio">
                            <g:render template="mapSnapshot" model="[size: 'small']"/>
                            <g:render template="primaryImage" model="[size: 'small', hideViewAll:true]"/>
                            <div ng-if="profileCtrl.primaryVideo" embed selected-multimedia="profileCtrl.primaryVideo"></div>
                            <div ng-if="profileCtrl.primaryAudio" embed selected-multimedia="profileCtrl.primaryAudio"></div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">

                            <g:if test="${params.isOpusReviewer}">
                                <g:render template="comments"/>
                            </g:if>
                        </div>
                    </div>
                </tab>
                <tab heading="Distribution" class="font-xxsmall" managed-tab ng-show="managedTabCtrl.hasContent()">
                    <div class="row">
                        <div class="col-md-12 padding-bottom-2">
                            <g:render template="map" model="[allowStaticImage: false, width: '100%', height: '900px', fullWidth: true]"/>
                        </div>
                    </div>
                </tab>
                <tab heading="Gallery" class="font-xxsmall" managed-tab ng-show="managedTabCtrl.hasContent()">
                    <div class="row">
                        <div class="col-md-12">
                            <g:render template="images"/>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-12">
                            <multimedia profile="profileCtrl.profile" readonly="profileCtrl.readonly"></multimedia>
                        </div>
                    </div>
                </tab>
                <tab heading="Literature & Links" class="font-xxsmall" managed-tab ng-show="managedTabCtrl.hasContent()">
                    <div class="row">
                        <div class="col-md-12">
                            <g:render template="../common/attachments" model="[hideHeading: false]"/>
                            <g:render template="links"/>
                            <g:render template="bhlLinks"/>
                            <g:render template="publications"/>
                            <g:render template="bibliography"/>
                            <g:if test="${!profile.archivedDate}">
                                <g:render template="lists"/>
                            </g:if>
                        </div>
                    </div>
                </tab>
                <tab heading="Key" class="font-xxsmall"
                     ng-show="profileCtrl.opus.keybaseProjectId && profileCtrl.hasKeybaseKey" managed-tab select-to-initialise="true">
                    <div class="row">
                        <key-player taxon-name="profileCtrl.profile.scientificName" style="display: block"
                                    opus-id="profileCtrl.opus.uuid"
                                    keybase-url="${g.createLink(controller:'keybase', action:'keyLookup', absolute: true)}"
                                    key-lookup-url="${request.scheme}://${request.serverName}${request.serverPort ? ":" + request.serverPort : ""}${request.contextPath}/keybase/findKey"
                                    profile-url="${request.scheme}://${request.serverName}${request.serverPort ? ":" + request.serverPort : ""}${request.contextPath}/opus/{{profileCtrl.opus.shortName ? profileCtrl.opus.shortName : profileCtrl.opus.uuid}}/profile"></key-player>
                    </div>
                </tab>
            </tabset>
        </div>
    </div>
</g:if>

<div class="row margin-top-1">
    <div class="col-md-12 col-xs-12 col-lg-12 small text-center" ng-cloak>
        <p><span ng-show="profileCtrl.opus.copyrightText">&copy; {{ profileCtrl.opus.copyrightText }}.</span> <a
                href="${request.contextPath}/opus/{{profileCtrl.opus.shortName ? profileCtrl.opus.shortName : profileCtrl.opus.uuid}}/about##copyright"
                target="_blank">Copyright Notice</a>.</p>
    </div>
</div>

<div class="row margin-top-1" ng-cloak ng-show="profileCtrl.profile.authorship.length > 0">
    <navigation-anchor anchor-name="view_authorship" title="{{profileCtrl.acknowledgementsSectionTitle}}" condition="profileCtrl.profile.authorship.length > 0"></navigation-anchor>

    <div class="col-sm-12 col-md-8 profile-contributor-text">
        <span ng-repeat="contrib in profileCtrl.profile.authorship"
              ng-show="contrib.text">{{contrib.category | capitalize}}(s) - {{contrib.text}}<span
                ng-show="!$last"><br/></span>
        </span>
    </div>
    <div class="col-sm-12 col-md-4 last-updated-text">
        Last updated: <span class="last-updated">{{profileCtrl.profile.lastUpdatedBy?profileCtrl.profile.lastUpdatedBy + ';':''}} {{ profileCtrl.profile.lastPublished | date : 'MMM d, y h:mm' }}</span>
        Status: <span ng-bind="profileCtrl.profile.profileStatus"></span>
    </div>
</div>

<a href="#top" du-smooth-scroll target="_self" class="font-xxsmall float-bottom-left"><span
        class="fa fa-arrow-up">&nbsp;Scroll to top</span></a>
