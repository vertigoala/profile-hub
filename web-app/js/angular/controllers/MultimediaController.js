/**
 * Created by Javier Molina on 09/06/16.
 */
profileEditor.controller('MultimediaController', function (util, $filter, profileService, messageService) {
    var self = this;

    self.documents = [];
    self.readonly;

    self.init = function (model, edit) {
        var options = JSON.parse(model);

        options.documentResourceAdmin =  edit == 'true';
        self.readonly = !options.documentResourceAdmin;
        self.documents = options.documents;
        var docListViewModel = new DocListViewModel(options.documents || [], options);
        ko.applyBindings(docListViewModel, document.getElementById('resourceList'));
    };
});