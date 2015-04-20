/**
 * Controller for managing the user access for an opus
 */
profileEditor.controller('UserAccessController', function (messageService, util, profileService, $modal) {
    var self = this;

    self.users = [];
    self.opusId = util.getEntityId("opus");
    self.roles = [{name: "Admin", key: "ROLE_PROFILE_ADMIN"}, {name: "Editor", key: "ROLE_PROFILE_EDITOR"}, {name: "Reviewer", key: "ROLE_PROFILE_REVIEWER"}];

    loadOpus();

    self.save = function (form) {
        var promise = profileService.updateUsers(self.opusId, self.users);
        promise.then(function () {
            form.$setPristine();

            messageService.success("User access has been successfully updated.");
        }, function () {
            messageService.alert("An error has occurred while updating user access.");
        });
    };

    self.deleteUser = function (userId, form) {
        var confirm = util.confirm("Are you sure you wish to remove this user?");

        confirm.then(function() {
            var index = -1;

            angular.forEach(self.users, function(u, idx) {
                if (u.userId == userId) {
                    index = idx;
                }
            });

            if (index > -1) {
                self.users.splice(index, 1);
                form.$setDirty();
            }
        });
    };

    self.editUser = function(userId, form) {
        var user = null;
        var index = -1;

        angular.forEach(self.users, function(u, idx) {
            if (u.userId == userId) {
                user = u;
                index = idx;
            }
        });

        var popup = showPopup(angular.copy(user));

        popup.result.then(function(data) {
            self.users[index] = data;

            form.$setDirty();
        });
    };

    self.addUser = function(form) {
        var popup = showPopup({});

        popup.result.then(function(data) {
            self.users.push(data);

            form.$setDirty();
        });
    };

    function showPopup(user) {
        return $modal.open({
            templateUrl: "addEditUserPopup.html",
            controller: "AddEditUserController",
            controllerAs: "addUserCtrl",
            size: "sm",
            resolve: {
                users: function() {
                    return self.users;
                },
                isNewUser: function() {
                    return user.userId == null;
                },
                roles: function() {
                    return self.roles;
                },
                user: function() {
                    return user;
                }
            }
        });
    }

    self.reset = function (form) {
        loadOpus(form);
    };

    function loadOpus(form) {
        if (!self.opusId) {
            return;
        }

        var promise = profileService.getOpus(self.opusId);

        promise.then(function (data) {
                console.log("Retrieved " + data.title);
                self.users = data.authorities;

                angular.forEach(self.users, function(user) {
                    popupateUserDetails(user);
                });

                if (form) {
                    form.$setPristine();
                }
            },
            function () {
                messageService.alert("An error occurred while retrieving the opus.");
            }
        );
    }

    function popupateUserDetails(user) {
        var promise = profileService.userSearch(user.userId);
        promise.then(function (data) {
                user.email = data.email;
                user.userName = data.userName;
                user.firstName = data.firstName;
                user.lastName = data.lastName;
                user.name = data.firstName + ' ' + data.lastName;
            },
            function () {
                messageService.alert("An error has occurred while searching for the user.");
            }
        );
    }

});


/**
 * Add/edit User modal dialog controller
 */
profileEditor.controller('AddEditUserController', function ($modalInstance, profileService, users, user, roles, isNewUser) {
    var self = this;

    self.users = users;
    self.roles = roles;
    self.searchTerm = "";
    self.user = user;
    self.isNewUser = isNewUser;

    self.userSearch = function () {
        self.user = null;

        var promise = profileService.userSearch(self.searchTerm);
        promise.then(function (data) {
                console.log(JSON.stringify(data))
                if (containsUser(data.userId)) {
                    self.error = "This user has already been assigned a role.";
                } else {
                    self.error = null;

                    self.user = {
                        userId: data.userId,
                        name: data.firstName + ' ' + data.lastName,
                        email: data.email,
                        notes: "",
                        role: null
                    };
                }
            },
            function (response) {
                if (response && response.status != 404) {
                    self.error = "No matching user was found."
                } else {
                    self.error = "An error has occurred while searching for the user.";
                }
            }
        );
    };

    self.ok = function() {
        $modalInstance.close(self.user);
    };

    self.cancel = function() {
        $modalInstance.dismiss("cancel");
    };

    function containsUser(userId) {
        for (var i = 0; i < self.users.length; i++) {
            if (self.users[i].userId === userId) {
                return true;
            }
        }
        return false;
    }
});