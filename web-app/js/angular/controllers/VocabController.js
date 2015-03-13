/**
 * Vocab controller
 */
profileEditor.controller('VocabController', function ($rootScope, profileService, util, messageService, $filter, $modal) {
    var self = this;

    self.opus = null;
    self.opusId = util.getPathItem(util.LAST);
    self.saving = false;
    self.newVocabTerm = null;
    self.vocabulary = null;
    self.replacements = [];

    var capitalize = $filter("capitalize");
    var orderBy = $filter("orderBy");

    loadOpus();

    self.addVocabTerm = function (form) {
        if (self.newVocabTerm) {
            self.vocabulary.terms.push({vocabId: self.opus.attributeVocabUuid, name: capitalize(self.newVocabTerm)});
            self.newVocabTerm = "";
            sortVocabTerms();
            form.$setDirty();
        }
    };

    self.removeVocabTerm = function (index, form) {
        var promise = profileService.findUsagesOfVocabTerm(self.opus.attributeVocabUuid, self.vocabulary.terms[index].name);
        promise.then(function (data) {
            if (data.usageCount == 0) {
                self.vocabulary.terms.splice(index, 1);

                sortVocabTerms();

                form.$setDirty();
            } else {
                showRemoveTermPopup(data, index, form)
            }
        },
        function() {
            messageService.alert("An error occurred while checking if the term is in use.");
        });
    };

    function showRemoveTermPopup(data, existingTermIndex, form) {
        var popup = $modal.open({
            templateUrl: "removeTermPopup.html",
            controller: "RemoveTermController",
            controllerAs: "removeTermCtrl",
            size: "sm",
            resolve: {
                usageData: function() {
                    return data
                },
                existingTerm: function() {
                    return self.vocabulary.terms[existingTermIndex];
                },
                terms: function() {
                    var terms = angular.copy(self.vocabulary.terms);
                    terms.splice(existingTermIndex, 1);
                    return terms;
                },
                form: function() {
                    return form;
                }
            }
        });

        popup.result.then(function(data) {
            self.replacements.push({vocabId: self.opus.attributeVocabUuid, existingTermName: data.existing.name, newTermName: data.new.name});

            form.$setDirty();
        });
    }

    self.termIsInReplacementList = function(term) {
        var match = false;
        angular.forEach(self.replacements, function(item) {
            if (item.existingTermName == term.name) {
                match = true;
            }
        });
        return match
    };

    self.editVocabTerm = function(index, form) {
        var popup = $modal.open({
            templateUrl: "editTermPopup.html",
            controller: "VocabModalController",
            controllerAs: "vocabModalCtrl",
            size: "sm",
            resolve: {
                term: function() {
                    return angular.copy(self.vocabulary.terms[index]);
                }
            }
        });

        popup.result.then(function(updatedTerm) {
            self.vocabulary.terms[index] = updatedTerm;
            form.$setDirty();
        });
    };

    self.saveVocabulary = function (form) {
        var promise = profileService.updateVocabulary(self.opus.attributeVocabUuid, self.vocabulary);
        promise.then(function () {
                if (self.replacements.length > 0) {
                    var promise = profileService.replaceUsagesOfVocabTerm(self.replacements);
                    promise.then(function() {
                        console.log("Replacements saved");

                        self.loadVocabulary(self.opus.attributeVocabUuid, form);
                        messageService.success("Vocabulary successfully updated.");
                    })
                } else {
                    self.loadVocabulary(self.opus.attributeVocabUuid, form);
                    messageService.success("Vocabulary successfully updated.");
                }
            },
            function () {
                messageService.alert("An error occurred while updating the vocabulary.");
            }
        );
    };

    self.loadVocabulary = function(vocabId, form) {
        messageService.info("Loading vocabulary...");
        self.replacements = [];

        var promise = profileService.getOpusVocabulary(vocabId);
        promise.then(function (data) {
                messageService.pop();

                self.vocabulary = data;

                sortVocabTerms();

                if (form) {
                    form.$setPristine()
                }
            },
            function () {
                messageService.alert("An error occurred while loading the vocabulary.");
            }
        );
    };

    function loadOpus() {
        if (!self.opusId) {
            return;
        }
        var promise = profileService.getOpus(self.opusId);

        messageService.info("Loading opus data...");
        promise.then(function (data) {
                console.log("Retrieved " + data.title);
                self.opus = data;

                self.loadVocabulary(self.opus.attributeVocabUuid);

                messageService.pop();
            },
            function () {
                messageService.alert("An error occurred while retrieving the opus.");
            }
        );
    }

    function sortVocabTerms() {
        self.vocabulary.terms = orderBy(self.vocabulary.terms, "name");
    }

});

/**
 * Edit Vocab Term modal dialog controller
 */
profileEditor.controller('VocabModalController', function ($modalInstance, term) {
    var self = this;

    self.term = term;

    self.ok = function() {
        $modalInstance.close(self.term);
    };

    self.cancel = function() {
        $modalInstance.dismiss("cancel");
    }
});

/**
 * Replace Vocab Term modal dialog controller
 */
profileEditor.controller('RemoveTermController', function ($modalInstance, usageData, existingTerm, terms) {
    var self = this;

    self.terms = terms;
    self.usageCount = usageData.usageCount;
    self.existingTerm = existingTerm;
    self.newTerm = null;

    self.ok = function() {
        $modalInstance.close({existing: self.existingTerm, new: self.newTerm});
    };

    self.cancel = function() {
        $modalInstance.dismiss("cancel");
    }
});