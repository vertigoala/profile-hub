/**
 * Controller for querying the current user's details
 */
profileEditor.controller('UserDetailController', function (profileService, $filter, util) {
    var self = this;

    self.user = {};
    self.opusId = util.getEntityId("opus");
    self.userRole = {name: "User", key: "ROLE_USER", order: 5, colour: ''};
    self.roles = [
        {name: "ALA Admin", key: "ROLE_ADMIN", order: 1, colour: 'color--red'},
        {name: "Admin", key: "ROLE_PROFILE_ADMIN", order: 2, colour: 'color--primary-red'},
        {name: "Editor", key: "ROLE_PROFILE_EDITOR", order: 3, colour: 'color--green'},
        {name: "Editor Plus", key: "ROLE_PROFILE_EDITOR_PLUS", order: 4, colour: 'color--green'},
        {name: "Reviewer", key: "ROLE_PROFILE_REVIEWER", order: 5, colour: 'color--medium-blue'},
        self.userRole];

    var orderBy = $filter("orderBy");

    self.loadUserDetails = function() {
        var promise = profileService.getUserDetails(self.opusId);
        promise.then(function (data) {
            self.user = data;

            var roles = [];
            angular.forEach(self.user.roles, function (authority) {
                var role = findRole(authority);
                if (role != null) {
                    roles.push(role);
                }
            });

            if (roles.length > 0) {
                self.user.role = orderBy(roles, "order")[0];
            } else {
                self.user.role = self.userRole;
            }

            self.tooltip = self.user.displayName + (self.user.role && self.user.role != self.userRole ? ' (' + self.user.role.name + ")" : '');
        });
    };

    self.loadUserDetails();

    function findRole(roleName) {
        var match = null;

        angular.forEach(self.roles, function (role) {
            if (role.key == roleName) {
                match = role;
            }
        });

        return match;
    }
});