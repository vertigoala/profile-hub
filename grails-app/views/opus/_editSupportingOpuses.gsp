<div class="panel panel-default" ng-form="OpusForm" ng-cloak>
    <div class="panel-heading">
        <a name="supportingCollections">
            <h4>Supporting Collections</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="col-sm-12">
            <p>Configure the profile collections to be used to support profile pages. Information from these collections can be included in the profile pages of your collection.</p>

            <div class="checkbox">
                <label for="showLinked" class="inline-label">
                    <input id="showLinked" type="checkbox" name="showLinked"
                           ng-model="opusCtrl.opus.showLinkedOpusAttributes" ng-false-value="false"
                           ng-disabled="opusCtrl.opus.supportingOpuses.length == 0">
                    Show data from supporting collections on the profile pages.
                </label>
            </div>

            <div class="checkbox">
                <label for="allowCopying" class="inline-label">
                    <input id="allowCopying" type="checkbox" name="allowCopying"
                           ng-model="opusCtrl.opus.allowCopyFromLinkedOpus" ng-false-value="false"
                           ng-disabled="opusCtrl.opus.supportingOpuses.length == 0">
                    Allow profile editors to copy data from supporting collections on the profile pages.
                </label>
            </div>

            <ul class="vertical-pad">
                <li ng-repeat="opus in opusCtrl.opus.supportingOpuses">
                    <a href="${request.contextPath}/opus/{{opus.uuid}}">{{ opus.title }}</a>
                    <a class="btn btn-mini btn-link" title="Remove this collection"
                       ng-click="opusCtrl.removeSupportingOpus($index, 'existing', OpusForm)">
                        <i class="fa fa-trash-o color--red"></i>
                    </a>
                </li>

                <li ng-repeat="opus in opusCtrl.newSupportingOpuses">
                    <div class="form-inline">
                        <div class="form-group">
                            <input placeholder="Collection name..."
                                   ng-model="opus.opus"
                                   autocomplete="off"
                                   size="70"
                                   class="form-control"
                                   typeahead="opus as opus.title for opus in opusCtrl.opusList | filter:$viewValue | limitTo:10"/>
                            <span class="fa fa-ban color--red" ng-if="!opus.uuid"></span>
                            <button class="btn btn-mini btn-link" title="Remove this collection"
                                    ng-click="opusCtrl.removeSupportingOpus($index, 'new', OpusForm)">
                                <i class="fa fa-trash-o color--red"></i>
                            </button>
                        </div>
                    </div>
                </li>
            </ul>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <div class="btn-group">
                    <button class="btn btn-default" ng-click="opusCtrl.addSupportingOpus()"><i
                            class="fa fa-plus"></i>  Add collection</button>
                </div>
                <button class="btn btn-primary pull-right" ng-click="opusCtrl.saveSupportingOpuses(OpusForm)">
                    <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="OpusForm.$dirty">*</span> Save</span>
                    <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>
</div>