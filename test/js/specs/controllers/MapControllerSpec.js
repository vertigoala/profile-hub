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
    var http;

    var getProfileResponse = '{"profile": {"guid": "guid1", "scientificName":"profileName", "links":[{},{}]}, "opus": {"imageSources": ["source1", "source2"], "mapDefaultLatitude": "123", "mapDefaultLongitude": "987", "mapZoom": "2", "mapAttribution":"mapAttr1"}}';

    beforeAll(function () {
        console.log("****** Map Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_, _$httpBackend_) {
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

        http = _$httpBackend_;
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

    it("should specify the map center when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));

        scope.init("biocacheWmsUrl", "biocacheInfoUrl");
        scope.$apply();

        expect(scope.center).toEqual({lat: '123', lng: '987', zoom: '2'});
    });

    it("should specify the base layer when the controller loads", function () {
        expect(scope.layers.baselayers.xyz).toBeDefined();
        expect(scope.layers.baselayers.xyz.name).toBe("Street");
        expect(scope.layers.baselayers.xyz.layerParams).toBeDefined();
    });

    it("should register a click event listener when the controller loads", function () {
        expect(scope.events).toEqual({map: {enable: ['click'], logic: 'emit'}});
    });

    it("should specify the overlay layer for the profile when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));

        scope.init("http://biocacheWmsUrl/bla?q=", "biocacheInfoUrl");
        scope.$apply();

        expect(scope.layers.overlays.wms).toBeDefined();
        expect(scope.layers.overlays.wms.name).toBe("profileName");
        expect(scope.layers.overlays.wms.visible).toBe(true);
        expect(scope.layers.overlays.wms.url).toBe("http://biocacheWmsUrl/bla?q=lsid%3Aguid1");
        expect(scope.layers.overlays.wms.layerOptions.attribution).toBe("mapAttr1");
    });

    it("should raise an alert message when the call to getProfile fails", function () {
        profileDefer.reject();

        scope.init("biocacheWmsUrl", "biocacheInfoUrl");
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the map information.");
    });

});