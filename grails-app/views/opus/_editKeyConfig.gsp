<div id="opusKeyConfig" class="well" ng-form="KeyForm" ng-cloak>
    <h3>Keys configuration</h3>
    <p>
        <label>Project ID:</label>
        <input type="text" class="input-small" name="projectId" ng-model="opusCtrl.opus.keybaseProjectId"/>
    </p>
    <p>
        <label>Top-level Key ID:</label>
        <input type="text" class="input-small" name="keyId" ng-model="opusCtrl.opus.keybaseKeyId"/>
    </p>

    <button class="btn btn-primary" ng-click="opusCtrl.saveOpus(KeyForm)">
        <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="KeyForm.$dirty">*</span> Save</span>
        <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
    </button>
</div>