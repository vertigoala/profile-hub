<div class="panel panel-default" ng-form="AuthForm" ng-cloak>
    <div class="panel-heading">
        <a name="authorship">
            <h4>Authorship and attribution controls</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="col-sm-12">
            <div class="checkbox">
                <label for="fineGrainedAttribution" class="inline-label">
                    <input id="fineGrainedAttribution" type="checkbox" name="allowFineGrainedAttribution"
                           ng-model="opusCtrl.opus.allowFineGrainedAttribution" ng-false-value="false">
                    Show contributors and editors for attributes.
                </label>
            </div>
        </div>
    </div>
    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <button class="btn btn-primary pull-right" ng-click="opusCtrl.saveOpus(AuthForm)">
                    <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="AuthForm.$dirty">*</span> Save</span>
                    <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>

</div>

<a name="authorshipVocab"></a>
<vocabulary-editor vocab-id="opusCtrl.opus.authorshipVocabUuid" vocab-name="Authors & acknowledgements vocabulary"></vocabulary-editor>