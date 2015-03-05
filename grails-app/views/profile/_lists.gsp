<div ng-show="listCtrl.lists.length > 0" ng-controller="ListsEditor as listCtrl" ng-init="listCtrl.init('${edit}')" class="bs-docs-example ng-cloak"
     ng-cloak id="browse_lists" data-content="Conservation & sensitivity lists">
    <ul>
        <li ng-repeat="list in listCtrl.lists">
            <a href="${grailsApplication.config.lists.base.url}/speciesListItem/list/{{ list.dataResourceUid }}">{{ list.list.listName }}</a>
        </li>
    </ul>
</div>