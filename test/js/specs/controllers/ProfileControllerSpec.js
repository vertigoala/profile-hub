describe("ProfileController tests", function () {
    var PROFILE_ID = "281ff2a5-e8d9-43ff-aff9-e20144b6a94c";
    var controller;
    var scope;
    var form;
    var messageService;
    var profileService;
    var util;
    var window;
    var profileDefer, deleteDefer, confirmDefer, modalDefer;

    var modal = {
        open: function() {}
    };

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
        modalDefer = $q.defer();

        var popup = {
            result: modalDefer.promise
        };

        spyOn(profileService, "getProfile").and.returnValue(profileDefer.promise);
        spyOn(profileService, "deleteProfile").and.returnValue(deleteDefer.promise);

        spyOn(modal, "open").and.returnValue(popup);

        spyOn(util, "getPathItem").and.returnValue(PROFILE_ID);
        spyOn(util, "confirm").and.returnValue(confirmDefer.promise);
        spyOn(util, "contextRoot").and.returnValue("/context");
        spyOn(util, "redirect");
        spyOn(util, "getEntityId").and.returnValue("profileId");

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("ProfileController as profileCtrl", {
            $scope: scope,
            profileService: profileService,
            util: util,
            messageService: messageService,
            $modal: modal
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
        scope.profileCtrl.loadProfile();
        scope.$apply();

        expect(profileService.getProfile).toHaveBeenCalled();
        expect(scope.profileCtrl.profile).toBeDefined();
    });

    it("should raise an alert message when getProfile fails", function () {
        profileDefer.reject();

        scope.profileCtrl.loadProfile();
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
        scope.profileCtrl.profileId = PROFILE_ID;
        scope.profileCtrl.opus = {uuid: "opusId"};
        confirmDefer.resolve();

        scope.profileCtrl.deleteProfile();
        scope.$apply();

        expect(profileService.deleteProfile).toHaveBeenCalledWith("opusId", PROFILE_ID);
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

    it("should open the modal dialog, and redirect to the edit profile page when it is closed, when createProfile is invoked", function() {
        scope.profileCtrl.createProfile("opusId");

        expect(modal.open).toHaveBeenCalledWith(jasmine.objectContaining({templateUrl: "createProfile.html"}));
        expect(modal.open).toHaveBeenCalledWith(jasmine.objectContaining({ controller: "CreateProfileController"}));
        expect(modal.open).toHaveBeenCalledWith(jasmine.objectContaining({controllerAs: "createProfileCtrl"}));
    });

    it("should open the modal dialog, and redirect to the edit profile page when it is closed, when createProfile is invoked", function() {
        modalDefer.resolve({uuid: "newProfileId"});
        scope.profileCtrl.opusId = "opusId1";

        scope.profileCtrl.createProfile("opusId");
        scope.$apply();

        expect(util.redirect).toHaveBeenCalledWith("/context/opus/opusId1/profile/newProfileId/update");
    });

    it("should create a new list when addBibliography is invoked if one doesn't exist", function() {
        scope.profileCtrl.profile = {};
        scope.profileCtrl.addBibliography(form);

        expect(scope.profileCtrl.profile.bibliography.length).toBe(1);
        expect(scope.profileCtrl.profile.bibliography[0].order).toBe(0);
    });

    it("should add new bibliography to the profile's list, with the correct order, when addBibliography is invoked", function() {
        scope.profileCtrl.profile = {bibliography: [{uuid:"id1", order: 0}, {uuid:"id2", order: 1}]};
        scope.profileCtrl.addBibliography(form);

        expect(scope.profileCtrl.profile.bibliography.length).toBe(3);
        expect(scope.profileCtrl.profile.bibliography[2].order).toBe(2);
    });

    it("should remove the specified bibliography item from the profile's list when deleteBibliography is invoked", function() {
        scope.profileCtrl.profile = {bibliography: [{uuid:"id1", order: 0}, {uuid:"id2", order: 1}]};
        scope.profileCtrl.deleteBibliography(1, form);

        expect(scope.profileCtrl.profile.bibliography.length).toBe(1);
        expect(scope.profileCtrl.profile.bibliography[0].order).toBe(0);
    });

    it("should remove the re-order all subsequent bibliography items when deleteBibliography is invoked", function() {
        scope.profileCtrl.profile = {bibliography: [{uuid:"id1", order: 0},
                                                    {uuid:"id2", order: 1},
                                                    {uuid:"id3", order: 2},
                                                    {uuid:"id4", order: 3}]};
        scope.profileCtrl.deleteBibliography(1, form);

        expect(scope.profileCtrl.profile.bibliography.length).toBe(3);
        expect(scope.profileCtrl.profile.bibliography[0].order).toBe(0);
        expect(scope.profileCtrl.profile.bibliography[1].order).toBe(1);
        expect(scope.profileCtrl.profile.bibliography[2].order).toBe(2);
    });

    it("should swap the positions of two items when moveBibliographyUp is invoked", function() {
        scope.profileCtrl.profile = {bibliography: [{uuid:"id1", order: 0},
            {uuid:"id2", order: 1},
            {uuid:"id3", order: 2},
            {uuid:"id4", order: 3}]};
        scope.profileCtrl.moveBibliographyUp(2, form);

        expect(scope.profileCtrl.profile.bibliography[0].uuid).toBe("id1");
        expect(scope.profileCtrl.profile.bibliography[1].uuid).toBe("id3");
        expect(scope.profileCtrl.profile.bibliography[2].uuid).toBe("id2");
        expect(scope.profileCtrl.profile.bibliography[3].uuid).toBe("id4");
    });

    it("should swap the positions of two items when moveBibliographyDown is invoked", function() {
        scope.profileCtrl.profile = {bibliography: [{uuid:"id1", order: 0},
            {uuid:"id2", order: 1},
            {uuid:"id3", order: 2},
            {uuid:"id4", order: 3}]};
        scope.profileCtrl.moveBibliographyDown(2, form);

        expect(scope.profileCtrl.profile.bibliography[0].uuid).toBe("id1");
        expect(scope.profileCtrl.profile.bibliography[1].uuid).toBe("id2");
        expect(scope.profileCtrl.profile.bibliography[2].uuid).toBe("id4");
        expect(scope.profileCtrl.profile.bibliography[3].uuid).toBe("id3");
    });
});

