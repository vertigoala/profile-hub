describe("OpusController tests", function () {
    var OPUS_ID = "281ff2a5-e8d9-43ff-aff9-e20144b6a94c";
    var controller;
    var scope;
    var mockUtil = {
        getPathItem: function () {
            return OPUS_ID;
        },
        getEntityId: function (str) {
            if (str == "opus") {
                return OPUS_ID
            } else if (str == "profile") {
                return "profileId1"
            }
        },
        isUuid: function () {
            return true;
        },
        LAST: "last",
        contextRoot: function () {
            return "/contextRoot";
        },
        redirect: function (location) {
        }
    };
    var form;
    var messageService;
    var profileService;
    var opusDefer, getResourceDefer, listResourcesDefer, saveOpusDefer, listOpusDefer, getListsDefer, keysDefer, supportingCollectionDefer;

    var getOpusResponse = '{"title": "OpusName", "dataResourceUid":"dataUid1", "imageSources": ["source1", "source2", "source3"], "recordSources": ["source1", "source2", "source3"], "mapPointColour": "12345", "supportingOpuses": []}';
    var getResourceResponse = '{"pubDescription":"resource description"}';
    var listResourceResponse = '{"dr776":" Insect and spider wetland indicator species list","dr774":" Toothed whales found in Australian waters"}';
    var getAllListsResponse = '{lists: [{"dataResourceUid": "id4", "listName": "list4"}, {"dataResourceUid": "id2", "listName": "list2"}, {"dataResourceUid": "id3", "listName": "list3"}, {"dataResourceUid": "id1", "listName": "list1"}]}';
    var listOpusResponse = '[{"uuid":"opus1", "title":"Opus 1"}, {"uuid":"opus2", "title":"Opus 2"}, {"uuid":"' + OPUS_ID + '", "title":"Opus 3"}]';

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
        listOpusDefer = $q.defer();
        getListsDefer = $q.defer();
        keysDefer = $q.defer();
        supportingCollectionDefer = $q.defer();

        spyOn(profileService, "getOpus").and.returnValue(opusDefer.promise);
        spyOn(profileService, "getResource").and.returnValue(getResourceDefer.promise);
        spyOn(profileService, "listResources").and.returnValue(listResourcesDefer.promise);
        spyOn(profileService, "saveOpus").and.returnValue(saveOpusDefer.promise);
        spyOn(profileService, "listOpus").and.returnValue(listOpusDefer.promise);
        spyOn(profileService, "getAllLists").and.returnValue(getListsDefer.promise);
        spyOn(profileService, "retrieveKeybaseProjects").and.returnValue(keysDefer.promise);
        spyOn(profileService, "updateSupportingCollections").and.returnValue(supportingCollectionDefer.promise);

        spyOn(mockUtil, "redirect").and.returnValue(null);

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
        scope.opusCtrl.loadOpus();
        scope.$apply();

        expect(profileService.getOpus).toHaveBeenCalled();
        expect(scope.opusCtrl.opus).toBeDefined();
    });


    it("should set the data resources map on the scope when the controller loads", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        listResourcesDefer.resolve(JSON.parse(listResourceResponse));

        scope.opusCtrl.loadOpus();
        scope.$apply();

        expect(Object.keys(scope.opusCtrl.dataResources).length).toBe(2);
        expect(scope.opusCtrl.dataResourceList.length).toBe(2);
    });

    it("should set the opus data resource on the scope when the controller loads", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        getResourceDefer.resolve(JSON.parse(getResourceResponse));

        scope.opusCtrl.loadOpus();
        scope.$apply();

        expect(scope.opusCtrl.dataResource).toBeDefined();
        expect(scope.opusCtrl.dataResource.pubDescription).toBe("resource description");
    });

    it("should raise an alert message when the call to getOpus fails", function () {
        opusDefer.reject();

        scope.opusCtrl.loadOpus();
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

    it("should create a new empty supporting opus record when addSupportingOpus is invoked", function () {
        scope.opusCtrl.addSupportingOpus();

        expect(scope.opusCtrl.newSupportingOpuses.length).toBe(1);
    });

    it("should create a new empty approved list record when addApprovedList is invoked", function () {
        scope.opusCtrl.addApprovedList()

        expect(scope.opusCtrl.newApprovedLists.length).toBe(1);
    });

    it("should create a new empty bio status list record when addBioStatusList is invoked", function () {
        scope.opusCtrl.addBioStatusList()

        expect(scope.opusCtrl.newBioStatusLists.length).toBe(1);
    });

    it("should not add more than one bio status", function () {
        scope.opusCtrl.opus = {bioStatusLists: ["list1"]};
        scope.opusCtrl.addBioStatusList();

        expect(scope.opusCtrl.newBioStatusLists.length).toBe(1);
    });

    it("should remove an existing imageSource from the opus when removeImageSource is invoked with 'existing'", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);

        scope.$apply();
        scope.opusCtrl.removeImageSource(1, 'existing', form);

        expect(scope.opusCtrl.opus.imageSources.length).toBe(2);
        expect(scope.opusCtrl.opus.imageSources[0]).toBe("source1");
        expect(scope.opusCtrl.opus.imageSources[1]).toBe("source3");
    });

    it("should remove an imageSource from the list of new sources when removeImageSource is invoked with 'new'", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);

        scope.$apply();
        scope.opusCtrl.newImageSources = ["newSource1", "newSource2", "newSource3"];
        scope.opusCtrl.removeImageSource(1, 'new', form);

        expect(scope.opusCtrl.opus.imageSources.length).toBe(3);
        expect(scope.opusCtrl.newImageSources.length).toBe(2);
        expect(scope.opusCtrl.newImageSources[0]).toBe("newSource1");
        expect(scope.opusCtrl.newImageSources[1]).toBe("newSource3");
    });

    it("should remove an existing recordSource from the opus when removeRecordSource is invoked with 'existing'", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);

        scope.$apply();
        scope.opusCtrl.removeRecordSource(1, 'existing', form);

        expect(scope.opusCtrl.opus.recordSources.length).toBe(2);
        expect(scope.opusCtrl.opus.recordSources[0]).toBe("source1");
        expect(scope.opusCtrl.opus.recordSources[1]).toBe("source3");
    });

    it("should remove an recordSource from the list of new sources when removeRecordSource is invoked with 'new'", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);

        scope.$apply();
        scope.opusCtrl.newRecordSources = ["newSource1", "newSource2", "newSource3"];
        scope.opusCtrl.removeRecordSource(1, 'new', form);

        expect(scope.opusCtrl.opus.recordSources.length).toBe(3);
        expect(scope.opusCtrl.newRecordSources.length).toBe(2);
        expect(scope.opusCtrl.newRecordSources[0]).toBe("newSource1");
        expect(scope.opusCtrl.newRecordSources[1]).toBe("newSource3");
    });

    it("should remove an existing supporting opus from the opus when removeSupportingOpus is invoked with 'existing'", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.$apply();
        scope.opusCtrl.supportingOpuses = [{opusId: "opus1", title: "Opus 1"}, {
            opusId: "opus2",
            title: "Opus 2"
        }, {opusId: "opus3", title: "Opus 3"}];

        scope.$apply();
        scope.opusCtrl.removeSupportingOpus(1, 'existing', form);

        expect(scope.opusCtrl.supportingOpuses.length).toBe(2);
        expect(scope.opusCtrl.supportingOpuses[0].opusId).toBe("opus1");
        expect(scope.opusCtrl.supportingOpuses[1].opusId).toBe("opus3");
    });

    it("should remove a supportingOpus from the list of new supportingOpuses when removeSupportingOpus is invoked with 'new'", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.$apply();
        scope.opusCtrl.supportingOpuses = [{opusId: "opus1", title: "Opus 1"}, {
            opusId: "opus2",
            title: "Opus 2"
        }, {opusId: "opus3", title: "Opus 3"}];

        scope.$apply();
        scope.opusCtrl.newSupportingOpuses = [{opusId: "opus1", title: "Opus 1"}, {
            opusId: "opus2",
            title: "Opus 2"
        }, {opusId: "opus3", title: "Opus 3"}];
        scope.opusCtrl.removeSupportingOpus(1, 'new', form);

        expect(scope.opusCtrl.supportingOpuses.length).toBe(3);
        expect(scope.opusCtrl.newSupportingOpuses.length).toBe(2);
        expect(scope.opusCtrl.newSupportingOpuses[0].opusId).toBe("opus1");
        expect(scope.opusCtrl.newSupportingOpuses[1].opusId).toBe("opus3");
    });

    it("should remove an existing approved list from the opus when removeApprovedList is invoked with 'existing'", function () {
        scope.opusCtrl.opus = {approvedLists: ["list1", "list2", "list3"]};

        scope.$apply();
        scope.opusCtrl.removeApprovedList(1, 'existing', form);

        expect(scope.opusCtrl.opus.approvedLists.length).toBe(2);
        expect(scope.opusCtrl.opus.approvedLists[0]).toBe("list1");
        expect(scope.opusCtrl.opus.approvedLists[1]).toBe("list3");
    });

    it("should remove an approved list from the list of new approved lists when removeApprovedList is invoked with 'new'", function () {
        scope.opusCtrl.opus = {approvedLists: ["list1", "list2", "list3"]};

        scope.opusCtrl.newApprovedLists = ["list4", "list5", "list6"];
        scope.opusCtrl.removeApprovedList(1, 'new', form);

        expect(scope.opusCtrl.opus.approvedLists.length).toBe(3);
        expect(scope.opusCtrl.newApprovedLists.length).toBe(2);
        expect(scope.opusCtrl.newApprovedLists[0]).toBe("list4");
        expect(scope.opusCtrl.newApprovedLists[1]).toBe("list6");
    });

    it("should remove an existing bio status list from the opus when removeBioStatusList is invoked with 'existing'", function () {
        scope.opusCtrl.opus = {bioStatusLists: ["list1", "list2", "list3"]};

        scope.$apply();
        scope.opusCtrl.removeBioStatusList(1, 'existing', form);

        expect(scope.opusCtrl.opus.bioStatusLists.length).toBe(2);
        expect(scope.opusCtrl.opus.bioStatusLists[0]).toBe("list1");
        expect(scope.opusCtrl.opus.bioStatusLists[1]).toBe("list3");
    });

    it("should remove an bio status list from the list of new bio status lists when removeBioStatusList is invoked with 'new'", function () {
        scope.opusCtrl.opus = {bioStatusLists: ["list1", "list2", "list3"]};

        scope.opusCtrl.newBioStatusLists = ["list4", "list5", "list6"];
        scope.opusCtrl.removeBioStatusList(1, 'new', form);

        expect(scope.opusCtrl.opus.bioStatusLists.length).toBe(3);
        expect(scope.opusCtrl.newBioStatusLists.length).toBe(2);
        expect(scope.opusCtrl.newBioStatusLists[0]).toBe("list4");
        expect(scope.opusCtrl.newBioStatusLists[1]).toBe("list6");
    });

    it("should set the form to Dirty removeImageSource is invoked", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);

        scope.$apply();
        scope.opusCtrl.newImageSources = ["newSource1", "newSource2", "newSource3"];

        scope.opusCtrl.removeImageSource(1, 'new', form);
        expect(form.$setDirty).toHaveBeenCalled();
    });

    it("should set the form to Dirty removeRecordSource is invoked", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);

        scope.$apply();
        scope.opusCtrl.newRecordSources = ["newSource1", "newSource2", "newSource3"];
        scope.opusCtrl.removeRecordSource(1, 'new', form);

        expect(form.$setDirty).toHaveBeenCalled();
    });

    it("should set the form to Dirty removeSupportingOpus is invoked", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.$apply();

        scope.opusCtrl.opus.supportingOpuses = [{opusId: "opus1", title: "Opus 1"}, {
            opusId: "opus2",
            title: "Opus 2"
        }, {opusId: "opus3", title: "Opus 3"}];
        scope.opusCtrl.newSupportingOpuses = [{opusId: "opus1", title: "Opus 1"}, {
            opusId: "opus2",
            title: "Opus 2"
        }, {opusId: "opus3", title: "Opus 3"}];
        scope.opusCtrl.removeSupportingOpus(1, 'new', form);

        expect(form.$setDirty).toHaveBeenCalled();
    });

    it("should merge newImageSources with the existing image sources when saveImageSources is called", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.opusCtrl.opusId = OPUS_ID;

        scope.$apply();
        scope.opusCtrl.newImageSources = [{dataResource: {id: "newId1"}}];

        scope.opusCtrl.saveImageSources(form);

        var expectedOpus = {
            title: "OpusName",
            dataResourceUid: "dataUid1",
            imageSources: ["source1", "source2", "source3", "newId1"], // new id added here
            recordSources: ["source1", "source2", "source3"],
            mapPointColour: "12345",
            supportingOpuses: [],
            keybaseProjectId: "",
            keybaseKeyId: ""
        };

        expect(profileService.saveOpus).toHaveBeenCalledWith(OPUS_ID, expectedOpus);
    });

    it("should validate that image sources have an associated data resource object", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.opusCtrl.opusId = OPUS_ID;
        scope.$apply();

        scope.opusCtrl.newImageSources = [{dataResource: {}}];

        scope.opusCtrl.saveImageSources(form);

        expect(profileService.saveOpus).not.toHaveBeenCalledWith();
        expect(messageService.alert).toHaveBeenCalledWith("1 image source is not valid. You must select items from the list.");
    });

    it("should save the opus if no new image sources have been added but existing image sources have been removed", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.opusCtrl.opusId = OPUS_ID;
        scope.$apply();

        scope.opusCtrl.removeImageSource(1, 'existing', form);
        scope.opusCtrl.saveImageSources(form);

        var expectedOpus = {
            title: "OpusName",
            dataResourceUid: "dataUid1",
            mapPointColour: "12345",
            imageSources: ["source1", "source3"], // existing source (index 1) removed
            recordSources: ["source1", "source2", "source3"],
            supportingOpuses: [],
            keybaseProjectId: "",
            keybaseKeyId: ""
        };

        expect(profileService.saveOpus).toHaveBeenCalledWith(OPUS_ID, expectedOpus);
    });

    it("should merge newRecordSources with the existing record sources when saveRecordSources is called", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.opusCtrl.opusId = OPUS_ID;

        scope.$apply();
        scope.opusCtrl.newRecordSources = [{dataResource: {id: "newId1"}}];

        scope.opusCtrl.saveRecordSources(form);

        var expectedOpus = {
            title: "OpusName",
            dataResourceUid: "dataUid1",
            imageSources: ["source1", "source2", "source3"],
            recordSources: ["source1", "source2", "source3", "newId1"], // new id added here
            mapPointColour: "12345",
            supportingOpuses: [],
            keybaseProjectId: "",
            keybaseKeyId: ""
        };

        expect(profileService.saveOpus).toHaveBeenCalledWith(OPUS_ID, expectedOpus);
    });

    it("should validate that record sources have an associated data resource object", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.opusCtrl.opusId = OPUS_ID;
        scope.$apply();

        scope.opusCtrl.newRecordSources = [{dataResource: {}}];

        scope.opusCtrl.saveRecordSources(form);

        expect(profileService.saveOpus).not.toHaveBeenCalledWith();
        expect(messageService.alert).toHaveBeenCalledWith("1 record source is not valid. You must select items from the list.");
    });

    it("should save the opus if no new record sources have been added but existing record sources have been removed", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.opusCtrl.opusId = OPUS_ID;
        scope.$apply();

        scope.opusCtrl.removeRecordSource(1, 'existing', form);
        scope.opusCtrl.saveRecordSources(form);

        var expectedOpus = {
            title: "OpusName",
            dataResourceUid: "dataUid1",
            mapPointColour: "12345",
            imageSources: ["source1", "source2", "source3"],
            recordSources: ["source1", "source3"], // existing source (index 1) removed
            supportingOpuses: [],
            keybaseProjectId: "",
            keybaseKeyId: ""
        };

        expect(profileService.saveOpus).toHaveBeenCalledWith(OPUS_ID, expectedOpus);
    });

    it("should merge newSupportingOpuses with the existing supporting opuses when saveSupportingOpus is called", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.opusCtrl.opusId = OPUS_ID;

        scope.$apply();
        scope.opusCtrl.supportingOpuses = [{uuid: "opus1", title: "Opus 1"}];
        scope.opusCtrl.newSupportingOpuses = [{opus: {uuid: "opus2", title: "Opus 2"}}];

        scope.opusCtrl.saveSupportingOpuses(form);

        var expectedData = {
            supportingOpuses: [{uuid: "opus1", title: "Opus 1"}, {uuid: "opus2", title: "Opus 2"}]
        };

        expect(profileService.updateSupportingCollections).toHaveBeenCalledWith(OPUS_ID, expectedData);
    });

    it("should validate that supporting opuses have an associated opus object with and id", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.opusCtrl.opusId = OPUS_ID;
        scope.$apply();

        scope.opusCtrl.newSupportingOpuses = [{opus: {title: "bla"}}];

        scope.opusCtrl.saveSupportingOpuses(form);

        expect(profileService.saveOpus).not.toHaveBeenCalledWith();
        expect(messageService.alert).toHaveBeenCalledWith("1 supporting collection is not valid. You must select items from the list.");
    });

    it("should save the opus if no new supporting opuses have been added but existing ones have been removed", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.opusCtrl.opusId = OPUS_ID;
        scope.$apply();
        scope.opusCtrl.supportingOpuses = [{uuid: "opus1", title: "Opus 1"}, {
            uuid: "opus2",
            title: "Opus 2"
        }, {uuid: "opus3", title: "Opus 3"}];

        scope.opusCtrl.removeSupportingOpus(1, 'existing', form);
        scope.opusCtrl.saveSupportingOpuses(form);

        var expectedData = {
            supportingOpuses: [{uuid: "opus1", title: "Opus 1"}, {uuid: "opus3", title: "Opus 3"}]
        };

        expect(profileService.updateSupportingCollections).toHaveBeenCalledWith(OPUS_ID, expectedData);
    });

    it("should include the data sharing flags in the call to updateSupportingCollections if they have been defined", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        scope.opusCtrl.opus.autoApproveShareRequests = true;
        scope.opusCtrl.opus.allowCopyFromLinkedOpus = true;
        scope.opusCtrl.opus.showLinkedOpusAttributes = true;
        scope.opusCtrl.opusId = OPUS_ID;
        scope.$apply();
        scope.opusCtrl.supportingOpuses = [{uuid: "opus1", title: "Opus 1"}, {
            uuid: "opus2",
            title: "Opus 2"
        }, {uuid: "opus3", title: "Opus 3"}];

        scope.opusCtrl.removeSupportingOpus(1, 'existing', form);
        scope.opusCtrl.saveSupportingOpuses(form);

        var expectedData = {
            supportingOpuses: [{uuid: "opus1", title: "Opus 1"}, {uuid: "opus3", title: "Opus 3"}],
            autoApproveShareRequests: true,
            allowCopyFromLinkedOpus: true,
            showLinkedOpusAttributes: true
        };

        expect(profileService.updateSupportingCollections).toHaveBeenCalledWith(OPUS_ID, expectedData);
    });

    it("should merge newApprovedLists with the existing approvedLists when saveApprovedLists is called", function () {
        scope.opusCtrl.opus = {title: "OpusName", approvedLists: ["list1", "list2", "list3"]};
        scope.opusCtrl.newApprovedLists = [{list: {dataResourceUid: "list4"}}, {list: {dataResourceUid: "list5"}}, {list: {dataResourceUid: "list6"}}];
        scope.opusCtrl.opusId = OPUS_ID;

        scope.opusCtrl.saveApprovedLists(form);

        var expectedOpus = {
            title: "OpusName",
            approvedLists: ["list1", "list2", "list3", "list4", "list5", "list6"],
            keybaseProjectId: "",
            keybaseKeyId: ""
        };

        expect(profileService.saveOpus).toHaveBeenCalledWith(OPUS_ID, expectedOpus);
    });

    it("should validate that approved lists have an associated dataResourceUid", function () {
        scope.opusCtrl.opusId = OPUS_ID;

        scope.opusCtrl.newApprovedLists = [{list: {listName: "list1"}}];

        scope.opusCtrl.saveApprovedLists(form);

        expect(profileService.saveOpus).not.toHaveBeenCalledWith();
        expect(messageService.alert).toHaveBeenCalledWith("1 list is not valid. You must select items from the list.");
    });

    it("should save the opus if no new approved lists have been added but existing ones have been removed", function () {
        scope.opusCtrl.opus = {title: "OpusName", approvedLists: ["list1", "list2", "list3"]};
        scope.opusCtrl.opusId = OPUS_ID;

        scope.opusCtrl.removeApprovedList(1, 'existing', form);
        scope.opusCtrl.saveApprovedLists(form);

        var expectedOpus = {
            title: "OpusName",
            approvedLists: ["list1", "list3"],
            keybaseProjectId: "",
            keybaseKeyId: ""
        };

        expect(profileService.saveOpus).toHaveBeenCalledWith(OPUS_ID, expectedOpus);
    });

    //
    it("should merge newBioStatusLists with the existing bioStatusLists when saveBioStatusLists is called", function () {
        scope.opusCtrl.opus = {title: "OpusName", bioStatusLists: []};
        scope.opusCtrl.newBioStatusLists = [{list: {dataResourceUid: "list4"}}];
        scope.opusCtrl.opusId = OPUS_ID;

        scope.opusCtrl.saveBioStatusLists(form);

        var expectedOpus = {
            title: "OpusName",
            bioStatusLists: ["list4"],
            keybaseProjectId: "",
            keybaseKeyId: ""
        };

        expect(profileService.saveOpus).toHaveBeenCalledWith(OPUS_ID, expectedOpus);
    });

    it("should validate that bio status lists have an associated dataResourceUid", function () {
        scope.opusCtrl.opusId = OPUS_ID;

        scope.opusCtrl.newBioStatusLists = [{list: {listName: "list1"}}];

        scope.opusCtrl.saveBioStatusLists(form);

        expect(profileService.saveOpus).not.toHaveBeenCalledWith();
        expect(messageService.alert).toHaveBeenCalledWith("1 list is not valid. You must select items from the list.");
    });

    it("should save the opus if no new bio status lists have been added but existing ones have been removed", function () {
        scope.opusCtrl.opus = {title: "OpusName", bioStatusLists: ["list1"]};
        scope.opusCtrl.opusId = OPUS_ID;

        scope.opusCtrl.removeBioStatusList(0, 'existing', form);
        scope.opusCtrl.saveBioStatusLists(form);

        var expectedOpus = {
            title: "OpusName",
            bioStatusLists: [],
            keybaseProjectId: "",
            keybaseKeyId: ""
        };

        expect(profileService.saveOpus).toHaveBeenCalledWith(OPUS_ID, expectedOpus);
    });

    it("should raise an alert message if the call to saveOpus fails", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        saveOpusDefer.reject();
        scope.$apply();
        scope.opusCtrl.saveOpus(form);
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("Failed to update OpusName.");
    });

    it("should raise a success message and set the form to pristine if the call to saveOpus succeeds", function () {
        scope.opusCtrl.opus = JSON.parse(getOpusResponse);
        saveOpusDefer.resolve({});
        scope.$apply();
        scope.opusCtrl.saveOpus(form);
        scope.$apply();

        expect(messageService.success).toHaveBeenCalledWith("Successfully updated OpusName.");
        expect(form.$setPristine).toHaveBeenCalled();
    });

    it("should check for duplicate short names when saving the opus", function () {
        scope.opusCtrl.initialShortName = "oldShortName";
        scope.opusCtrl.opus = {shortName: "newShortName"};

        scope.opusCtrl.saveOpus(form);

        expect(profileService.getOpus).toHaveBeenCalledWith("newShortName");
    });

    it("should only check for duplicate short names when saving the opus if the short name has changed", function () {
        scope.opusCtrl.initialShortName = "oldShortName";
        scope.opusCtrl.opus = {shortName: "oldShortName"};

        scope.opusCtrl.saveOpus(form);

        expect(profileService.getOpus).not.toHaveBeenCalledWith();
    });

    it("should raise an alert message if there is another opus with the same shortName when saving the opus", function () {
        scope.opusCtrl.initialShortName = "oldShortName";
        scope.opusCtrl.opus = {shortName: "newShortName"};

        opusDefer.resolve({shortName: "newShortName"});
        scope.opusCtrl.saveOpus(form);
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("The specified short name is already in use. Short Names must be unique across all collections.");
    });

    it("should save the opus if there are no other opuses with the same shortName when saving the opus", function () {
        scope.opusCtrl.initialShortName = "oldShortName";
        scope.opusCtrl.opus = {shortName: "newShortName"};

        opusDefer.reject({status: 404});
        scope.opusCtrl.saveOpus(form);
        scope.$apply();

        expect(profileService.saveOpus).toHaveBeenCalled();
    });

    it("should redirect to the update opus page after creating a new opus", function () {
        scope.opusCtrl.opus = {};

        var newUuid = "123456";
        saveOpusDefer.resolve({uuid: newUuid});

        scope.opusCtrl.saveOpus(form);
        scope.$apply();

        expect(mockUtil.redirect).toHaveBeenCalledWith("/contextRoot/opus/" + newUuid + "/update");
    });

    it("should stay on the same page after updating an exiting new opus", function () {
        scope.opusCtrl.opus = {uuid: "123456"};

        saveOpusDefer.resolve({uuid: "123456"});

        scope.opusCtrl.saveOpus(form);
        scope.$apply();

        expect(mockUtil.redirect).not.toHaveBeenCalled();
    })
});
