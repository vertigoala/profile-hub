describe("MapController tests", function () {
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
    var profileDefer, popupDefer;

    var getProfileResponse = '{"profile": {"guid": "guid1", "scientificName":"profileName", "links":[{},{}]}, "opus": {"imageSources": ["source1", "source2"]}}';

    beforeAll(function () {
        console.log("****** Map Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_, $httpBackend) {
        scope = $rootScope.$new();
        profileService = _profileService_;

        profileDefer = $q.defer();
        popupDefer = $q.defer();

        spyOn(profileService, "getProfile").and.returnValue(profileDefer.promise);

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("MapController", {
            $scope: scope,
            profileService: profileService,
            util: mockUtil,
            messageService: messageService
        });
    }));

    it("should set the profile attribute of the current scope when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));

        scope.init("biocacheWmsUrl", "biocacheInfoUrl");
        scope.$apply();

        expect(scope.profile).toBeDefined();
    });

    it("should set the opus attribute of the current scope when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));

        scope.init("biocacheWmsUrl", "biocacheInfoUrl");
        scope.$apply();

        expect(scope.opus).toBeDefined();
    });

    it("should set the biocache urls to the provided values when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));

        scope.init("biocacheWmsUrl", "biocacheInfoUrl");
        scope.$apply();

        expect(scope.biocacheInfoUrl).toBe("biocacheInfoUrl");
        expect(scope.biocacheWMSUrl).toBe("biocacheWmsUrl");
    });

    it("should raise an alert message when the call to getProfile fails", function () {
        profileDefer.reject();

        scope.init("biocacheWmsUrl", "biocacheInfoUrl");
        scope.$apply();

        expect(scope.links.length).toBe(0);
        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the links.");
    });

    it("should raise an alert message if the call to biocache fails", function () {
        popupDefer.reject();

        scope.onMapClick({latlng: {lat: 1, lng: 2}}, "someurl");
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalled();
    });
});