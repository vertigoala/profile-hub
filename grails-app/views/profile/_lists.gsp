<div ng-controller="ListsEditor as listCtrl" ng-init="listCtrl.init('${edit}')">
    <a name="{{listCtrl.readonly ? 'view_' : 'edit_'}}lists"></a>
    <div class="panel panel-default" ng-show="listCtrl.lists.length > 0">
        <div class="panel-body">
            <div class="col-sm-2"><strong>Conservation & sensitivity lists</strong></div>

            <div class="col-sm-10">
                <ul>
                    <li ng-repeat="list in listCtrl.lists">
                        <a href="${grailsApplication.config.lists.base.url}/speciesListItem/list/{{ list.dataResourceUid }}">{{ list.list.listName }}</a>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <div class="panel panel-default" ng-show="listCtrl.conservationStatuses.length > 0">
        <a name="{{listCtrl.readonly ? 'view_' : 'edit_'}}conservationStatus"></a>
        <div class="panel-body">
            <div class="col-sm-2"><strong>Conservation status</strong></div>

            <div class="col-sm-10">
                <div class="col-sm-4" ng-repeat="status in listCtrl.conservationStatuses">
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