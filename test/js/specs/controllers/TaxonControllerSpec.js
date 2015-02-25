describe("TaxonController tests", function () {
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
    var profileDefer, classificationDefer, speciesProfileDefer;

    var getProfileResponse = '{"profile": {"guid": "guid1", "scientificName":"profileName"}, "opus": {"imageSources": ["source1", "source2"]}}';
    var classifcationResponse = '[{},{}]'; // don't what is in the response, just that there are 2 elements so we can check array sizes
    var speciesProfileResposne = '{}'; // don't care what is in the response

    beforeAll(function () {
        console.log("****** Taxon Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_) {
        scope = $rootScope.$new();
        profileService = _profileService_;

        profileDefer = $q.defer();
        classificationDefer = $q.defer();
        speciesProfileDefer = $q.defer();

        spyOn(profileService, "getProfile").and.returnValue(profileDefer.promise);
        spyOn(profileService, "getClassifications").and.returnValue(classificationDefer.promise);
        spyOn(profileService, "getSpeciesProfile").and.returnValue(speciesProfileDefer.promise);

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("TaxonController", {
            $scope: scope,
            profileService: profileService,
            util: mockUtil,
            messageService: messageService
        });
    }));

    it("should set the profile attribute of the current scope when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        classificationDefer.resolve(JSON.parse(classifcationResponse));
        speciesProfileDefer.resolve(JSON.parse(speciesProfileResposne));

        scope.init("false");
        scope.$apply();

        expect(scope.profile).toBeDefined();
    });

    it("should set the opus attribute of the current scope when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        classificationDefer.resolve(JSON.parse(classifcationResponse));
        speciesProfileDefer.resolve(JSON.parse(speciesProfileResposne));

        scope.init("false");
        scope.$apply();

        expect(scope.opus).toBeDefined();
    });

    it("should set the readonly flag to false when init is called with edit=false", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        classificationDefer.resolve(JSON.parse(classifcationResponse));
        speciesProfileDefer.resolve(JSON.parse(speciesProfileResposne));

        scope.init("false");
        scope.$apply();

        expect(scope.readonly).toBe(true);
    });

    it("should set the readonly flag to false when init is called with edit=true", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        classificationDefer.resolve(JSON.parse(classifcationResponse));
        speciesProfileDefer.resolve(JSON.parse(speciesProfileResposne));

        scope.init("true");
        scope.$apply();

        expect(scope.readonly).toBe(false);
    });

    it("should set the classifications array on the scope with the results from the getClassification call", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        classificationDefer.resolve(JSON.parse(classifcationResponse));
        speciesProfileDefer.resolve(JSON.parse(speciesProfileResposne));

        scope.init("false");
        scope.$apply();

        expect(scope.classifications.length).toBe(2);
    });

    it("should raise an alert message when the call to getProfile fails", function () {
        profileDefer.reject();
        classificationDefer.resolve(JSON.parse(classifcationResponse));
        speciesProfileDefer.resolve(JSON.parse(speciesProfileResposne));

        scope.init("false");
        scope.$apply();

        expect(scope.profile).not.toBeDefined();
        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the profile.");
    });

    it("should raise an alert message when the call to getClassification fails", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        classificationDefer.reject();
        speciesProfileDefer.resolve(JSON.parse(speciesProfileResposne));

        scope.init("false");
        scope.$apply();

        expect(scope.classifications.length).toBe(0);
        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the taxonomy.");
    });

    it("should raise an alert message when the call to getSpeciesProfile fails", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        classificationDefer.resolve(JSON.parse(classifcationResponse));
        speciesProfileDefer.reject();

        scope.init("false");
        scope.$apply();

        expect(scope.speciesProfile).toBeNull();
        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the taxonomy.");
    });

    it("should add a 'loading taxonomy' info message when retrieving the classification and species profile (ie 2 messages), and remove them when done", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        classificationDefer.resolve(JSON.parse(classifcationResponse));
        speciesProfileDefer.resolve(JSON.parse(speciesProfileResposne));

        scope.init("false");
        scope.$apply();

        expect(messageService.info).toHaveBeenCalledWith("Loading taxonomy..."); // twice
        expect(messageService.info.calls.count()).toBe(2);
        expect(messageService.pop).toHaveBeenCalledWith();
        expect(messageService.pop.calls.count()).toBe(2);
    });
});