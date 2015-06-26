describe("CreateProfileController tests", function () {
    var controller;
    var scope;
    var profileService;

    var createDefer;

    var modalInstance = {
        dismiss: function(d) {},
        close: function(d) {}
    };


    beforeAll(function () {
        console.log("****** Create Profile Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q) {
        scope = $rootScope.$new();
        profileService = _profileService_;

        createDefer = $q.defer();

        spyOn(profileService, "createProfile").and.returnValue(createDefer.promise);

        spyOn(modalInstance, "close");
        spyOn(modalInstance, "dismiss");

        controller = $controller("CreateProfileController as profileCtrl", {
            $scope: scope,
            profileService: profileService,
            $modalInstance: modalInstance,
            opusId: "opus1234"
        });

    }));

    it("should dismiss the modal when cancel is invoked", function() {
        scope.profileCtrl.cancel();

        expect(modalInstance.dismiss).toHaveBeenCalled();
    });

    it("should set the error attribute if the call to createProfile fails", function() {
        createDefer.reject();

        scope.profileCtrl.ok();
        scope.$apply();

        expect(scope.profileCtrl.error).toBe("An error occurred while creating the profile.");
        expect(modalInstance.close).not.toHaveBeenCalled();
    });

    it("should set the error attribute if the call to createProfile returns a null", function() {
        createDefer.resolve(null);

        scope.profileCtrl.ok();
        scope.$apply();

        expect(scope.profileCtrl.error).toBe("An error occurred while creating the profile.");
        expect(modalInstance.close).not.toHaveBeenCalled();
    });

    it("should close the modal instance, passing in the new profile, when createProfile succeeds", function() {
        var profile = {uuid: "newuuid"};

        createDefer.resolve(profile);

        scope.profileCtrl.ok();
        scope.$apply();

        expect(modalInstance.close).toHaveBeenCalledWith(profile);
    });
});

