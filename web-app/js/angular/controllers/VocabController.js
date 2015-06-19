/**
 * Vocab controller
 */
profileEditor.controller('VocabController', function ($rootScope, profileService, util, messageService, $filter, $modal) {
    var self = this;

    self.opus = null;
    self.opusId = util.getEntityId("opus");
    self.saving = false;
    self.newVocabTerm = null;
    self.vocabulary = null;
    self.replacements = [];

    var capitalize = $filter("capitalize");
    var orderBy = $filter("orderBy");

    loadOpus();

    self.addVocabTerm = function (form) {
        if (self.newVocabTerm) {
            self.vocabulary.terms.push({termId: "", name: capitalize(self.newVocabTerm), order: self.vocabulary.terms.length});
            self.newVocabTerm = "";
            sortVocabTerms();
            form.$setDirty();
        }
    };

    self.removeVocabTerm = function (index, form) {
        var promise = profileService.findUsagesOfVocabTerm(self.opusId, self.opus.attributeVocabUuid, self.vocabulary.terms[index].name);
        promise.then(function (data) {
            if (data.usageCount == 0) {
                var deletedItemOrder = self.vocabulary.terms[index].order;
                self.vocabulary.terms.splice(index, 1);

                angular.forEach(self.vocabulary.terms, function(term) {
                    if (term.order > deletedItemOrder) {
                        term.order = term.order - 1;
                    }
                });

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
            size: "md",
            resolve: {
                usageData: function() {
                    return data
                },
                existingTerm: function() {
                    return self.vocabulary.terms[existingTermIndex];
                },
                terms: function() {
                    var terms = angular.copy(self.vocabulary.terms);
                    var deletedItemOrder = terms[existingTermIndex].order;
                    terms.splice(existingTermIndex, 1);

                    angular.forEach(terms, function(term) {
                        if (term.order > deletedItemOrder) {
                            term.order = term.order - 1;
                        }
                    });
                    angular.forEach(self.vocabulary.terms, function(term) {
                        if (term.order > deletedItemOrder) {
                            term.order = term.order - 1;
                        }
                    });
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
            size: "md",
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
        var promise = profileService.updateVocabulary(self.opusId, self.opus.attributeVocabUuid, self.vocabulary);
        promise.then(function () {
                if (self.replacements.length > 0) {
                    var promise = profileService.replaceUsagesOfVocabTerm(self.opusId, self.opus.attributeVocabUuid, self.replacements);
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

        var promise = profileService.getOpusVocabulary(self.opusId, vocabId);
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

    self.moveTermUp = function(index, form) {
        if (index > 0) {
            self.vocabulary.terms[index].order = self.vocabulary.terms[index].order - 1;
            self.vocabulary.terms[index - 1].order = self.vocabulary.terms[index - 1].order + 1;

            sortVocabTerms();

            form.$setDirty();
        }
    };

    self.moveTermDown = function(index, form) {
        if (index < self.vocabulary.terms.length) {
            self.vocabulary.terms[index].order = self.vocabulary.terms[index].order + 1;
            self.vocabulary.terms[index + 1].order = self.vocabulary.terms[index + 1].order - 1;

            sortVocabTerms();

            form.$setDirty();
        }
    };

    function loadOpus() {
        if (!self.opusId) {
            return;
        }

        var promise = profileService.getOpus(self.opusId);

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
        self.vocabulary.terms = orderBy(self.vocabulary.terms, "order");
    }

    self.sortAlphabetically = function(form) {
        self.vocabulary.terms = orderBy(self.vocabulary.terms, "name");
        angular.forEach(self.vocabulary.terms, function(term, index) {
            term.order = index;
        });

        form.$setDirty();
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