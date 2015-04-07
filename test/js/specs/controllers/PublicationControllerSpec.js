describe("PublicationController tests", function () {
    var controller;
    var scope;
    var mockUtil = {
        getPathItem: function () {
            return "12345"
        },
        getEntityId: function (str) {
            if (str == "opus") {
                return "opusId1"
            } else if (str == "profile") {
                return "profileId1"
            }
        },
        confirm: function() {},
        LAST: "last"
    };
    var form;
    var messageService;
    var profileService;
    var saveDefer, getPubDefer, deleteDefer, confirmDefer;

    var getPublicationsResponse = '[{"authors":"tasd","title":"test","description":"asd","uuid":"543beabb-ba00-41c5-9810-f8047126bf17","uploadDate":"2015-04-06T23:50:42Z","doi":null,"publicationDate":"2015-03-31T13:00:00Z"},{"authors":"aaaa","title":"test","description":"aaa","uuid":"d40dc8b1-326c-44f6-9f73-366eaade574a","uploadDate":"2015-04-02T05:09:30Z","doi":null,"publicationDate":"2015-04-06T14:00:00Z"}]';

    beforeAll(function () {
        console.log("****** Publication Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_) {
        scope = $rootScope.$new();
        profileService = _profileService_;

        saveDefer = $q.defer();
        getPubDefer = $q.defer();
        deleteDefer = $q.defer();
        confirmDefer = $q.defer();

        spyOn(profileService, "getPublications").and.returnValue(getPubDefer.promise);
        spyOn(profileService, "savePublication").and.returnValue(saveDefer.promise);
        spyOn(profileService, "deletePublication").and.returnValue(deleteDefer.promise);

        spyOn(mockUtil, "confirm").and.returnValue(confirmDefer.promise);

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("PublicationController as pubCtrl", {
            $scope: scope,
            profileService: profileService,
            util: mockUtil,
            messageService: messageService
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

    it("should load all publications for the profile when the controller initialises", function () {
        getPubDefer.resolve(JSON.parse(getPublicationsResponse));

        scope.$apply();

        expect(scope.pubCtrl.publications.length).toBe(2);
    });

    it("should sort publications by publicationDate", function () {
        getPubDefer.resolve(JSON.parse(getPublicationsResponse));

        scope.$apply();

        expect(scope.pubCtrl.publications[0].publicationDate).toBe("2015-04-06T14:00:00Z");
        expect(scope.pubCtrl.publications[1].publicationDate).toBe("2015-03-31T13:00:00Z");
    });

    it("should return the most recently published publication when mostRecentPublication is called", function () {
        getPubDefer.resolve(JSON.parse(getPublicationsResponse));

        scope.$apply();

        var pub = scope.pubCtrl.mostRecentPublication();

        expect(pub.publicationDate).toBe("2015-04-06T14:00:00Z");
    });

    it("should clear the newPublication and newFile attributes when cancelNewPublication is called", function () {
        scope.pubCtrl.newPublication = {title: "bla"};
        scope.pubCtrl.newFile = {};

        scope.pubCtrl.cancelNewPublication(form);

        expect(scope.pubCtrl.newPublication).toBe(null);
        expect(scope.pubCtrl.newFile).toBe(null);
        expect(form.$setPristine).toHaveBeenCalled();
    });

    it("should display a confirmation dialog when deletePublication is invoked", function () {
        scope.pubCtrl.deletePublication(0);
        scope.pubCtrl.profileId = "profileId1";
        scope.pubCtrl.opusId = "opusId1";
        scope.$apply();

        expect(mockUtil.confirm).toHaveBeenCalled();
    });

    it("should display a confirmation dialog when deletePublication is invoked", function () {
        scope.pubCtrl.deletePublication(0);
        scope.pubCtrl.profileId = "profileId1";
        scope.pubCtrl.opusId = "opusId1";
        scope.$apply();

        expect(mockUtil.confirm).toHaveBeenCalled();
    });

    it("should not delete the publication if the confirmation is cancelled", function () {
        scope.pubCtrl.profileId = "profileId1";
        scope.pubCtrl.opusId = "opusId1";
        scope.pubCtrl.publications = [{uuid: "uuid1", title: "title1"}];

        confirmDefer.reject();
        scope.pubCtrl.deletePublication(0);
        scope.$apply();

        expect(mockUtil.confirm).toHaveBeenCalled();
        expect(scope.pubCtrl.publications.length).toBe(1);
    });

    it("should invoke profileService if the confirmation has been confirmed", function () {
        scope.pubCtrl.profileId = "profileId1";
        scope.pubCtrl.opusId = "opusId1";
        scope.pubCtrl.publications = [{uuid: "uuid1", title: "title1"}];

        confirmDefer.resolve({});
        deleteDefer.resolve({});
        scope.pubCtrl.deletePublication(0);
        scope.$apply();

        expect(profileService.deletePublication).toHaveBeenCalledWith("opusId1", "profileId1", "uuid1");
        expect(scope.pubCtrl.publications.length).toBe(0);
    });

    it("should raise an alert message if the delete fails", function () {
        scope.pubCtrl.profileId = "profileId1";
        scope.pubCtrl.opusId = "opusId1";
        scope.pubCtrl.publications = [{uuid: "uuid1", title: "title1"}];

        confirmDefer.resolve({});
        deleteDefer.reject();
        scope.pubCtrl.deletePublication(0);
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalled();
        expect(scope.pubCtrl.publications.length).toBe(1);
    });

    it("should invoke the savePublication method of the profile service when savePublication is invoked", function () {
        scope.pubCtrl.profileId = "profileId1";
        scope.pubCtrl.opusId = "opusId1";
        scope.pubCtrl.newPublication = {publicationId: "uuid1", title: "title"};
        scope.pubCtrl.newFile = "a file";

        saveDefer.resolve({});

        scope.pubCtrl.savePublication(form);
        scope.$apply();

        // we cannot actually inspect the content of the FormData object, unfortunately.
        expect(profileService.savePublication).toHaveBeenCalledWith("opusId1", "profileId1", null, jasmine.any(FormData));

        expect(scope.pubCtrl.newPublication).toBe(null);
        expect(scope.pubCtrl.newFile).toBe(null);
    });

    it("should set the form to pristine after the publication is successfully saved", function () {
        scope.pubCtrl.newPublication = {"uuid": "uuid1", "title": "title"};

        saveDefer.resolve({});
        saveDefer.resolve({});

        scope.pubCtrl.savePublication(form);
        scope.$apply();

        expect(form.$setPristine).toHaveBeenCalled();
    });
});