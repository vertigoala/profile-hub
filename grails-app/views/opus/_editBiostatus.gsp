<div class="panel panel-default" ng-form="BioStatusFrom" ng-cloak>
    <div class="panel-heading">
        <a name="biostatus">
            <h4>Bio Status List</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="col-sm-12">
            <p>Configure the species lists to be included in your profile pages.</p>

            <div class="col-sm-12">
                <ul>
                    <li ng-repeat="bioStatus in opusCtrl.opus.bioStatusLists">
                        <a href="${grailsApplication.config.lists.base.url}/speciesListItem/list/{{bioStatus}}">{{(opusCtrl.allSpeciesLists | filter: bioStatus)[0].listName | default:'Loading...'}}</a>
                        <a class="btn btn-mini btn-link" title="Remove this resource"
                           ng-click="opusCtrl.removeBioStatusList($index, 'existing', BioStatusFrom)">
                            <i class="fa fa-trash-o color--red"></i>
                        </a>
                    </li>

                    <li ng-repeat="bioStatus in opusCtrl.newBioStatusLists">
                        <div class="form-inline">
                            <div class="form-group">
                                <input placeholder="List name..."
                                       ng-model="bioStatus.list"
                                       autocomplete="off" size="70"
                                       class="form-control"
                                       typeahead="list as list.listName for list in opusCtrl.allSpeciesLists | filter:$viewValue | limitTo:10"/>
                                <span class="fa fa-ban color--red"
                                      ng-if="bioStatus.list && !bioStatus.list.dataResourceUid"></span>
                                <button class="btn btn-mini btn-link" title="Remove this resource"
                                        ng-click="opusCtrl.removeBioStatusList($index, 'new', BioStatusFrom)">
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
                    <button class="btn btn-default" ng-click="opusCtrl.addBioStatusList()" ng-disabled="!opusCtrl.canAddBioStatusList()"><i
                            class="fa fa-plus"></i>  Add list</button>
                </div>
                <button class="btn btn-primary pull-right" ng-click="opusCtrl.saveBioStatusLists(BioStatusFrom)">
                    <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="BioStatusFrom.$dirty">*</span> Save</span>
                    <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>
</div>
