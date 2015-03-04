describe("OpusController tests", function () {
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
    var opusDefer, getResourceDefer, listResourcesDefer;

    var getOpusResponse = '{"opus": {"dataResourceUid":"dataUid1", "imageSources": ["source1", "source2"]}}';
    var getResourceResponse = '{"pubDescription":"resource description"}';
    var listResourceResponse = '[{"name": "resourc21","uri": "http://resource1","uid": "res1"}, {"name": "resource2","uri": "http://resource2","uid": "res2"}]';

    beforeAll(function () {
        console.log("****** Opus Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_) {
        scope = $rootScope.$new();
        profileService = _profileService_;

        opusDefer = $q.defer();
        getResourceDefer = $q.defer();
        listResourcesDefer = $q.defer();

        spyOn(profileService, "getOpus").and.returnValue(opusDefer.promise);
        spyOn(profileService, "getResource").and.returnValue(getResourceDefer.promise);
        spyOn(profileService, "listResources").and.returnValue(listResourcesDefer.promise);

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("OpusController as opusCtrl", {
            $scope: scope,
            profileService: profileService,
            util: mockUtil,
            messageService: messageService
        });
    }));

    it("should set the opus attribute of the current scope when the controller is loaded", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        scope.$apply();

        expect(profileService.getOpus).toHaveBeenCalled();
        expect(scope.opusCtrl.opus).toBeDefined();
    });


    it("should set the data resources map on the scope when the controller loads", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        listResourcesDefer.resolve(JSON.parse(listResourceResponse));

        scope.$apply();

        expect(scope.opusCtrl.dataResources.length).toBe(2);
    });

    it("should set the opus description on the scope when the controller loads", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        getResourceDefer.resolve(JSON.parse(getResourceResponse));

        scope.$apply();

        expect(scope.opusCtrl.opusDescription).toBe("resource description");
    });

    it("should raise an alert message when the call to getOpus fails", function () {
        opusDefer.reject();

        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the opus.");
    });

});
