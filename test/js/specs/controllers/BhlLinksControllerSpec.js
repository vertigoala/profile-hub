describe("BHLLinksController tests", function () {
    var controller;
    var scope;
    var mockUtil = {
        getPathItem: function () {
            return "12345"
        },
        getPathItemFromUrl: function(index, url) {
            return "98765"
        },
        LAST: "last"
    };
    var messageService;
    var profileService;
    var profileDefer, saveLinksDefer;

    var getProfileResponse = '{"profile": {"guid": "guid1", "scientificName":"profileName", "bhl":[{},{}]}, "opus": {"imageSources": ["source1", "source2"]}}';

    beforeAll(function () {
        console.log("****** BHL Links Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_) {
        scope = $rootScope.$new();
        profileService = _profileService_;

        profileDefer = $q.defer();
        saveLinksDefer = $q.defer();
        bhlPageDefer = $q.defer();

        spyOn(profileService, "getProfile").and.returnValue(profileDefer.promise);
        spyOn(profileService, "updateBhlLinks").and.returnValue(saveLinksDefer.promise);
        spyOn(profileService, "lookupBhlPage").and.returnValue(bhlPageDefer.promise);

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("BHLLinksEditor", {
            $scope: scope,
            profileService: profileService,
            util: mockUtil,
            messageService: messageService
        });
    }));

    it("should set the profile attribute of the current scope when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.profile).toBeDefined();
    });

    it("should set the opus attribute of the current scope when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.opus).toBeDefined();
    });

    it("should set the readonly flag to false when init is called with edit=false", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.readonly).toBe(true);
    });

    it("should set the readonly flag to false when init is called with edit=true", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));

        scope.init("true");
        scope.$apply();

        expect(scope.readonly).toBe(false);
    });

    it("should set the links array on the scope with the results from the getProfile call", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));

        scope.init("false");
        scope.$apply();

        expect(scope.bhl.length).toBe(2);
    });

    it("should raise an alert message when the call to getProfile fails", function () {
        profileDefer.reject();

        scope.init("false");
        scope.$apply();

        expect(scope.bhl.length).toBe(0);
        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the Biodiversity Heritage References.");
    });

    it("should create a new empty link object at the start of the list when addLink is invoked", function () {
        scope.bhl = [{"url": "blabla", "title": "linkTitle", "description": "desc"}];
        scope.addLink();

        expect(scope.bhl.length).toBe(2);
        expect(scope.bhl[0].uuid).not.toBeDefined();
        expect(scope.bhl[0].title).toBe("");
        expect(scope.bhl[0].url).toBe("");
        expect(scope.bhl[0].description.length).toBe(0);
        expect(scope.bhl[0].thumbnailUrl.length).toBe(0);
    });

    it("it should remove the specified link from the list when deleteLink is invoked", function () {
        scope.bhl = [{"url": "url1", "title": "first", "description": "desc1"},
            {"url": "url2", "title": "second", "description": "desc2"},
            {"url": "url3", "title": "third", "description": "desc3"}];
        scope.deleteLink(1);

        expect(scope.bhl.length).toBe(2);
        expect(scope.bhl[0].title).toBe("first");
        expect(scope.bhl[1].title).toBe("third");
    });

    it("should invoke the updateLinks method of the profile service with the correct values when saveLinks is invoked", function () {
        var links = '[{"uuid":"uuid1","url":"url1","title":"first","description":"desc1"},{"uuid":"uuid1","url":"url2","title":"second","description":"desc2"}]';
        scope.profile = {"uuid": "profileId1"};
        scope.bhl = JSON.parse(links);

        saveLinksDefer.resolve("bla");
        scope.saveLinks();
        scope.$apply();

        var data = '{"profileId":"profileId1","links":' + links + '}';

        expect(profileService.updateBhlLinks).toHaveBeenCalledWith("profileId1", data);
        expect(messageService.success).toHaveBeenCalled();
    });

    it("should raise an alert message if the call to saveLinks fails", function () {
        var links = '[{"uuid":"uuid1","url":"url1","title":"first","description":"desc1"},{"uuid":"uuid1","url":"url2","title":"second","description":"desc2"}]';
        scope.profile = {"uuid": "profileId1"};
        scope.bhl = JSON.parse(links);

        saveLinksDefer.reject();
        scope.saveLinks();
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalled();
    });

    it("should do nothing if updateThumbnail is invoked but the URL is blank", function() {
        var links = '[{"uuid":"uuid1","url":"url1","title":"first","description":"desc1"},{"uuid":"uuid1","url":"url2","title":"second","description":"desc2"}]';
        scope.profile = {"uuid": "profileId1"};
        scope.bhl = JSON.parse(links);
        scope.bhl[0].url = "";

        scope.updateThumbnail(0);

        expect(profileService.lookupBhlPage).not.toHaveBeenCalled();
    });

    it("should retrieve the page information when updateThumbnail is invoked", function() {
        var links = '[{"uuid":"uuid1","url":"url1","title":"first","description":"desc1"},{"uuid":"uuid1","url":"url2","title":"second","description":"desc2"}]';
        scope.profile = {"uuid": "profileId1"};
        scope.bhl = JSON.parse(links);

        bhlPageDefer.resolve(JSON.parse('{"thumbnailUrl": "thumbnailUrl1", "Result": {"FullTitle": "title", "Edition": "ed1", "PublisherName": "publisher", "Doi": "doi1"}}'));
        scope.updateThumbnail(0);
        scope.$apply();

        expect(profileService.lookupBhlPage).toHaveBeenCalled();
        expect(scope.bhl[0].thumbnailUrl).toBe("thumbnailUrl1");
        expect(scope.bhl[0].fullTitle).toBe("title");
        expect(scope.bhl[0].edition).toBe("ed1");
        expect(scope.bhl[0].publisherName).toBe("publisher");
        expect(scope.bhl[0].doi).toBe("doi1");
    });

    it ("should raise an alert message when the call to lookupPage fails", function() {
        var links = '[{"uuid":"uuid1","url":"url1","title":"first","description":"desc1"},{"uuid":"uuid1","url":"url2","title":"second","description":"desc2"}]';
        scope.profile = {"uuid": "profileId1"};
        scope.bhl = JSON.parse(links);

        bhlPageDefer.reject();
        scope.updateThumbnail(0);
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("Failed to lookup page information from the biodiversity heritage library.");
    });
});