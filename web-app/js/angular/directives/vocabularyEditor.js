profileEditor.directive('vocabularyEditor', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            vocabId: '=',
            vocabName: '@',
            allowReordering: '@'
        },
        templateUrl: $browser.baseHref() + 'static/templates/vocabularyEditor.html',
        controller: ['$scope', 'profileService', 'util', 'messageService', '$modal', '$filter', function ($scope, profileService, util, messageService, $modal, $filter) {

            $scope.opusId = util.getEntityId("opus");
            $scope.saving = false;
            $scope.newVocabTerm = null;
            $scope.vocabulary = null;
            $scope.replacements = [];
            $scope.allowReordering = false;

            var capitalize = $filter("capitalize");
            var orderBy = $filter("orderBy");

            $scope.addVocabTerm = function (form) {
                if ($scope.newVocabTerm) {
                    $scope.vocabulary.terms.push({termId: "", name: capitalize($scope.newVocabTerm), order: $scope.vocabulary.terms.length});
                    $scope.newVocabTerm = "";
                    sortVocabTerms();
                    form.$setDirty();
                }
            };

            $scope.removeVocabTerm = function (index, form) {
                var promise = profileService.findUsagesOfVocabTerm($scope.opusId, $scope.vocabId, $scope.vocabulary.terms[index].name);
                promise.then(function (data) {
                        if (data.usageCount == 0) {
                            var deletedItemOrder = $scope.vocabulary.terms[index].order;
                            $scope.vocabulary.terms.splice(index, 1);

                            angular.forEach($scope.vocabulary.terms, function(term) {
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
                            return $scope.vocabulary.terms[existingTermIndex];
                        },
                        terms: function() {
                            var terms = angular.copy($scope.vocabulary.terms);
                            var deletedItemOrder = terms[existingTermIndex].order;
                            terms.splice(existingTermIndex, 1);

                            angular.forEach(terms, function(term) {
                                if (term.order > deletedItemOrder) {
                                    term.order = term.order - 1;
                                }
                            });
                            angular.forEach($scope.vocabulary.terms, function(term) {
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
                    $scope.replacements.push({vocabId: $scope.vocabId, existingTermName: data.existing.name, newTermName: data.new.name});

                    form.$setDirty();
                });
            }

            $scope.termIsInReplacementList = function(term) {
                var match = false;
                angular.forEach($scope.replacements, function(item) {
                    if (item.existingTermName == term.name) {
                        match = true;
                    }
                });
                return match
            };

            $scope.editVocabTerm = function(index, form) {
                var popup = $modal.open({
                    templateUrl: "editTermPopup.html",
                    controller: "VocabModalController",
                    controllerAs: "vocabModalCtrl",
                    size: "md",
                    resolve: {
                        term: function() {
                            return angular.copy($scope.vocabulary.terms[index]);
                        }
                    }
                });

                popup.result.then(function(updatedTerm) {
                    $scope.vocabulary.terms[index] = updatedTerm;
                    form.$setDirty();
                });
            };

            $scope.saveVocabulary = function (form) {
                var promise = profileService.updateVocabulary($scope.opusId, $scope.vocabId, $scope.vocabulary);
                promise.then(function () {
                        if ($scope.replacements.length > 0) {
                            var promise = profileService.replaceUsagesOfVocabTerm($scope.opusId, $scope.vocabId, $scope.replacements);
                            promise.then(function() {
                                console.log("Replacements saved");

                                $scope.loadVocabulary(form);
                                messageService.success("Vocabulary successfully updated.");
                            })
                        } else {
                            $scope.loadVocabulary(form);
                            messageService.success("Vocabulary successfully updated.");
                        }
                    },
                    function () {
                        messageService.alert("An error occurred while updating the vocabulary.");
                    }
                );
            };

            $scope.loadVocabulary = function(form) {
                console.log("loadVocabulary 1")
                messageService.info("Loading vocabulary...");
                $scope.replacements = [];

                var promise = profileService.getOpusVocabulary($scope.opusId, $scope.vocabId);
                promise.then(function (data) {
                        console.log("loadVocabulary 2")
                        messageService.pop();

                        $scope.vocabulary = data;

                        sortVocabTerms();

                        if (form) {
                            form.$setPristine()
                        }
                    },
                    function () {
                        console.log("loadVocabulary 3")
                        messageService.alert("An error occurred while loading the vocabulary.");
                    }
                );
            };

            $scope.moveTermUp = function(index, form) {
                if (index > 0) {
                    $scope.vocabulary.terms[index].order = $scope.vocabulary.terms[index].order - 1;
                    $scope.vocabulary.terms[index - 1].order = $scope.vocabulary.terms[index - 1].order + 1;

                    sortVocabTerms();

                    form.$setDirty();
                }
            };

            $scope.moveTermDown = function(index, form) {
                if (index < $scope.vocabulary.terms.length) {
                    $scope.vocabulary.terms[index].order = $scope.vocabulary.terms[index].order + 1;
                    $scope.vocabulary.terms[index + 1].order = $scope.vocabulary.terms[index + 1].order - 1;

                    sortVocabTerms();

                    form.$setDirty();
                }
            };

            function sortVocabTerms() {
                if ($scope.allowReordering) {
                    $scope.vocabulary.terms = orderBy($scope.vocabulary.terms, "order");
                } else {
                    $scope.vocabulary.terms = orderBy($scope.vocabulary.terms, "name");
                }
                angular.forEach($scope.vocabulary.terms, function(term, index) {
                    term.order = index;
                });
            }

            $scope.sortAlphabetically = function(form) {
                $scope.vocabulary.terms = orderBy($scope.vocabulary.terms, "name");
                angular.forEach($scope.vocabulary.terms, function(term, index) {
                    term.order = index;
                });

                form.$setDirty();
            };
        }],
        link: function (scope, element, attrs, ctrl) {
            scope.$watch("vocabId", function(newValue) {
                console.log("Watch vocabId " + newValue)
                if (newValue !== undefined) {
                    scope.loadVocabulary();
                }
            });
        }
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