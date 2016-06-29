<div class="panel panel-default" ng-form="AuthForm" ng-cloak>
    <div class="panel-heading">
        <a name="authorship">
            <h4 class="section-panel-heading">Authorship and attribution controls</h4>
            <p:help help-id="opus.edit.authorship"/>
        </a>
    </div>

    <div class="panel-body">
        <div class="row">
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
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <save-button ng-click="opusCtrl.saveOpus(AuthForm)" form="AuthForm"></save-button>
            </div>
        </div>
    </div>

</div>

<a name="authorshipVocab"></a>
<vocabulary-editor vocab-id="opusCtrl.opus.authorshipVocabUuid" vocab-name="Acknowledgements vocabulary" help-url="<p:helpUrl help-id="opus.edit.acknowledgementVocabulary"/>"
                   all-mandatory="true" allow-mandatory="false" allow-reordering="false" allow-categories="false"></vocabulary-editor>