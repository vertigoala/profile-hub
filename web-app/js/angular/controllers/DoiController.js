/**
 * Created by temi varghese on 14/07/15.
 */
profileEditor.controller('DoiController', function (util, $filter) {
    var self = this;
    self.opusId = null;
    self.profileId = null;
    self.uuid = null;
    self.scientificName = null;
    self.pubId = util.getEntityId('publication');

    self.profile = null;
    self.publications = [];
    self.selectedPublication = null;

    var orderBy = $filter("orderBy");

    self.init = function (publications, profile, opus) {
        // makes lastest version appear first
        self.publications = orderBy(publications, 'publicationDate', true);
        self.profile = profile;
        self.opus = opus;
        self.profileId = profile.uuid;
        self.opusId = opus.uuid;
        self.getSelectedPublication();
    };

    self.getSelectedPublication = function () {
        self.publications.forEach(function (item) {
            if (item.uuid == self.pubId) {
                self.selectedPublication = item;
            }
        });
    }
});