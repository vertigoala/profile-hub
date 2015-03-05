/**
 * Opus controller
 */
profileEditor.controller('OpusController', function ($rootScope, profileService, util, messageService) {
    var self = this;

    self.opusId = util.getPathItem(util.LAST);
    self.readonly = true;
    self.dataResource = null;

    loadOpus();

    function loadOpus() {
        var promise = profileService.getOpus(self.opusId);
        messageService.info("Loading opus data...");
        promise.then(function (data) {
                self.opus = data;

                loadResources();
                loadDescription();

                messageService.pop();
            },
            function () {
                messageService.alert("An error occurred while retrieving the opus.");
            }
        );
    }

    function loadResources() {
        var promise = profileService.listResources();
        console.log("Loading data resources...");
        promise.then(function (data) {
                self.dataResources = data;
            },
            function () {
                console.log("Failed to retrieve opus description from collectory.");
            }
        );
    }

    function loadDescription() {
        var promise = profileService.getResource(self.opus.dataResourceUid);
        console.log("Loading opus description...");
        promise.then(function (data) {
                self.dataResource = data;
            },
            function () {
                console.log("Failed to retrieve opus description from collectory.");
            }
        );
    }

});