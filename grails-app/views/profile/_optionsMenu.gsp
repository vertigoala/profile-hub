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
                <span class="fa fa-cog"></span>
                Options
                <span class="fa fa-angle-double-down"></span>
            </button>

            <ul class="dropdown-menu dropdown-menu-right"
                role="menu"
                aria-labelledby="optionsDropdown">
                <li role="presentation" ng-hide="profileCtrl.isArchived()">
                    <a href="${request.contextPath}/opus/{{profileCtrl.opusId}}/profile/{{profileCtrl.profileId}}/json"
                       target="_blank"><span class="fa fa-file-text-o"></span>&nbsp;&nbsp;Export as JSON
                    </a>
                </li>
                <li role="presentation" ng-controller="ExportController as exportCtrl" ng-hide="profileCtrl.isArchived()">
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
                    <li class="divider" ng-hide="profileCtrl.isArchived()"></li>
                    <li role="presentation" ng-hide="!profileCtrl.readonly() || profileCtrl.isArchived()">
                        <a href="${request.contextPath}/opus/{{profileCtrl.opusId}}/profile/{{profileCtrl.profile.scientificName}}/update"
                           target="_self"><span
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
                    <li class="divider" ng-hide="profileCtrl.readonly()"></li>
                    <li role="presentation">
                        <a href="" ng-click="profileCtrl.deleteProfile()" target="_self"
                           ng-hide="profileCtrl.readonly() || !profileCtrl.profileId || profileCtrl.profile.publications.length > 0"><span
                                class="fa fa-trash-o"></span>&nbsp;&nbsp;Delete this profile</a>
                    </li>
                    <li role="presentation">
                        <a href="" ng-click="profileCtrl.archiveProfile()" target="_self"
                           ng-hide="profileCtrl.readonly() || profileCtrl.isArchived()"><span
                                class="fa fa-archive"></span>&nbsp;&nbsp;Archive this profile</a>
                    </li>
                    <li role="presentation" ng-hide="!profileCtrl.isArchived()">
                        <a href="" ng-click="profileCtrl.restoreProfile()" target="_self"><span
                                class="fa fa-recycle"></span>&nbsp;&nbsp;Restore this profile</a>
                    </li>
                </g:if>
            </ul>
        </div>
    </div>
</div>

<g:render template="archiveAndRestorePopups"/>
