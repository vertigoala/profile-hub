/**
 * Created by var03f on 14/07/15.
 */
profileEditor.controller('DoiController', function(messageService, profileService, util, $filter){
    var self = this;
    self.opusId = null;
    self.uuid = null;
    self.scientificName = null;
    self.pubId = util.getEntityId('publication');

    self.profile = null;
    self.publications = [];
    self.selectedPublication = null;

    var orderBy = $filter("orderBy");

    self.loadPublications= function(){
        var promise = profileService.getPublicationsFromId(self.pubId);
        promise.then(function(data){
            // makes lastest version appear first
            data.publications = orderBy(data.publications, 'publicationDate', true);
            
            self.publications = data.publications;
            self.profile = data;
            self.uuid = data.uuid;
            self.opusId = data.opusId;
            self.scientificName = data.scientificName;
            self.getSelectedPublication();
        },function(){
            messageService.alert('Could not load publications using publication id: ' + self.pubId);
        });
    }

    self.getSelectedPublication = function(){
        var remove = null;
        self.publications.forEach(function(item, index){
            if(item.uuid == self.pubId){
                self.selectedPublication = item;
                remove = index;
            }
        });

        // remove the selected publication from list.
        if(remove !== null){
            self.publications.splice(remove,1);
            console.log("removed publication at index " + remove);
            console.log(self.publications)
            console.log(self.selectedPublication)
        }

    }
});