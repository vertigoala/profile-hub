<div class="well control-group" ng-form="AuthForm" ng-cloak>
    <h3>Authorship and attribution controls</h3>

    <div class="row-fluid">
        <label for="fineGrainedAttribution" class="inline-label">
            <input id="fineGrainedAttribution" type="checkbox" name="allowFineGrainedAttribution"
                   ng-model="opusCtrl.opus.allowFineGrainedAttribution" ng-false-value="false">
            Show contributors and editors for attributes.
        </label>
    </div>

    <div class="row-fluid">
        <button class="btn btn-primary" ng-click="opusCtrl.saveOpus(AuthForm)">
            <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="AuthForm.$dirty">*</span> Save</span>
            <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
        </button>
    </div>
</div>