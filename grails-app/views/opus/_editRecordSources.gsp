<div class="panel panel-default" ng-form="RecordForm" ng-cloak>
    <div class="panel-heading">
        <a name="recordSources">
            <h4 class="section-panel-heading">Approved Specimen/Observation sources</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="col-sm-12">
            <p>Configure the record sources to be included in your profile pages. This will set what data is used on maps.
            These are data resources accessible via Atlas API's.
            </p>

            <div class="col-sm-12">
                <ul>
                    <li ng-repeat="recordSource in opusCtrl.opus.recordSources">
                        <a href="${grailsApplication.config.collectory.base.url}/public/show/{{recordSource}}">{{opusCtrl.dataResources[recordSource] | default:'Loading...'}}</a>
                        <button class="btn btn-mini btn-link" title="Remove this resource"
                                ng-click="opusCtrl.removeRecordSource($index, 'existing', RecordForm)">
                            <i class="fa fa-trash-o color--red"></i>
                        </button>
                    </li>

                    <li ng-repeat="recordSource in opusCtrl.newRecordSources">
                        <div class="form-inline">
                            <div class="form-group">
                                <input placeholder="Record source name..."
                                       ng-model="recordSource.dataResource"
                                       autocomplete="off"
                                       size="70"
                                       class="form-control"
                                       typeahead="source as source.name for source in opusCtrl.dataResourceList | filter:$viewValue | limitTo:10"/>
                                <span class="fa fa-ban color--red"
                                      ng-if="recordSource.dataResource && !recordSource.dataResource.id"></span>
                                <button class="btn btn-mini btn-link" title="Remove this resource"
                                        ng-click="opusCtrl.removeRecordSource($index, 'new', RecordForm)">
                                    <i class="fa fa-trash-o color--red"></i>
                                </button>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <div class="btn-group">
                    <button class="btn btn-default" ng-click="opusCtrl.addRecordSource()"><i
                            class="fa fa-plus"></i>  Add record source</button>
                </div>
                <button class="btn btn-primary pull-right" ng-click="opusCtrl.saveRecordSources(RecordForm)">
                    <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="RecordForm.$dirty">*</span> Save</span>
                    <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>

</div>