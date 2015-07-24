/**
 *  About opus controller
 */
profileEditor.controller('AboutController', function (profileService, messageService, util) {
    var self = this;
    
    self.opusId = util.getEntityId("opus");
    self.aboutHtml = null;
    self.citationHtml = null;

    var future = profileService.getOpusAbout(self.opusId);
    future.then(function(data) {
        self.aboutHtml = data.opus.aboutHtml;
        self.citationHtml = data.opus.citationHtml;
    });

    self.hasCitation = function() {
        return self.citationHtml != null && self.citationHtml.trim() != ''
    };

    self.saveAboutHtml = function (form) {
        console.log('About HTML' + self.aboutHtml);
        var promise = profileService.updateOpusAbout(self.opusId, self.aboutHtml, self.citationHtml);
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