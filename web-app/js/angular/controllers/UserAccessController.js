/**
 * Controller for managing the user access for an opus
 */
profileEditor.controller('UserAccessController', function (messageService, util, profileService) {
    var self = this;

    self.admins = [];
    self.editors = [];
    self.searchTerm = "";
    self.retrievedUser = null;
    self.opusId = util.getEntityId("opus");

    loadOpus();

    self.save = function (form) {
        var promise = profileService.updateUsers(self.opusId, self.admins, self.editors);
        promise.then(function() {
            form.$setPristine();

            messageService.success("User access has been successfully updated.");
        }, function() {
            messageService.alert("An error has occurred while updating user access.");
        });
    };

    self.remove = function (group, idx, form) {
        if (group == 'admins') {
            self.admins.splice(idx, 1);
        }

        if (group == 'editors') {
            self.editors.splice(idx, 1);
        }

        form.$setDirty();
    };

    self.addAdmin = function (form) {
        self.error = null;
        if (self.retrievedUser != null) {
            if (!containsUser(self.retrievedUser, self.admins)) {
                self.admins.push(self.retrievedUser);

                form.$setDirty();
            }
        }
    };

    self.addEditor = function (form) {
        self.error = null;
        if (self.retrievedUser != null) {
            if (!containsUser(self.retrievedUser, self.editors)) {
                self.editors.push(self.retrievedUser);

                form.$setDirty();
            }
        }
    };

    self.userSearch = function () {
        self.retrievedUser = null;

        var promise = profileService.userSearch(self.searchTerm);
        promise.then(function (data) {
                self.error = null;
                self.retrievedUser = data;
                self.retrievedUser.displayName = data.firstName + ' ' + data.lastName;
            },
            function (response) {
                if (response && response.status != 404) {
                    self.error = "No matching user was found."
                } else {
                    messageService.alert("An error has occurred while searching for the user.");
                }
            }
        );
    };

    self.reset = function(form) {
        self.retrievedUser = null;
        self.searchTerm = null;
        loadOpus(form);
    };

    function loadOpus(form) {
        if (!self.opusId) {
            return;
        }

        var promise = profileService.getOpus(self.opusId);

        promise.then(function (data) {
                console.log("Retrieved " + data.title);
                self.admins = data.admins;

                angular.forEach(self.admins, function(user) {
                    popupateUserDetails(user)
                });

                self.editors = data.editors;
                angular.forEach(self.editors, function(user) {
                    popupateUserDetails(user)
                });

                if (form) {
                    form.$setPristine();
                }
            },
            function () {
                messageService.alert("An error occurred while retrieving the opus.");
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

    function popupateUserDetails(user) {
        var promise = profileService.userSearch(user.userId);
        promise.then(function (data) {
                user.email = data.email;
                user.userName = data.userName;
                user.firstName = data.firstName;
                user.lastName = data.lastName;
                user.displayName = data.firstName + ' ' + data.lastName;
            },
            function () {
                messageService.alert("An error has occurred while searching for the user.");
            }
        );
    }
});