<div class="well" ng-controller="VocabController as vocabCtrl" ng-form="VocabForm" ng-cloak>
    <h3>
        Attribute vocabulary
    </h3>

    <div class="row-fluid">
        <div class="control-group">
            <label for="strict" class="inline-label">
                <input id="strict" type="radio" name="strict" ng-value="true" ng-model="vocabCtrl.vocabulary.strict">
                Strict: Attributes must use a value from the vocabulary for their title. New values can only be added on this page.
            </label>
            <br/>
            <label for="notStrict" class="inline-label">
                <input id="notStrict" type="radio" name="strict" ng-value="false" ng-model="vocabCtrl.vocabulary.strict">
                Not Strict: Attributes can use any value for their title (new values will be automatically added to the vocabulary).
            </label>
        </div>

        <div class="subpanel">
            <div class="input-append">
                <label for="newTerm" class="inline-label">New Term:</label>
                <input id="newTerm" type="text" ng-model="vocabCtrl.newVocabTerm" class="input-xlarge"/>
                <button class="btn" ng-click="vocabCtrl.addVocabTerm(VocabForm)"><i
                        class="icon icon-plus"></i>  Add term</button>
            </div>
            <hr/>

            <div class="row-fluid">
                <span ng-repeat="term in vocabCtrl.vocabulary.terms" class="column-item" ng-if="!vocabCtrl.termIsInReplacementList(term)">
                    <span ng-class="term.termId ? '' : 'newItem'">{{ term.name }}</span>
                    <button class="btn-link fa fa-edit" title="Edit the {{ term.name }} term"
                            ng-click="vocabCtrl.editVocabTerm($index, VocabForm)"></button>
                    <button class="btn-link fa fa-trash" title="Delete the {{ term.name }} term"
                            ng-click="vocabCtrl.removeVocabTerm($index, VocabForm)"></button>
                    <button class="btn btn-link fa fa-arrow-down"
                            ng-if="!$last"
                            ng-click="vocabCtrl.moveTermDown($index, VocabForm)"
                            title="Move this term down"></button>
                    <button class="btn btn-link fa fa-arrow-up" ng-if="!$first"
                            ng-click="vocabCtrl.moveTermUp($index, VocabForm)"
                            title="Move this term up"></button>
                </span>
            </div>
            <div class="row-fluid">
                <button class="btn btn-link pull-right" ng-click="vocabCtrl.sortAlphabetically(VocabForm)">Sort alphabetically</button>
            </div>
        </div>
        <button class="btn btn-primary" ng-click="vocabCtrl.saveVocabulary(VocabForm)">
            <span ng-show="!vocabCtrl.saving" id="saved"><span ng-show="VocabForm.$dirty">*</span> Save vocabulary
            </span>
            <span ng-show="vocabCtrl.saving" id="saving">Saving....</span>
        </button>
        <button class="btn btn-warning" ng-click="vocabCtrl.loadVocabulary(vocabCtrl.opus.attributeVocabUuid, VocabForm)">
            Reset
        </button>
    </div>
</div>

<script type="text/ng-template" id="editTermPopup.html">
<div class="modal-header">
    <h3 class="modal-title">Edit vocabulary term</h3>
</div>

<div class="modal-body">
    <label for="editTerm" class="inline-label">New Term:</label>
    <input id="editTerm" type="text" ng-model="vocabModalCtrl.term.name" class="input-xlarge" ng-enter="vocabModalCtrl.ok()"/>
</div>

<div class="modal-footer">
    <button class="btn btn-primary" ng-click="vocabModalCtrl.ok()">OK</button>
    <button class="btn btn-warning" ng-click="vocabModalCtrl.cancel()">Cancel</button>
</div>
</script>

<script type="text/ng-template" id="removeTermPopup.html">
<div class="modal-header">
    <h3 class="modal-title">Remove vocabulary term</h3>
</div>

<div class="modal-body">
    <p>
        The vocabulary term {{removeTermCtrl.existingTerm.name}} is being used in {{removeTermCtrl.usageCount}} place(s).
        You must either select a replacement term or manually find and remove all usages before you can delete the term.
    </p>

    <label for="editTerm" class="inline-label">Replacement Term:</label>
    <select ng-options="term as term.name for term in removeTermCtrl.terms" ng-model="removeTermCtrl.newTerm" class="input-large" ng-enter="removeTermCtrl.ok()"></select>
</div>

<div class="modal-footer">
    <button class="btn btn-primary" ng-click="removeTermCtrl.ok()" >OK</button>
    <button class="btn btn-warning" ng-click="removeTermCtrl.cancel()">Cancel</button>
</div>
</script>