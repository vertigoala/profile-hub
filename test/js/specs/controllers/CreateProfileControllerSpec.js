describe("CreateProfileController tests", function () {
    var controller;
    var scope;
    var profileService;

    var searchDefer, createDefer;

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

        searchDefer = $q.defer();
        createDefer = $q.defer();

        spyOn(profileService, "profileSearch").and.returnValue(searchDefer.promise);
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

    it("Should set the error attribute if an error occurs while searching", function() {
        searchDefer.reject();

        scope.profileCtrl.ok();
        scope.$apply();

        expect(scope.profileCtrl.error).toBe("An error occurred while searching for existing profiles.")
    });

    it("should invoke profileSearch to check for existing profiles", function() {
        scope.profileCtrl.scientificName = "name1";
        scope.profileCtrl.ok();

        expect(profileService.profileSearch).toHaveBeenCalledWith("opus1234", "name1", false);
    });

    it("should set the error attribute if the search returns matching records", function() {
        searchDefer.resolve([{},{},{}]);

        scope.profileCtrl.ok();
        scope.$apply();

        expect(scope.profileCtrl.error).toBe("A profile already exists for this scientific name.");
        expect(profileService.createProfile).not.toHaveBeenCalled();
    });

    it("should invoke createProfile when the search returns 0 results", function() {
        searchDefer.resolve([]);

        scope.profileCtrl.ok();
        scope.$apply();

        expect(scope.profileCtrl.error).toBe("");
        expect(profileService.createProfile).toHaveBeenCalled();
    });

    it("should set the error attribute if the call to createProfile fails", function() {
        searchDefer.resolve([]);
        createDefer.reject();

        scope.profileCtrl.ok();
        scope.$apply();

        expect(scope.profileCtrl.error).toBe("An error occurred while creating the profile.");
        expect(modalInstance.close).not.toHaveBeenCalled();
    });

    it("should set the error attribute if the call to createProfile returns a null", function() {
        searchDefer.resolve([]);
        createDefer.resolve(null);

        scope.profileCtrl.ok();
        scope.$apply();

        expect(scope.profileCtrl.error).toBe("An error occurred while creating the profile.");
        expect(modalInstance.close).not.toHaveBeenCalled();
    });

    it("should close the modal instance, passing in the new profile, when createProfile succeeds", function() {
        var profile = {uuid: "newuuid"};

        searchDefer.resolve([]);
        createDefer.resolve(profile);

        scope.profileCtrl.ok();
        scope.$apply();

        expect(modalInstance.close).toHaveBeenCalledWith(profile);
    });
});

