<div class="well control-group" ng-form="ListForm" ng-cloak>
    <h3>Approved Lists</h3>

    <p>Configure the species lists to be included in your profile pages. If you do not approve any specific lists, then all lists will be considered.</p>

    <ul>
        <li ng-repeat="approvedList in opusCtrl.opus.approvedLists">
            <a href="${grailsApplication.config.lists.base.url}/speciesListItem/list/{{approvedList}}">{{(opusCtrl.allSpeciesLists | filter: approvedList)[0].listName | default:'Loading...'}}</a>
            <a class="btn btn-mini btn-danger" title="Remove this resource"
                    ng-click="opusCtrl.removeApprovedList($index, 'existing', ListForm)">
                <i class="icon-minus icon-white"></i>
            </a>
        </li>

        <li ng-repeat="approvedList in opusCtrl.newApprovedLists">
            <input placeholder="List name..."
                   ng-model="approvedList.list"
                   autocomplete="off"
                   class="input-xlarge"
                   typeahead="list as list.listName for list in opusCtrl.allSpeciesLists | filter:$viewValue | limitTo:10"/>
            <span class="fa fa-ban red" ng-if="approvedList.list && !approvedList.list.dataResourceUid"></span>
            <button class="btn btn-mini btn-danger" title="Remove this resource"
                    ng-click="opusCtrl.removeApprovedList($index, 'new', ListForm)">
                <i class="icon-minus icon-white"></i>
            </button>
        </li>
    </ul>
    <button class="btn btn-primary" ng-click="opusCtrl.saveApprovedLists(ListForm)">
        <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="ListForm.$dirty">*</span> Save</span>
        <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
    </button>
    <button class="btn btn-info" ng-click="opusCtrl.addApprovedList()"><i class="icon icon-plus icon-white"></i>  Add list</button>
</div>