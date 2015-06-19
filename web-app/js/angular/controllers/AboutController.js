/**
 *  About opus controller
 */
profileEditor.controller('AboutController', function (profileService, messageService, util) {
    var self = this;
    
    self.opusId = util.getEntityId("opus");
    self.aboutHtml = null;

    var future = profileService.getOpusAbout(self.opusId);
    future.then(function(data) {
        self.aboutHtml = data.opus.aboutHtml;
    });

    self.saveAboutHtml = function (form) {
        var promise = profileService.updateOpusAbout(self.opusId, self.aboutHtml);
        promise.then(function () {
                messageService.success("About page text successfully updated.");
                form.$setPristine();
            },
            function () {
                messageService.alert("An error occurred while updating the about page.");
            }
        );
    };
});