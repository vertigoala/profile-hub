/**
 * Attributes controller
 */
profileEditor.controller('AttributeEditor', function (profileService, navService, util, messageService, $window, $filter, $modal, $scope) {
    var self = this;

    self.attributes = [];
    self.attributeTitles = [];
    self.allowedVocabulary = [];
    self.historyShowing = {};
    self.vocabularyStrict = false;
    self.supportingAttributes = {};
    self.showSupportingData = false;
    self.currentUser = util.currentUser();
    self.supportingAttributeTitles = [];

    var capitalize = $filter("capitalize");

    self.init = function (edit) {
        self.readonly = edit != 'true';

        self.profileId = util.getEntityId("profile");
        self.opusId = util.getEntityId("opus");

        var profilePromise = profileService.getProfile(self.opusId, self.profileId);
        messageService.info("Loading profile data...");
        profilePromise.then(function (data) {
                messageService.pop();
                self.profile = data.profile;
                self.opus = data.opus;
                self.attributes = data.profile.attributes;

                angular.forEach(self.attributes, function(attribute) {
                    navService.add(attribute.title, util.toKey(attribute.title));
                    attribute.key = util.toKey(attribute.title);
                });

                if (self.opus.supportingOpuses && self.opus.supportingOpuses.length > 0) {
                    self.loadAttributesFromSupportingCollections();
                }

                loadVocabulary();
            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    self.isValid = function (attributeTitle) {
        attributeTitle = capitalize(attributeTitle);
        return !self.vocabularyStrict || (self.vocabularyStrict && self.allowedVocabulary.indexOf(attributeTitle) > -1)
    };

    self.showAttribute = function (attribute) {
        return (self.readonly &&
            (!attribute.fromCollection ||
            (attribute.fromCollection && self.opus.showLinkedOpusAttributes && self.showSupportingData)))
            || (!self.readonly &&
            attribute.fromCollection && self.opus.allowCopyFromLinkedOpus && self.showSupportingData)
    };

    self.showTitleGroup = function (title) {
        var show = false;

        angular.forEach(self.attributes, function (attribute) {
            show = show || (attribute.title == title && self.showAttribute(attribute));
        });

        return show;
    };

    function loadVocabulary() {
        if (self.opus.attributeVocabUuid != null) {
            var vocabPromise = profileService.getOpusVocabulary(self.opusId, self.opus.attributeVocabUuid);
            vocabPromise.then(function (data) {
                self.attributeTitles = [];
                self.allowedVocabulary = [];
                angular.forEach(data.terms, function (term) {
                    if (self.attributeTitles.indexOf(term) == -1) {
                        self.attributeTitles.push(term);
                    }
                    if (self.allowedVocabulary.indexOf(term.name) == -1) {
                        self.allowedVocabulary.push(term.name);
                    }
                });

                self.attributeTitles.sort(compareTitles);

                self.vocabularyStrict = data.strict;
            });
        }
    }

    function compareTitles(left, right) {
        var compare = -1;
        if (left.order == right.order) {
            compare = left.name.toLowerCase() < right.name.toLowerCase() ? -1 : left.name.toLowerCase() > right.name.toLowerCase();
        } else {
            compare = left.order < right.order ? -1 : 1;
        }
        return compare;
    }

    self.revertAttribute = function (attributeIdx, auditIdx, form) {
        self.attributes[attributeIdx].title = self.attributes[attributeIdx].audit[auditIdx].object.title;
        self.attributes[attributeIdx].text = self.attributes[attributeIdx].audit[auditIdx].object.text;
        form.$setDirty();
    };

    self.showAudit = function (idx) {
        var future = profileService.getAuditForAttribute(self.attributes[idx].uuid);
        future.then(function (audit) {
                var d = new diff_match_patch();

                for (var i = 0; i < audit.length - 1; i++) {
                    if (audit[i + 1].object.plainText && audit[i].object.plainText) {
                        var diff = d.diff_main(audit[i + 1].object.plainText, audit[i].object.plainText);
                        audit[i].diff = d.diff_prettyHtml(diff);
                    }
                }

                self.attributes[idx].audit = audit;
                self.attributes[idx].auditShowing = true;
            },
            function () {
                messageService.alert("An error occurred while retrieving the audit history.")
            }
        );
    };

    self.hideAudit = function (idx) {
        self.attributes[idx].auditShowing = false;
    };

    self.deleteAttribute = function (idx) {
        var confirmed = util.confirm("Are you sure you wish to delete this attribute?");

        confirmed.then(function () {
            if (self.attributes[idx].uuid !== "") {
                var future = profileService.deleteAttribute(self.opusId, self.profileId, self.attributes[idx].uuid);
                future.then(function () {
                        self.attributes.splice(idx, 1);
                    },
                    function () {
                        messageService.alert("An error occurred while deleting the record.");
                    }
                );
            } else {
                self.attributes.splice(idx, 1);
                console.log("Local delete only deleting attributes: " + self.attributes.length);
            }
        });
    };

    self.addAttribute = function (form) {
        self.attributes.unshift(
            {"uuid": "", "title": "", "text": "", contributor: []}
        );
        if (form) {
            form.$setDirty();
        }
    };

    self.copyAttribute = function (index, form) {
        var copy = angular.copy(self.attributes[index]);
        copy.fromCollection = null;
        copy.original = self.attributes[index];
        copy.source = copy.original.fromCollection.opusTitle;
        copy.uuid = "";
        self.attributes[index] = copy;
        if (self.isValid(copy.title)) {
            self.saveAttribute(index, form);
        }
    };

    self.saveAttribute = function (idx, attributeForm) {
        var attribute = self.attributes[idx];
        self.attributes[idx].saving = true;

        var data = {
            profileId: self.profile.uuid,
            uuid: attribute.uuid,
            title: capitalize(attribute.title),
            text: attribute.text
        };

        if (attribute.source) {
            data.source = attribute.source;
        }
        if (attribute.attributeTo) {
            data.attributeTo = attribute.attributeTo;
        }
        if (attribute.original) {
            data.original = attribute.original;
        }
        if (attribute.creators) {
            data.creators = attribute.creators;
        }
        if (attribute.editors) {
            data.editors = attribute.editors;
        }
        data.significantEdit = attribute.significantEdit ? attribute.significantEdit : false;

        var future = profileService.saveAttribute(self.opusId, self.profileId, attribute.uuid, data);

        future.then(function (attribute) {
                self.attributes[idx].saving = false;
                messageService.success("Last saved " + new Date());

                self.attributes[idx].uuid = attribute.attributeId;
                self.attributes[idx].auditShowing = false;
                self.attributes[idx].audit = null;
                attributeForm.$setPristine();
            },
            function () {
                self.attributes[idx].saving = false;
                messageService.alert("An error has occurred while saving.");
            }
        );
    };

    self.loadAttributesFromSupportingCollections = function () {
        var profileAttributeMap = [];
        angular.forEach(self.attributes, function (attribute) {
            profileAttributeMap.push(attribute.title)
        });

        var supportingOpusList = [];
        angular.forEach(self.opus.supportingOpuses, function (opus) {
            supportingOpusList.push(opus.uuid)
        });

        if (supportingOpusList) {
            var searchResult = profileService.profileSearch(supportingOpusList.join(","), self.profile.scientificName, false);
            searchResult.then(function (searchResults) {
                    angular.forEach(searchResults, function (result) {
                        var profilePromise = profileService.getProfile(result.opus.uuid, result.profileId);
                        profilePromise.then(function (supporting) {

                            angular.forEach(supporting.profile.attributes, function (attribute) {
                                if (profileAttributeMap.indexOf(attribute.title) == -1) {
                                    attribute.fromCollection = {
                                        opusId: supporting.opus.uuid,
                                        opusTitle: supporting.opus.title,
                                        opusShortName: supporting.opus.shortName,
                                        profileId: supporting.profile.uuid
                                    };

                                    attribute.key = util.toKey(attribute.title);

                                    self.attributes.push(attribute);
                                    var title = {name: attribute.title};

                                    self.supportingAttributeTitles.push(attribute.title);

                                    if (self.attributeTitles.indexOf(title) == -1) {
                                        self.attributeTitles.push(title);
                                    }
                                }

                                if (!self.supportingAttributes[attribute.title]) {
                                    self.supportingAttributes[attribute.title] = [];
                                }
                                self.supportingAttributes[attribute.title].push({
                                    opusId: supporting.opus.uuid,
                                    opusTitle: supporting.opus.title,
                                    profileId: supporting.profile.uuid,
                                    title: attribute.title,
                                    text: attribute.text
                                });
                            });
                        });
                    });
                }
            );
        }
    };

    self.viewInOtherCollections = function (title) {
        var supporting = self.supportingAttributes[title];

        $modal.open({
            templateUrl: "supportingCollections.html",
            controller: "AttributePopupController",
            controllerAs: "attrModalCtrl",
            size: "lg",
            resolve: {
                supporting: function () {
                    return supporting;
                }
            }
        });
    };

    self.toggleShowSupportingData = function() {
        angular.forEach(self.supportingAttributeTitles, function(title) {
            if (self.showSupportingData) {
                navService.add(title, util.toKey(title));
            } else {
                navService.remove(util.toKey(title));
            }
        });
    };

    self.parseInt = function(number) {
        return parseInt(number, 10);
    }
});


/**
 * Attributes Popup controller
 */
profileEditor.controller('AttributePopupController', function ($modalInstance, supporting) {
    var self = this;

    self.supporting = supporting;

    self.close = function () {
        $modalInstance.dismiss();
    };
});