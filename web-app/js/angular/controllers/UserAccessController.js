/**
 * Controller for managing the user access for an opus
 */
profileEditor.controller('UserAccessController', function (messageService, util, profileService) {
    var self = this;

    self.admins = [];
    self.editors = [];
    self.searchTerm = "";
    self.retrievedUser = null;

    self.save = function () {
        var promise = profileService.updateUsers(self.opusId, self.admins, self.editors);
        promise.then(function() {
            messageService.success("User access has been successfully updated.");
        }, function() {
            messageService.alert("An error has occurred while updating user access.");
        });
    };

    self.remove = function (group, idx) {
        if (group == 'admins') {
            self.admins.splice(idx, 1);
        }

        if (group == 'editors') {
            self.editors.splice(idx, 1);
        }
    };

    self.addAdmin = function () {
        if (self.retrievedUser != null) {
            if (!containsUser(self.retrievedUser, self.admins)) {
                self.admins.push(self.retrievedUser);
            }
        }
    };

    self.addEditor = function () {
        if (self.retrievedUser != null) {
            if (!containsUser(self.retrievedUser, self.editors)) {
                self.editors.push(self.retrievedUser);
            }
        }
    };

    self.userSearch = function () {
        self.retrievedUser = null;

        var promise = profileService.userSearch(self.searchTerm);
        promise.then(function (data) {
                self.retrievedUser = data;
                self.retrievedUser.displayName = data.firstName + ' ' + data.lastName;
            },
            function () {
                messageService.alert("An error has occurred while searching for the user.");
            }
        );
    };

    function containsUser(user, users) {
        for (var i = 0; i < users.length; i++) {
            if (users[i].userId === user.userId) {
                return true;
            }
        }
        return false;
    }
});