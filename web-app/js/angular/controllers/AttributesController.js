/**
 * Atributes controller
 */
profileEditor.controller('AttributeEditor', function (profileService, util, messageService, $window, $filter) {
    var self = this;

    self.attributes = [];
    self.attributeTitles = [];
    self.historyShowing = {};
    self.vocabularyStrict = false;

    var capitalize = $filter("capitalize");

    self.init = function (edit) {
        self.readonly = edit != 'true';

        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));
        messageService.info("Loading profile data...");
        profilePromise.then(function (data) {
                messageService.pop();
                self.profile = data.profile;
                self.opus = data.opus;
                self.attributes = data.profile.attributes;

                loadVocabulary();
            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    self.isValid = function(attributeTitle) {
        attributeTitle = capitalize(attributeTitle);
        return !self.vocabularyStrict || (self.vocabularyStrict && self.attributeTitles.indexOf(attributeTitle) > -1)
    };

    function loadVocabulary() {
        if (self.opus.attributeVocabUuid != null) {
            var vocabPromise = profileService.getOpusVocabulary(self.opus.attributeVocabUuid);
            vocabPromise.then(function (data) {
                self.attributeTitles = [];
                angular.forEach(data.terms, function(term) {
                    self.attributeTitles.push(term.name);
                });
                //self.attributeTitles = data.terms;
                self.vocabularyStrict = data.strict;
            });
        }
    }

    self.revertAttribute = function (attributeIdx, auditIdx, form) {
        self.attributes[attributeIdx].title = self.attributes[attributeIdx].audit[auditIdx].object.title;
        self.attributes[attributeIdx].text = self.attributes[attributeIdx].audit[auditIdx].object.text;
        form.$setDirty();
    };

    self.showAudit = function (idx) {
        var future = profileService.getAuditForAttribute(self.attributes[idx].uuid);
        future.then(function (audit) {
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
        var confirmed = $window.confirm("Are you sure?");

        if (confirmed) {
            if (self.attributes[idx].uuid !== "") {
                var future = profileService.deleteAttribute(self.attributes[idx].uuid, self.profile.uuid);
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
        }
    };

    self.addAttribute = function () {
        self.attributes.unshift(
            {"uuid": "", "title": "", "text": "", contributor: []}
        );
        console.log("adding attributes: " + self.attributes.length);
    };

    self.saveAttribute = function (idx, attributeForm) {
        var attribute = self.attributes[idx];
        self.attributes[idx].saving = true;

        var future = profileService.saveAttribute(self.profile.uuid, attribute.uuid, {
            profileId: self.profile.uuid,
            attributeId: attribute.uuid,
            title: capitalize(attribute.title),
            text: attribute.text
        });

        future.then(function (attribute) {
                self.attributes[idx].saving = false;
                messageService.success("Last saved " + new Date());

                self.attributes[idx].uuid = attribute.attributeId;
                self.attributes[idx].auditShowing = false;
                attributeForm.$setPristine();
            },
            function () {
                self.attributes[idx].saving = false;
                messageService.alert("An error has occurred while saving.");
            }
        );
    };
});