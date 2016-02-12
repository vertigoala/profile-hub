<div ng-controller="ListsEditor as listCtrl" ng-init="listCtrl.init('${edit}')">
    <a name="{{listCtrl.readonly ? 'view_' : 'edit_'}}lists"></a>

    <div class="panel panel-default" ng-show="listCtrl.lists.length > 0">
        <div class="panel-heading">
            <div class="row">
                <div class="col-md-12">
                    <h4 class="section-panel-heading">Conservation & sensitivity lists</h4>
                </div>
            </div>
        </div>

        <div class="panel-body">
            <div class="row">
                <div class="col-md-12">
                    <ul>
                        <li ng-repeat="list in listCtrl.lists">
                            <a href="${grailsApplication.config.lists.base.url}/speciesListItem/list/{{ list.dataResourceUid }}">{{ list.list.listName }}</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <div class="panel panel-default" ng-show="listCtrl.conservationStatuses.length > 0">
        <a name="{{listCtrl.readonly ? 'view_' : 'edit_'}}conservationStatus"></a>
        <div class="panel-heading">
            <div class="row">
                <div class="col-md-12">
                    <h4 class="section-panel-heading">Conservation status</h4>
                </div>
            </div>
        </div>

        <div class="panel-body">
            <div class="row section-no-para" ng-show="listCtrl.conservationStatuses.length > 0">
                <div class="col-md-3 minor-heading">Conservation status</div>
                <div class="col-md-9">
                    <div ng-repeat="status in listCtrl.conservationStatuses" class="col-md-4">
                        <a href="${grailsApplication.config.collectory.base.url}/public/showDataResource/{{listCtrl.statusRegions[status.region].id}}"
                           title="Threatened Species Codes - details" target="_blank">
                            <div class="status"
                                 ng-class="listCtrl.getColourForStatus(status.status)">{{listCtrl.statusRegions[status.region].abbrev | default:'IUCN'}}</div>
                            {{status.rawStatus}}
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="panel panel-default" ng-show="listCtrl.features.length > 0">
        <a name="{{listCtrl.readonly ? 'view_' : 'edit_'}}features"></a>
        <div class="panel-heading">
            <div class="row">
                <div class="col-md-12">
                    <h4 class="section-panel-heading">{{ listCtrl.opus.featureListSectionName | default:'Feature List' }}</h4>
                </div>
            </div>
        </div>

        <div class="panel-body">
            <div class="row section-no-para" ng-repeat="feature in listCtrl.features">
                <div class="col-md-3 minor-heading">{{feature.key | formatText | capitalize}}</div>

                <div class="col-md-9">
                    {{feature.value  | formatText}}
                </div>
            </div>
        </div>
    </div>
</div>