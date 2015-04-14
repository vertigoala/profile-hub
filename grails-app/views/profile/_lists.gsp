<div ng-controller="ListsEditor as listCtrl" ng-init="listCtrl.init('${edit}')">
    <div ng-show="listCtrl.lists.length > 0" class="bs-docs-example"
         ng-cloak id="browse_lists" data-content="Conservation & sensitivity lists">
        <ul>
            <li ng-repeat="list in listCtrl.lists">
                <a href="${grailsApplication.config.lists.base.url}/speciesListItem/list/{{ list.dataResourceUid }}">{{ list.list.listName }}</a>
            </li>
        </ul>
    </div>

    <div ng-show="listCtrl.conservationStatuses.length > 0"
         class="bs-docs-example"
         data-content="Conservation status">
        <div class="row-fluid">
            <div class="span5" ng-repeat="status in listCtrl.conservationStatuses" style="margin-left: 2.5%">
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