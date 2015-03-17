describe("UserAccessController tests", function () {
    var controller;
    var scope;
    var mockUtil = {};
    var messageService;
    var profileService;
    var searchDefer, updateDefer;

    var user1 = {userId: "1", firstName: "fred", lastName: "smith", email: "fred@smith.com"};
    var user2 = {userId: "2", firstName: "jane", lastName: "doe", email: "jane@doe.com"};
    var user3 = {userId: "3", firstName: "fred", lastName: "bloggs", email: "fred@bloggs.com"};

    var searchUserResponse = JSON.stringify(user1);
    var updateUserResponse = '{}';

    beforeAll(function () {
        console.log("****** User Access Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_) {
        scope = $rootScope.$new();
        profileService = _profileService_;

        searchDefer = $q.defer();
        updateDefer = $q.defer();

        spyOn(profileService, "userSearch").and.returnValue(searchDefer.promise);
        spyOn(profileService, "updateUsers").and.returnValue(updateDefer.promise);

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("UserAccessController as userCtrl", {
            $scope: scope,
            profileService: profileService,
            util: mockUtil,
            messageService: messageService
        });
    }));

    it("should add the specified user to the admin list when addAdmin is called", function () {
        scope.userCtrl.retrievedUser = user1;
        scope.userCtrl.addAdmin();

        expect(scope.userCtrl.admins.length).toBe(1);
        expect(scope.userCtrl.editors.length).toBe(0);
    });

    it("should add the specified user to the editors list when addEditor is called", function () {
        scope.userCtrl.retrievedUser = user1;
        scope.userCtrl.addEditor();

        expect(scope.userCtrl.admins.length).toBe(0);
        expect(scope.userCtrl.editors.length).toBe(1);
    });

    it("should not add the same user to the admin list when addAdmin is called", function () {
        scope.userCtrl.retrievedUser = user1;
        scope.userCtrl.addAdmin();
        scope.userCtrl.addAdmin();

        expect(scope.userCtrl.admins.length).toBe(1);
        expect(scope.userCtrl.editors.length).toBe(0);
    });

    it("should not the same user to the editors list when addEditor is called", function () {
        scope.userCtrl.retrievedUser = user1;
        scope.userCtrl.addEditor();
        scope.userCtrl.addEditor();

        expect(scope.userCtrl.admins.length).toBe(0);
        expect(scope.userCtrl.editors.length).toBe(1);
    });

    it("should add multiple users to the admin list when addAdmin is called", function () {
        scope.userCtrl.retrievedUser = user1;
        scope.userCtrl.addAdmin();
        scope.userCtrl.retrievedUser = user2;
        scope.userCtrl.addAdmin();

        expect(scope.userCtrl.admins.length).toBe(2);
        expect(scope.userCtrl.editors.length).toBe(0);
    });

    it("should add multiple users to the editors list when addEditor is called", function () {
        scope.userCtrl.retrievedUser = user1;
        scope.userCtrl.addEditor();
        scope.userCtrl.retrievedUser = user2;
        scope.userCtrl.addEditor();

        expect(scope.userCtrl.admins.length).toBe(0);
        expect(scope.userCtrl.editors.length).toBe(2);
    });

    it("should do nothing addAdmin is called but the retrieved user is null", function () {
        scope.userCtrl.addAdmin();

        expect(scope.userCtrl.admins.length).toBe(0);
        expect(scope.userCtrl.editors.length).toBe(0);
    });

    it("should do nothing addEditor is called but the retrieved user is null", function () {
        scope.userCtrl.addEditor();

        expect(scope.userCtrl.admins.length).toBe(0);
        expect(scope.userCtrl.editors.length).toBe(0);
    });

    it("should remove the specified admin from the admins list", function() {
        scope.userCtrl.admins.push(user1);
        scope.userCtrl.admins.push(user2);
        scope.userCtrl.admins.push(user3);

        scope.userCtrl.remove('admins', 1);
        expect(scope.userCtrl.admins.length).toBe(2);
        expect(scope.userCtrl.admins[0].userId).toBe("1");
        expect(scope.userCtrl.admins[1].userId).toBe("3");
    });

    it("should not touch the editors list when remove is called for the admin group", function() {
        scope.userCtrl.admins.push(user1);
        scope.userCtrl.editors.push(user1);

        scope.userCtrl.remove('admins', 0);
        expect(scope.userCtrl.admins.length).toBe(0);
        expect(scope.userCtrl.editors.length).toBe(1);
    });

    it("should remove the specified editor from the editors list", function() {
        scope.userCtrl.editors.push(user1);
        scope.userCtrl.editors.push(user2);
        scope.userCtrl.editors.push(user3);

        scope.userCtrl.remove('editors', 1);
        expect(scope.userCtrl.editors.length).toBe(2);
        expect(scope.userCtrl.editors[0].userId).toBe("1");
        expect(scope.userCtrl.editors[1].userId).toBe("3");
    });

    it("should not touch the admins list when remove is called for the editors group", function() {
        scope.userCtrl.admins.push(user1);
        scope.userCtrl.editors.push(user1);

        scope.userCtrl.remove('editors', 0);
        expect(scope.userCtrl.admins.length).toBe(1);
        expect(scope.userCtrl.editors.length).toBe(0);
    });

    it("should not do anything when remove (admins) is called when the admins list is empty", function() {
        scope.userCtrl.remove('admins', 1);

        expect(scope.userCtrl.admins.length).toBe(0);
    });

    it("should not do anything when remove (editors) is called when the editors list is empty", function() {
        scope.userCtrl.remove('editors', 1);

        expect(scope.userCtrl.editors.length).toBe(0);
    });

    it("should raise a success message when the call to updateUsers succeeds", function () {
        updateDefer.resolve(JSON.parse(updateUserResponse));

        scope.userCtrl.save();
        scope.$apply();

        expect(messageService.success).toHaveBeenCalledWith("User access has been successfully updated.");
    });

    it("should raise an alert message when the call to updateUsers fails", function () {
        updateDefer.reject();

        scope.userCtrl.save();
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("An error has occurred while updating user access.");
    });

    it("should set the retrievedUser attribute on the current scope when the call to searchUser finds a match", function () {
        searchDefer.resolve(user1);
        scope.searchTerm = "test";
        scope.userCtrl.userSearch();
        scope.$apply();

        expect(scope.userCtrl.retrievedUser.email).toBe(user1.email);
    });

    it("should set the retrievedUser.displayName attribute on the current scope when the call to searchUser finds a match", function () {
        searchDefer.resolve(user2);
        scope.searchTerm = "test";
        scope.userCtrl.userSearch();
        scope.$apply();

        expect(scope.userCtrl.retrievedUser.displayName).toBeDefined();
        expect(scope.userCtrl.retrievedUser.displayName).toBe(user2.firstName + ' ' + user2.lastName);
    });

    it("should raise an alert message when the call to searchUser fails", function () {
        searchDefer.reject();

        scope.userCtrl.userSearch();
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("An error has occurred while searching for the user.");
    });
});
