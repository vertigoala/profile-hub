describe("ProfileController tests", function () {
    var PROFILE_ID = "281ff2a5-e8d9-43ff-aff9-e20144b6a94c";
    var controller;
    var scope;
    var form;
    var messageService;
    var profileService;
    var util;
    var window;
    var profileDefer, deleteDefer, confirmDefer;

    var getProfileResponse = '{"profile": {"guid": "guid1", "scientificName":"profileName", "attributes":["attr1", "attr2"]}, "opus": {"imageSources": ["source1", "source2"]}}';

    beforeAll(function () {
        console.log("****** Profile Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_, _util_, _$window_) {
        scope = $rootScope.$new();
        profileService = _profileService_;
        util = _util_;
        window = _$window_;

        profileDefer = $q.defer();
        deleteDefer = $q.defer();
        confirmDefer = $q.defer();

        spyOn(profileService, "getProfile").and.returnValue(profileDefer.promise);
        spyOn(profileService, "deleteProfile").and.returnValue(deleteDefer.promise);

        spyOn(util, "getPathItem").and.returnValue(PROFILE_ID);
        spyOn(util, "confirm").and.returnValue(confirmDefer.promise);
        spyOn(util, "contextRoot").and.returnValue("/context");
        spyOn(util, "redirect");

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("ProfileController as profileCtrl", {
            $scope: scope,
            profileService: profileService,
            util: util,
            messageService: messageService,
        });

        form = {
            dirty: false,
            $setPristine: function () {
                this.dirty = false;
            },
            $setDirty: function () {
                this.dirty = true;
            }
        };
        spyOn(form, "$setPristine");
        spyOn(form, "$setDirty");
    }));

    it("should set the profile attribute of the current scope when the controller is loaded", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        scope.$apply();

        expect(profileService.getProfile).toHaveBeenCalled();
        expect(scope.profileCtrl.profile).toBeDefined();
    });

    it("should raise an alert message when getProfile fails", function () {
        profileDefer.reject();
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while loading the profile.");
    });

    it("should display the confirmation popup when deleteProfile is invoked", function() {
        scope.profileCtrl.deleteProfile();

        expect(util.confirm).toHaveBeenCalled();
    });

    it("should do nothing if the confirmation popup is cancelled when deleteProfile is invoked", function() {
        confirmDefer.reject();
        scope.$apply();

        scope.profileCtrl.deleteProfile();

        expect(profileService.deleteProfile).not.toHaveBeenCalled();
    });

    it("should invoke profileService.deleteProfile when deleteProfile is confirmed", function() {
        scope.profileCtrl.profile = {profileId: PROFILE_ID};
        scope.profileCtrl.opus = {uuid: "opusId"};
        confirmDefer.resolve();

        scope.profileCtrl.deleteProfile();
        scope.$apply();

        expect(profileService.deleteProfile).toHaveBeenCalledWith(PROFILE_ID, "opusId");
    });

    it("should redirect to the opus main page when deleteProfile succeeds", function() {
        scope.profileCtrl.profile = {profileId: PROFILE_ID};
        scope.profileCtrl.opus = {uuid: "opusId"};
        confirmDefer.resolve();
        deleteDefer.resolve({});

        scope.profileCtrl.deleteProfile();
        scope.$apply();

        expect(util.redirect).toHaveBeenCalledWith("/context/opus/opusId");
        expect(messageService.alert).not.toHaveBeenCalled();
    });

    it("should raise an alert message when deleteProfile fails", function() {
        scope.profileCtrl.profile = {profileId: PROFILE_ID};
        scope.profileCtrl.opus = {uuid: "opusId"};
        confirmDefer.resolve();
        deleteDefer.reject();

        scope.profileCtrl.deleteProfile();
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while deleting the profile.");
    });
});
