describe("OpusController tests", function () {
    var controller;
    var scope;
    var mockUtil = {
        getPathItem: function () {
            return "12345"
        },
        LAST: "last"
    };
    var form;
    var messageService;
    var profileService;
    var opusDefer, getResourceDefer, listResourcesDefer, saveOpusDefer;

    var getOpusResponse = '{"title": "OpusName", "dataResourceUid":"dataUid1", "imageSources": ["source1", "source2", "source3"], "recordSources": ["source1", "source2", "source3"], "mapPointColour": "12345"}';
    var getResourceResponse = '{"pubDescription":"resource description"}';
    var listResourceResponse = '{"dr776":" Insect and spider wetland indicator species list","dr774":" Toothed whales found in Australian waters"}';

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
        saveOpusDefer = $q.defer();

        spyOn(profileService, "getOpus").and.returnValue(opusDefer.promise);
        spyOn(profileService, "getResource").and.returnValue(getResourceDefer.promise);
        spyOn(profileService, "listResources").and.returnValue(listResourcesDefer.promise);
        spyOn(profileService, "saveOpus").and.returnValue(saveOpusDefer.promise);

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("OpusController as opusCtrl", {
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

        expect(Object.keys(scope.opusCtrl.dataResources).length).toBe(2);
        expect(scope.opusCtrl.dataResourceList.length).toBe(2);
    });

    it("should set the opus data resource on the scope when the controller loads", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        getResourceDefer.resolve(JSON.parse(getResourceResponse));

        scope.$apply();

        expect(scope.opusCtrl.dataResource).toBeDefined();
        expect(scope.opusCtrl.dataResource.pubDescription).toBe("resource description");
    });

    it("should raise an alert message when the call to getOpus fails", function () {
        opusDefer.reject();

        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the opus.");
    });

    it("should create a new empty image source record when addImageSource is invoked", function () {
        scope.opusCtrl.addImageSource();

        expect(scope.opusCtrl.newImageSources.length).toBe(1);
    });

    it("should create a new empty record source record when addRecordSource is invoked", function () {
        scope.opusCtrl.addRecordSource();

        expect(scope.opusCtrl.newRecordSources.length).toBe(1);
    });

    it("should remove an existing imageSource from the opus when removeImageSource is invoked with 'existing'", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));

        scope.$apply();
        scope.opusCtrl.removeImageSource(1, 'existing', form);

        expect(scope.opusCtrl.opus.imageSources.length).toBe(2);
        expect(scope.opusCtrl.opus.imageSources[0]).toBe("source1");
        expect(scope.opusCtrl.opus.imageSources[1]).toBe("source3");
    });

    it("should remove an imageSource from the list of new sources when removeImageSource is invoked with 'new'", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));

        scope.$apply();
        scope.opusCtrl.newImageSources = ["newSource1", "newSource2", "newSource3"];
        scope.opusCtrl.removeImageSource(1, 'new', form);

        expect(scope.opusCtrl.opus.imageSources.length).toBe(3);
        expect(scope.opusCtrl.newImageSources.length).toBe(2);
        expect(scope.opusCtrl.newImageSources[0]).toBe("newSource1");
        expect(scope.opusCtrl.newImageSources[1]).toBe("newSource3");
    });

    it("should remove an existing recordSource from the opus when removeRecordSource is invoked with 'existing'", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));

        scope.$apply();
        scope.opusCtrl.removeRecordSource(1, 'existing', form);

        expect(scope.opusCtrl.opus.recordSources.length).toBe(2);
        expect(scope.opusCtrl.opus.recordSources[0]).toBe("source1");
        expect(scope.opusCtrl.opus.recordSources[1]).toBe("source3");
    });

    it("should remove an recordSource from the list of new sources when removeRecordSource is invoked with 'new'", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));

        scope.$apply();
        scope.opusCtrl.newRecordSources = ["newSource1", "newSource2", "newSource3"];
        scope.opusCtrl.removeRecordSource(1, 'new', form);

        expect(scope.opusCtrl.opus.recordSources.length).toBe(3);
        expect(scope.opusCtrl.newRecordSources.length).toBe(2);
        expect(scope.opusCtrl.newRecordSources[0]).toBe("newSource1");
        expect(scope.opusCtrl.newRecordSources[1]).toBe("newSource3");
    });

    it("should set the form to Dirty removeRecordSource is invoked", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));

        scope.$apply();
        scope.opusCtrl.newImageSources = ["newSource1", "newSource2", "newSource3"];

        scope.opusCtrl.removeImageSource(1, 'new', form);
        expect(form.$setDirty).toHaveBeenCalled();
    });

    it("should set the form to Dirty removeRecordSource is invoked", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));

        scope.$apply();
        scope.opusCtrl.newRecordSources = ["newSource1", "newSource2", "newSource3"];
        scope.opusCtrl.removeRecordSource(1, 'new', form);

        expect(form.$setDirty).toHaveBeenCalled();
    });

    it("should merge newImageSources with the existing image sources when saveImageSources is called", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));

        scope.$apply();
        scope.opusCtrl.newImageSources = [{dataResource: {id: "newId1"}}];

        scope.opusCtrl.saveImageSources(form);

        var expectedOpus = {
            title: "OpusName",
            dataResourceUid: "dataUid1",
            mapPointColour: "12345",
            imageSources: ["source1", "source2", "source3", "newId1"], // new id added here
            recordSources: ["source1", "source2", "source3"]
        };

        expect(profileService.saveOpus).toHaveBeenCalledWith("12345", expectedOpus);
    });

    it("should validate that image sources have an associated data resource object", function() {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        scope.$apply();

        scope.opusCtrl.newImageSources = [{dataResource: {}}];

        scope.opusCtrl.saveImageSources(form);

        expect(profileService.saveOpus).not.toHaveBeenCalledWith();
        expect(messageService.alert).toHaveBeenCalledWith("1 image source is not valid. You must select items from the list.");
    });

    it("should save the opus if no new image sources have been added but existing image sources have been removed", function() {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        scope.$apply();

        scope.opusCtrl.removeImageSource(1, 'existing', form);
        scope.opusCtrl.saveImageSources(form);

        var expectedOpus = {
            title: "OpusName",
            dataResourceUid: "dataUid1",
            mapPointColour: "12345",
            imageSources: ["source1", "source3"], // existing source (index 1) removed
            recordSources: ["source1", "source2", "source3"]
        };

        expect(profileService.saveOpus).toHaveBeenCalledWith("12345", expectedOpus);
    });

    it("should merge newRecordSources with the existing record sources when saveRecordSources is called", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));

        scope.$apply();
        scope.opusCtrl.newRecordSources = [{dataResource: {id: "newId1"}}];

        scope.opusCtrl.saveRecordSources(form);

        var expectedOpus = {
            title: "OpusName",
            dataResourceUid: "dataUid1",
            mapPointColour: "12345",
            imageSources: ["source1", "source2", "source3"],
            recordSources: ["source1", "source2", "source3", "newId1"] // new id added here
        };

        expect(profileService.saveOpus).toHaveBeenCalledWith("12345", expectedOpus);
    });

    it("should validate that record sources have an associated data resource object", function() {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        scope.$apply();

        scope.opusCtrl.newRecordSources = [{dataResource: {}}];

        scope.opusCtrl.saveRecordSources(form);

        expect(profileService.saveOpus).not.toHaveBeenCalledWith();
        expect(messageService.alert).toHaveBeenCalledWith("1 record source is not valid. You must select items from the list.");
    });

    it("should save the opus if no new record sources have been added but existing record sources have been removed", function() {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        scope.$apply();

        scope.opusCtrl.removeRecordSource(1, 'existing', form);
        scope.opusCtrl.saveRecordSources(form);

        var expectedOpus = {
            title: "OpusName",
            dataResourceUid: "dataUid1",
            mapPointColour: "12345",
            imageSources: ["source1", "source2", "source3"],
            recordSources: ["source1", "source3"] // existing source (index 1) removed
        };

        expect(profileService.saveOpus).toHaveBeenCalledWith("12345", expectedOpus);
    });

    it("should raise an alert message if the call to saveOpus fails", function() {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        saveOpusDefer.reject();
        scope.$apply();
        scope.opusCtrl.saveOpus(form);
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("Failed to update OpusName.");
    });

    it("should raise a success message and set the form to pristine if the call to saveOpus succeeds", function() {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        saveOpusDefer.resolve({});
        scope.$apply();
        scope.opusCtrl.saveOpus(form);
        scope.$apply();

        expect(messageService.success).toHaveBeenCalledWith("Successfully updated OpusName.");
        expect(form.$setPristine).toHaveBeenCalled();
    });

});
