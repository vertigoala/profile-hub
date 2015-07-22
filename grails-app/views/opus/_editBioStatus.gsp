<div class="panel panel-default" ng-form="BioStatusFrom" ng-cloak>
    <div class="panel-heading">
        <a name="biostatus">
            <h4 class="section-panel-heading">Status Lists</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="col-sm-12">
            <p>Configure the list(s) to be used for the status(es) of your profiles. These lists can include such statuses as biostatus, naturalised status, pest status, and so forth.</p>

            <p>Using the <a href="${grailsApplication.config.lists.base.url}"
                            target="_blank">Atlas of Living Australia Lists Tool</a>, upload a Species List as a .csv file, where the first column is "scientific_name" (this must match the name of your profile, excluding author information), plus a separate column for each status you wish to display.
            </p>

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
                    <button class="btn btn-default" ng-click="opusCtrl.addBioStatusList()"><i
                            class="fa fa-plus"></i>  Add list</button>
                </div>
                <button class="btn btn-primary pull-right" ng-click="opusCtrl.saveBioStatusLists(BioStatusFrom)">
                    <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="BioStatusFrom.$dirty">*</span> Save
                    </span>
                    <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>
</div>
