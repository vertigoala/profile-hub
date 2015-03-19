describe("SearchController tests", function () {
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
    var searchDefer;

    var searchResponse = '[{"guid":"null","scientificName":"Acacia excelsa subsp. angusta","profileId":"e077b602-2a53-44f2-988d-6b54a35f323c"},{"guid":"null","scientificName":"Acacia excelsa subsp. excelsa","profileId":"63155a38-ba7e-4d33-8432-3a9a3556f588"}]';

    beforeAll(function () {
        console.log("****** Search Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_) {
        scope = $rootScope.$new();
        profileService = _profileService_;

        searchDefer = $q.defer();

        spyOn(profileService, "profileSearch").and.returnValue(searchDefer.promise);

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("SearchController as searchCtrl", {
            $scope: scope,
            profileService: profileService,
            util: mockUtil,
            messageService: messageService
        });
    }));

    it("should set the search results attribute of the current scope when search is invoked", function () {
        searchDefer.resolve(JSON.parse(searchResponse));
        scope.searchCtrl.search();
        scope.$apply();

        expect(scope.searchCtrl.profiles.length).toBe(2); // there are 2 results in the dummy searchResponse
    });

    it("should read the opusId from the URL and the searchTerm from the current scope to call profileService.search", function () {
        searchDefer.resolve(JSON.parse(searchResponse));

        var searchTerm = "searchValue";
        scope.searchCtrl.searchTerm = searchTerm;

        scope.searchCtrl.search();
        scope.$apply();

        expect(profileService.profileSearch).toHaveBeenCalledWith("12345", searchTerm, true);
    });
});
