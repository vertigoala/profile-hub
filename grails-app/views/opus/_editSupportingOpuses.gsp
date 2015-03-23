<div class="well control-group" ng-form="OpusForm" ng-cloak>
    <h3>Approved Supporting Collections</h3>

    <p>Configure the profile collections to be used to support profile pages. Information from these collections can be included in the profile pages of your collection.</p>

    <label for="showLinked" class="inline-label">
        <input id="showLinked" type="checkbox" name="showLinked" ng-model="opusCtrl.opus.showLinkedOpusAttributes" ng-false-value="false" ng-disabled="opusCtrl.opus.supportingOpuses.length == 0">
        Show data from supporting collections on the profile pages.
    </label>
    <br/>
    <label for="allowCopying" class="inline-label">
        <input id="allowCopying" type="checkbox" name="allowCopying" ng-model="opusCtrl.opus.allowCopyFromLinkedOpus" ng-false-value="false" ng-disabled="opusCtrl.opus.supportingOpuses.length == 0 || !opusCtrl.opus.showLinkedOpusAttributes">
        Allow profile editors to copy data from supporting collections on the profile pages.
    </label>

        <ul class="vertical-pad">
            <li ng-repeat="opus in opusCtrl.opus.supportingOpuses">
                <a href="${request.contextPath}/opus/{{opus.uuid}}">{{ opus.title }}</a>
                <a class="btn btn-mini btn-danger" title="Remove this collection"
                   ng-click="opusCtrl.removeSupportingOpus($index, 'existing', OpusForm)">
                    <i class="icon-minus icon-white"></i>
                </a>
            </li>

            <li ng-repeat="opus in opusCtrl.newSupportingOpuses">
                <input placeholder="Collection name..."
                       ng-model="opus.opus"
                       autocomplete="off"
                       class="input-xlarge"
                       typeahead="opus as opus.title for opus in opusCtrl.opusList | filter:$viewValue | limitTo:10"/>
                <span class="fa fa-ban red" ng-if="!opus.uuid"></span>
                <button class="btn btn-mini btn-danger" title="Remove this collection"
                        ng-click="opusCtrl.removeSupportingOpus($index, 'new', OpusForm)">
                    <i class="icon-minus icon-white"></i>
                </button>
            </li>
        </ul>

    <div>
        <button class="btn btn-primary" ng-click="opusCtrl.saveSupportingOpuses(OpusForm)">
            <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="OpusForm.$dirty">*</span> Save</span>
            <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
        </button>
        <button class="btn btn-info" ng-click="opusCtrl.addSupportingOpus()"><i class="icon icon-plus"></i>  Add collection</button>
    </div>
</div>