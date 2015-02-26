describe("ListsController tests", function () {
    var controller;
    var scope;
    var mockUtil = {
        getPathItem: function () {
            return "12345"
        },
        LAST: "last"
    };
    var messageService;
    var profileService;
    var profileDefer, listsDefer;

    var getProfileResponse = '{"profile": {"guid": "guid1", "scientificName":"profileName"}, "opus": {"imageSources": ["source1", "source2"]}}';
    var listsResponse = '[{},{}]';

    beforeAll(function () {
        console.log("****** Lists Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_) {
        scope = $rootScope.$new();
        profileService = _profileService_;

        profileDefer = $q.defer();
        listsDefer = $q.defer();

        spyOn(profileService, "getProfile").and.returnValue(profileDefer.promise);
        spyOn(profileService, "retrieveLists").and.returnValue(listsDefer.promise);

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("ListsEditor", {
            $scope: scope,
            profileService: profileService,
            util: mockUtil,
            messageService: messageService
        });
    }));

    it("should set the profile attribute of the current scope when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        listsDefer.resolve(JSON.parse(listsResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.profile).toBeDefined();
    });

    it("should set the opus attribute of the current scope when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        listsDefer.resolve(JSON.parse(listsResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.opus).toBeDefined();
    });

    it("should set the readonly flag to false when init is called with edit=false", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        listsDefer.resolve(JSON.parse(listsResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.readonly).toBe(true);
    });

    it("should set the readonly flag to false when init is called with edit=true", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        listsDefer.resolve(JSON.parse(listsResponse));;

        scope.init("true");
        scope.$apply();

        expect(scope.readonly).toBe(false);
    });

    it("should set the lists array on the scope with the results from the retrieveLists call", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        listsDefer.resolve(JSON.parse(listsResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.lists.length).toBe(2);
    });

    it("should raise an alert message when the call to getProfile fails", function () {
        profileDefer.reject();
        listsDefer.resolve(JSON.parse(listsResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.profile).not.toBeDefined();
        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the profile.");
    });

    it("should raise an alert message when the call to retrieveLists fails", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        listsDefer.reject();

        scope.init("false");
        scope.$apply();

        expect(scope.lists.length).toBe(0);
        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the lists.");
    });

    it("should add a 'loading lists' info message when retrieving the lists, and remove it when done", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        listsDefer.resolve(JSON.parse(listsResponse));

        scope.init("false");
        scope.$apply();

        expect(messageService.info).toHaveBeenCalledWith("Loading lists...");
        expect(messageService.info.calls.count()).toBe(1);
        expect(messageService.pop).toHaveBeenCalledWith();
        expect(messageService.pop.calls.count()).toBe(1);
    });

    it("should not attempt to load lists if the profile.guid attribute is not present", function () {
        var getProfileResponse = '{"profile": {"guid": "", "scientificName":"profileName"}, "opus": {"imageSources": ["source1", "source2"]}}';
        profileDefer.resolve(JSON.parse(getProfileResponse));
        listsDefer.resolve(JSON.parse(listsResponse));

        scope.init("false");
        scope.$apply();

        expect(profileService.retrieveLists).not.toHaveBeenCalled();
        expect(messageService.info).not.toHaveBeenCalled();
    });

});