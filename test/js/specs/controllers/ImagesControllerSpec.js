describe("ImagesController tests", function () {
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
    var profileDefer, imageDefer;

    var getProfileResponse = '{"profile": {"guid": "guid1", "scientificName":"profileName"}, "opus": {"imageSources": ["source1", "source2"]}}';
    var retrieveImagesResponse = '{"occurrences": [{"largeImageUrl": "url1", "dataResourceName": "name1"}, {"largeImageUrl": "url1", "dataResourceName": "name2"}]}';

    beforeAll(function () {
        console.log("****** Images Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_) {
        scope = $rootScope.$new();
        profileService = _profileService_;

        profileDefer = $q.defer();
        imageDefer = $q.defer();

        spyOn(profileService, "getProfile").and.returnValue(profileDefer.promise);
        spyOn(profileService, "retrieveImages").and.returnValue(imageDefer.promise);

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("ImagesController", {
            $scope: scope,
            profileService: profileService,
            util: mockUtil,
            messageService: messageService
        });
    }));

    it("should set the profile attribute of the current scope when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        imageDefer.resolve(JSON.parse(retrieveImagesResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.profile).toBeDefined();
    });

    it("should set the opus attribute of the current scope when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        imageDefer.resolve(JSON.parse(retrieveImagesResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.opus).toBeDefined();
    });

    it("should set the readonly flag to false when init is called with edit=false", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        imageDefer.resolve(JSON.parse(retrieveImagesResponse));
        scope.init("false");
        scope.$apply();

        expect(scope.readonly).toBe(true);
    });

    it("should set the readonly flag to false when init is called with edit=true", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        imageDefer.resolve(JSON.parse(retrieveImagesResponse));

        scope.init("true");
        scope.$apply();

        expect(scope.readonly).toBe(false);
    });

    it("should set the images array on the scope with the results from the retrieveImages call", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        imageDefer.resolve(JSON.parse(retrieveImagesResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.images).toBeDefined();
        expect(scope.images.length).toBe(2);
    });

    it("should set the firstImage attribute of the array to the first item in the retrieveImages response", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        imageDefer.resolve(JSON.parse(retrieveImagesResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.firstImage).toBeDefined();
        expect(scope.firstImage.largeImageUrl).toBe("url1");
    });

    it("should raise an alert message when the call to getProfile fails", function () {
        profileDefer.reject();
        imageDefer.resolve(JSON.parse(retrieveImagesResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.profile).not.toBeDefined();
        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the profile.");
    });

    it("should raise an alert message when the call to retrieveImages fails", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        imageDefer.reject();

        scope.init("false");
        scope.$apply();

        expect(scope.images.length).toBe(0);
        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the images.");
    });

    it("should add a 'loading images' info message when retrieving the images, and remove it when done", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        imageDefer.resolve(JSON.parse(retrieveImagesResponse));

        scope.init("false");
        scope.$apply();

        expect(messageService.info).toHaveBeenCalledWith("Loading images...");
        expect(messageService.info.calls.count()).toBe(1);
        expect(messageService.pop).toHaveBeenCalledWith();
        expect(messageService.pop.calls.count()).toBe(1);
    });

    it("should use the scientificName to retrieve images if the profile.guid attribute is not present", function () {
        var getProfileResponse = '{"profile": {"guid": "", "scientificName":"profileName"}, "opus": {"imageSources": ["source1", "source2"]}}';

        profileDefer.resolve(JSON.parse(getProfileResponse));
        imageDefer.resolve(JSON.parse(retrieveImagesResponse));

        scope.init("false");
        scope.$apply();

        expect(profileService.retrieveImages).toHaveBeenCalledWith("profileName", "source1,source2");
    });

    it("should use the profile.guid attribute prefixed with 'lsid:' to retrieve images if it is present", function () {
        var getProfileResponse = '{"profile": {"guid": "guid1", "scientificName":"profileName"}, "opus": {"imageSources": ["source1", "source2"]}}';

        profileDefer.resolve(JSON.parse(getProfileResponse));
        imageDefer.resolve(JSON.parse(retrieveImagesResponse));

        scope.init("false");
        scope.$apply();

        expect(profileService.retrieveImages).toHaveBeenCalledWith("lsid:guid1", "source1,source2");
    });
});