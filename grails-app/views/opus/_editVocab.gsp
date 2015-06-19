<div class="panel panel-default" ng-controller="VocabController as vocabCtrl" ng-form="VocabForm" ng-cloak>
    <div class="panel-heading">
        <a name="attributeVocab">
            <h4>Attribute vocabulary</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="col-sm-12">
            <p>Attribute vocabulary settings</p>

            <div class="radio">
                <label for="strict" class="inline-label">
                    <input id="strict" type="radio" name="strict" ng-value="true"
                           ng-model="vocabCtrl.vocabulary.strict">
                    Strict: Attributes must use a value from the vocabulary for their title. New values can only be added on this page.
                </label>
            </div>

            <div class="radio">
                <label for="notStrict" class="inline-label">
                    <input id="notStrict" type="radio" name="strict" ng-value="false"
                           ng-model="vocabCtrl.vocabulary.strict">
                    Not Strict: Attributes can use any value for their title (new values will be automatically added to the vocabulary).
                </label>
            </div>
            <hr/>

            <label for="newTerm">Enter a new term</label>

            <div class="input-group">
                <input id="newTerm" type="text" class="form-control input-lg" ng-model="vocabCtrl.newVocabTerm"
                       ng-enter="vocabCtrl.addVocabTerm(VocabForm)">
                <span class="input-group-btn">
                    <button class="btn btn-default btn-lg" type="button" ng-click="vocabCtrl.addVocabTerm(VocabForm)"><i
                            class="fa fa-plus"></i> Add term</button>
                </span>
            </div>

            <h4>Existing terms</h4>

            <div class="col-md-12">

                <div class="row">
                    <span ng-repeat="term in vocabCtrl.vocabulary.terms" class="column-item"
                          ng-if="!vocabCtrl.termIsInReplacementList(term)">
                        <span ng-class="term.termId ? '' : 'newItem'">{{term.order + 1}}. {{ term.name }}</span>
                        <button class="btn btn-link btn-xs fa fa-edit" title="Edit the {{ term.name }} term"
                                ng-click="vocabCtrl.editVocabTerm($index, VocabForm)"></button>
                        <button class="btn btn-link btn-xs fa fa-trash-o" title="Delete the {{ term.name }} term"
                                ng-click="vocabCtrl.removeVocabTerm($index, VocabForm)"></button>
                        <button class="btn btn-link btn-xs fa fa-arrow-down"
                                ng-if="!$last"
                                ng-click="vocabCtrl.moveTermDown($index, VocabForm)"
                                title="Move this term down"></button>
                        <button class="btn btn-link btn-xs fa fa-arrow-up" ng-if="!$first"
                                ng-click="vocabCtrl.moveTermUp($index, VocabForm)"
                                title="Move this term up"></button>
                    </span>

                    <div class="row">
                        <div class="col-md-12">
                            <button class="btn btn-link pull-right small"
                                    ng-click="vocabCtrl.sortAlphabetically(VocabForm)">Sort alphabetically</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <div class="btn-group">
                    <button class="btn btn-default"
                            ng-click="vocabCtrl.loadVocabulary(vocabCtrl.opus.attributeVocabUuid, VocabForm)">
                        Reset
                    </button>
                </div>
                <button class="btn btn-primary pull-right" ng-click="vocabCtrl.saveVocabulary(VocabForm)">
                    <span ng-show="!vocabCtrl.saving" id="saved"><span
                            ng-show="VocabForm.$dirty">*</span> Save
                    </span>
                    <span ng-show="vocabCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>

    <script type="text/ng-template" id="editTermPopup.html">
    <div class="modal-header">
        <h4 class="modal-title">Edit vocabulary term</h4>
    </div>

    <div class="modal-body">
        <div class="form-horizontal">
            <div class="form-group">
                <label for="editTerm" class="col-sm-3 control-label">New Term</label>

                <div class="col-sm-8">
                    <input id="editTerm" type="text" ng-model="vocabModalCtrl.term.name" class="form-control"
                           ng-enter="vocabModalCtrl.ok()"/>
                </div>
            </div>
        </div>
    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" ng-click="vocabModalCtrl.ok()">OK</button>
        <button class="btn btn-default" ng-click="vocabModalCtrl.cancel()">Cancel</button>
    </div>
    </script>

    <script type="text/ng-template" id="removeTermPopup.html">
    <div class="modal-header">
        <h4 class="modal-title">Remove vocabulary term</h4>
    </div>

    <div class="modal-body">
        <p>
            The vocabulary term {{removeTermCtrl.existingTerm.name}} is being used in {{removeTermCtrl.usageCount}} profile(s).
            You must either select a replacement term or manually find and remove all usages before you can delete the term.
        </p>

        <div class="form-horizontal">
            <div class="form-group">
                <label for="editTerm" class="col-sm-3 control-label">Replacement Term:</label>

                <div class="col-sm-8">
                    <select ng-options="term as term.name for term in removeTermCtrl.terms"
                            ng-model="removeTermCtrl.newTerm" class="form-control"
                            ng-enter="removeTermCtrl.ok()"></select>
                </div>
            </div>
        </div>
    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" ng-click="removeTermCtrl.ok()">OK</button>
        <button class="btn btn-default" ng-click="removeTermCtrl.cancel()">Cancel</button>
    </div>
    </script>

</div>
