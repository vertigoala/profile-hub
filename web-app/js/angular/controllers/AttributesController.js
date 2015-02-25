/**
 * Atributes controller
 */
profileEditor.controller('AttributeEditor', function ($scope, profileService, util, messageService, $window) {

    $scope.attributes = [];
    $scope.attributeTitles = [];

    $scope.init = function (edit) {
        $scope.readonly = edit != 'true';

        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));
        messageService.info("Loading profile data...");
        profilePromise.then(function (data) {
                messageService.pop();
                $scope.profile = data.profile;
                $scope.opus = data.opus;
                $scope.attributes = data.profile.attributes;

                loadVocabulary();
            },
            function () {
                messageService.alert("An error occurred while retrieving the profile.");
            }
        );
    };

    function loadVocabulary() {
        if ($scope.opus.attributeVocabUuid != null) {
            var vocabPromise = profileService.getOpusVocabulary($scope.opus.attributeVocabUuid);
            vocabPromise.then(function (data) {
                $scope.attributeTitles = data.terms;
            });
        }
    }

    $scope.revertAttribute = function (attributeIdx, auditIdx) {
        $scope.attributes[attributeIdx].title = $scope.attributes[attributeIdx].audit[auditIdx].object.title;
        $scope.attributes[attributeIdx].text = $scope.attributes[attributeIdx].audit[auditIdx].object.text;
        $scope.$apply();
    };

    $scope.showAudit = function (idx) {
        var future = profileService.getAuditForAttribute($scope.attributes[idx].uuid);
        future.then(function (audit) {
                $scope.attributes[idx].audit = audit;
            },
            function () {
                messageService.alert("An error occurred while retrieving the audit history.")
            }
        );
    };

    $scope.deleteAttribute = function (idx) {
        var confirmed = $window.confirm("Are you sure?");

        if (confirmed) {
            if ($scope.attributes[idx].uuid !== "") {
                var future = profileService.deleteAttribute($scope.attributes[idx].uuid, $scope.profile.uuid);
                future.then(function () {
                        $scope.attributes.splice(idx, 1);
                    },
                    function () {
                        messageService.alert("An error occurred while deleting the record.");
                    }
                );
            } else {
                $scope.attributes.splice(idx, 1);
                console.log("Local delete only deleting attributes: " + $scope.attributes.length);
            }
        }
    };

    $scope.addAttribute = function () {
        $scope.attributes.unshift(
            {"uuid": "", "title": "", "text": "", contributor: []}
        );
        console.log("adding attributes: " + $scope.attributes.length);
    };

    $scope.saveAttribute = function (idx, attributeForm) {
        var attribute = $scope.attributes[idx];
        $scope.attributes[idx].saving = true;

        var future = profileService.saveAttribute($scope.profile.uuid, attribute.uuid, {
            profileId: $scope.profile.uuid,
            attributeId: attribute.uuid,
            title: attribute.title,
            text: attribute.text
        });

        future.then(function (attribute) {
                $scope.attributes[idx].saving = false;
                messageService.success("Last saved " + new Date());

                $scope.attributes[idx].uuid = attribute.attributeId;
                attributeForm.$setPristine();
            },
            function () {
                $scope.attributes[idx].saving = false;
                messageService.alert("An error has occurred while saving.");
            }
        );
    };
});