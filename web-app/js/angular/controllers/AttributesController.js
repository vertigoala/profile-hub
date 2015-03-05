/**
 * Atributes controller
 */
profileEditor.controller('AttributeEditor', function (profileService, util, messageService, $window) {
    var self = this;
    
    self.attributes = [];
    self.attributeTitles = [];

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

    function loadVocabulary() {
        if (self.opus.attributeVocabUuid != null) {
            var vocabPromise = profileService.getOpusVocabulary(self.opus.attributeVocabUuid);
            vocabPromise.then(function (data) {
                self.attributeTitles = data.terms;
            });
        }
    }

    self.revertAttribute = function (attributeIdx, auditIdx) {
        self.attributes[attributeIdx].title = self.attributes[attributeIdx].audit[auditIdx].object.title;
        self.attributes[attributeIdx].text = self.attributes[attributeIdx].audit[auditIdx].object.text;
        self.$apply();
    };

    self.showAudit = function (idx) {
        var future = profileService.getAuditForAttribute(self.attributes[idx].uuid);
        future.then(function (audit) {
                self.attributes[idx].audit = audit;
            },
            function () {
                messageService.alert("An error occurred while retrieving the audit history.")
            }
        );
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
            title: attribute.title,
            text: attribute.text
        });

        future.then(function (attribute) {
                self.attributes[idx].saving = false;
                messageService.success("Last saved " + new Date());

                self.attributes[idx].uuid = attribute.attributeId;
                attributeForm.$setPristine();
            },
            function () {
                self.attributes[idx].saving = false;
                messageService.alert("An error has occurred while saving.");
            }
        );
    };
});