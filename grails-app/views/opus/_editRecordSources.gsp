<div class="well" ng-form="RecordForm" ng-cloak>
    <h3>Approved Specimen/Observation sources</h3>

    <p>Configure the record sources to be included in your profile pages. This will set what data is used on maps.
    These are data resources accessible via Atlas API's.
    </p>
    <ul>
        <li ng-repeat="recordSource in opusCtrl.opus.recordSources">
            <a href="${grailsApplication.config.collectory.base.url}/public/show/{{recordSource}}">{{opusCtrl.dataResources[recordSource] | default:'Loading...'}}</a>
            <button class="btn btn-mini btn-danger" title="Remove this resource"
                    ng-click="opusCtrl.removeRecordSource($index, 'existing', RecordForm)">
                <i class="icon-minus icon-white"></i>
            </button>
        </li>

        <li ng-repeat="recordSource in opusCtrl.newRecordSources">
            <input placeholder="Record source name..."
                   ng-model="recordSource.dataResource"
                   autocomplete="off"
                   class="input-xlarge"
                   typeahead="source as source.name for source in opusCtrl.dataResourceList | filter:$viewValue | limitTo:10"/>
            <span class="fa fa-ban red" ng-if="recordSource.dataResource && !recordSource.dataResource.id"></span>
            <button class="btn btn-mini btn-danger" title="Remove this resource"
                    ng-click="opusCtrl.removeRecordSource($index, 'new', RecordForm)">
                <i class="icon-minus icon-white"></i>
            </button>
        </li>
    </ul>

    <button class="btn btn-primary" ng-click="opusCtrl.saveRecordSources(RecordForm)">
        <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="RecordForm.$dirty">*</span> Save</span>
        <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
    </button>
    <button class="btn btn-info" ng-click="opusCtrl.addRecordSource()"><i class="icon icon-plus"></i>  Add record source</button>
</div>