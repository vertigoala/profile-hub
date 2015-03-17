<div id="opusInfo" class="well" ng-form="OpusForm">
    <div ng-show="!opusCtrl.opus.uuid">
        <p>
            <label>Name</label>
            <input type="text"
                   class="input-xxlarge"
                   name="opusName"
                   ng-model="opusCtrl.opus.dataResource"
                   typeahead-editable="false"
                   typeahead-on-select="opusCtrl.opusResourceChanged($item, $model, $label)"
                   typeahead="source as source.name for source in opusCtrl.dataResourceList | filter:$viewValue | limitTo:10"/>
            <alert type="danger" ng-show="!opusCtrl.opus.dataResource">You must select a value from the list.</alert>
        </p>

    </div>
    <h4>Description</h4>
    <p>
        {{opusCtrl.dataResource.pubDescription | default:'No description available.'}}
    </p>
    <h4>Rights</h4>
    <p>
        {{opusCtrl.dataResource.rights | default:'No rights statement available.'}}
    </p>
    <h4>Citation</h4>
    <p>
        {{opusCtrl.dataResource.citation | default:'No citation statement available.'}}
    </p>

    <button class="btn btn-primary" ng-click="opusCtrl.saveOpus(OpusForm)" ng-show="!opusCtrl.opus.uuid" ng-disabled="!opusCtrl.opus.dataResource">
        <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="OpusForm.$dirty">*</span> Save</span>
        <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
    </button>
</div>