<div class="panel panel-default" ng-form="OpusForm" ng-cloak>
    <div class="panel-heading">
        <a name="overview">
            <h4 class="section-panel-heading">Site overview</h4>
        </a>
    </div>

    <div class="panel-body">

        <div>
            <div class="col-sm-12">
                <div class="form-group">
                    <label for="opusName">Title</label>
                    <input type="text"
                           id="opusName"
                           class="form-control"
                           name="opusName"
                           ng-required="true"
                           ng-model="opusCtrl.opus.title"/>
                </div>
                <div class="form-group" ng-show="!opusCtrl.opus.uuid">
                    <label for="dataResource">Atlas of Living Australia Resource</label>
                    <input type="text"
                           id="dataResource"
                           class="form-control"
                           name="opusName"
                           ng-model="opusCtrl.opus.dataResource"
                           ng-required="true"
                           typeahead-editable="false"
                           typeahead-on-select="opusCtrl.opusResourceChanged($item, $model, $label)"
                           typeahead="source as source.name for source in opusCtrl.dataResourceList | filter:$viewValue | limitTo:10"/>
                    <span class="small">This allows data from Atlas of Living Australia (such as occurrence maps) to be included in your profiles.</span>
                    <alert type="danger"
                           ng-show="!opusCtrl.opus.dataResource">You must select a value from the list.</alert>
                </div>
                <div class="form-group">
                    <label>Description</label>

                    <textarea id="description" ng-model="opusCtrl.opus.description" rows="4"
                              class="form-control" ng-maxlength="300" maxlength="300"></textarea>
                </div>
            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <button class="btn btn-primary pull-right" ng-click="opusCtrl.saveOpus(OpusForm)"
                        ng-disabled="(!opusCtrl.opus.uuid && !opusCtrl.opus.dataResource) || OpusForm.$invalid">
                    <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="OpusForm.$dirty">*</span> Save</span>
                    <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>
</div>