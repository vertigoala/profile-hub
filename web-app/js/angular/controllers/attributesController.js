/**
 * Atributes controller
 */
profileEditor.controller('AttributeEditor', function ($scope, profileService, util, messageService) {

    $scope.attributes = [];
    $scope.attributeTitles = [];

    $scope.init = function (edit) {
        $scope.readonly = edit != 'true';

        var profilePromise = profileService.getProfile(util.getPathItem(util.LAST));
        messageService.info("Loading profile data...");
        profilePromise.success(function (data, status, headers, config) {
            messageService.pop();
            $scope.profile = data.profile;
            $scope.opus = data.opus;
            $scope.attributes = data.profile.attributes;

            loadVocabulary();
        });
        profilePromise.error(function (data, status, headers, config) {
            console.log("There was a problem retrieving profile..." + status);
            messageService.alert("An error occurred while retrieving the profile.");
        });
    };

    function loadVocabulary() {
        if ($scope.opus.attributeVocabUuid != null) {
            var vocabPromise = profileService.getOpusVocabulary($scope.opus.attributeVocabUuid);
            vocabPromise.success(function (data, status, headers, config) {
                $scope.attributeTitles = data.terms;
            });
            vocabPromise.error(function (data, status, headers, config) {
                console.log("There was a problem retrieving vocab for profile..." + status);
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
        future.success(function (audit) {
            $scope.attributes[idx].audit = audit;
        });
        future.error(function (data, status, headers, config) {
            console.log("There was a problem retrieving profile..." + status);
            messageService.alert("An error occurred while retrieving the audit history.")
        });
    };

    $scope.deleteAttribute = function (idx) {
        var confirmed = window.confirm("Are you sure?");
        if (confirmed) {
            if ($scope.attributes[idx].uuid !== "") {
                var future = profileService.deleteAttribute($scope.attributes[idx].uuid, $scope.profile.uuid);
                future.success(function (data) {
                    $scope.attributes.splice(idx, 1);
                });
                future.error(function (data, status, headers, config) {
                    messageService.alert("An error occurred while deleting the record.")
                });
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
        console.log("Saving attribute " + idx);
        var attribute = $scope.attributes[idx];
        $scope.attributes[idx].saving = true;

        var future = profileService.saveAttribute($scope.profile.uuid, attribute.uuid, {
            profileId: $scope.profile.uuid,
            attributeId: attribute.uuid,
            title: attribute.title,
            text: attribute.text
        });

        future.success(function (attribute) {
            $scope.attributes[idx].saving = false;
            messageService.success("Last saved " + new Date());
            console.log("Id before = " + $scope.attributes[idx].uuid + "; id after = " + attribute.attributeId);
            $scope.attributes[idx].uuid = attribute.attributeId;
            attributeForm.$setPristine();
        });
        future.error(function (data, status, headers, config) {
            $scope.attributes[idx].saving = false;
            messageService.alert("An error has occurred while saving.");
        });

    };
});