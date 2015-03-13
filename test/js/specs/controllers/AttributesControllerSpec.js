describe("AttributesController tests", function () {
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
    var profileDefer, vocabDefer, saveAttrDefer, deleteAttrDefer, showAuditDefer;
    var window;

    var getProfileResponse = '{"profile": {"guid": "guid1", "scientificName":"profileName", "attributes":["attr1", "attr2"]}, "opus": {"imageSources": ["source1", "source2"]}}';
    var vocabResponse = '{"terms": ["term1", "term2"]}';
    var saveAttributeResponse = '{"attributeId": "newId"}';
    var deleteAttributeResponse = '{}';
    var showAuditResponse = '[{"userId": "1", "object": {"text":"auditText1", "title":"auditTitle1"}}, {"userId": "2", "object": {"text":"auditText2", "title":"auditTitle2"}}]';

    beforeAll(function () {
        console.log("****** Attributes Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_, _$window_, _$filter_) {
        scope = $rootScope.$new();
        profileService = _profileService_;
        window = _$window_;

        profileDefer = $q.defer();
        vocabDefer = $q.defer();
        deleteAttrDefer = $q.defer();
        saveAttrDefer = $q.defer();
        showAuditDefer = $q.defer();

        spyOn(profileService, "getProfile").and.returnValue(profileDefer.promise);
        spyOn(profileService, "getOpusVocabulary").and.returnValue(vocabDefer.promise);
        spyOn(profileService, "saveAttribute").and.returnValue(saveAttrDefer.promise);
        spyOn(profileService, "deleteAttribute").and.returnValue(deleteAttrDefer.promise);
        spyOn(profileService, "getAuditForAttribute").and.returnValue(showAuditDefer.promise);

        spyOn(window, "confirm").and.returnValue(true);

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("AttributeEditor as attrCtrl", {
            $scope: scope,
            profileService: profileService,
            util: mockUtil,
            messageService: messageService,
            $filter: _$filter_
        });
    }));

    it("should set the profile attribute of the current scope.attrCtrl when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        vocabDefer.resolve(JSON.parse(vocabResponse));

        scope.attrCtrl.init("false");
        scope.$apply();

        expect(scope.attrCtrl.profile).toBeDefined();
    });

    it("should set the opus attribute of the current scope.attrCtrl when init is called", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        vocabDefer.resolve(JSON.parse(vocabResponse));

        scope.attrCtrl.init("false");
        scope.$apply();

        expect(scope.attrCtrl.opus).toBeDefined();
    });

    it("should set the readonly flag to false when init is called with edit=false", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        vocabDefer.resolve(JSON.parse(vocabResponse));

        scope.attrCtrl.init("false");
        scope.$apply();

        expect(scope.attrCtrl.readonly).toBe(true);
    });

    it("should set the readonly flag to false when init is called with edit=true", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        vocabDefer.resolve(JSON.parse(vocabResponse));

        scope.attrCtrl.init("true");
        scope.$apply();

        expect(scope.attrCtrl.readonly).toBe(false);
    });

    it("should set the attributes array on the scope.attrCtrl with the results from the getProfile call", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        vocabDefer.resolve(JSON.parse(vocabResponse));

        scope.attrCtrl.init("false");
        scope.$apply();

        expect(scope.attrCtrl.attributes.length).toBe(2);
    });

    it("should set the attributeTitles array on the scope.attrCtrl with the results from the getOpusVocabulary call if the opus has attributeVocabUuid", function () {
        var getProfileResponse = '{"profile": {"guid": "guid1", "scientificName":"profileName", "attributes":["attr1", "attr2"]}, ' +
            '"opus": {"imageSources": ["source1", "source2"], "attributeVocabUuid": "1234"}}';
        profileDefer.resolve(JSON.parse(getProfileResponse));
        vocabDefer.resolve(JSON.parse(vocabResponse));

        scope.attrCtrl.init("false");
        scope.$apply();

        expect(scope.attrCtrl.attributeTitles.length).toBe(2);
    });

    it("should leave the attributeTitles array empty if the opus has attributeVocabUuid", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        vocabDefer.resolve(JSON.parse(vocabResponse));

        scope.attrCtrl.init("false");
        scope.$apply();

        expect(scope.attrCtrl.attributeTitles.length).toBe(0);
    });

    it("should raise an alert message when the call to getProfile fails", function () {
        profileDefer.reject();
        vocabDefer.resolve(JSON.parse(vocabResponse));

        scope.attrCtrl.init("false");
        scope.$apply();

        expect(scope.attrCtrl.profile).not.toBeDefined();
        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the profile.");
    });


    it("should add a 'loading profile data' info message when retrieving the profile, and remove it when done", function () {
        profileDefer.resolve(JSON.parse(getProfileResponse));
        vocabDefer.resolve(JSON.parse(vocabResponse));

        scope.attrCtrl.init("false");
        scope.$apply();

        expect(messageService.info).toHaveBeenCalledWith("Loading profile data...");
        expect(messageService.info.calls.count()).toBe(1);
        expect(messageService.pop).toHaveBeenCalledWith();
        expect(messageService.pop.calls.count()).toBe(1);
    });

    it("should create a new empty attribute object at the start of the attributes list when addAttribute is invoked", function () {
        expect(scope.attrCtrl.attributes.length).toBe(0);
        scope.attrCtrl.addAttribute();

        expect(scope.attrCtrl.attributes.length).toBe(1);
        expect(scope.attrCtrl.attributes[0].title).toBe("");
        expect(scope.attrCtrl.attributes[0].uuid).toBe("");
        expect(scope.attrCtrl.attributes[0].contributor.length).toBe(0);
    });

    it("should invoke the saveAttribute method of the profile service when saveAttribute is invoked", function () {
        scope.attrCtrl.profile = {"uuid": "profileId1"};
        scope.attrCtrl.attributes = [{"saving": "false", "uuid": "uuid1", "title": "attrTitle", "text": "attrText"}];
        var form = {
            $setPristine: function () {
            }
        };

        saveAttrDefer.resolve(JSON.parse(saveAttributeResponse));

        scope.attrCtrl.saveAttribute(0, form);
        scope.$apply();

        expect(profileService.saveAttribute).toHaveBeenCalledWith("profileId1", "uuid1", {
            "profileId": "profileId1",
            "attributeId": "uuid1",
            "title": "AttrTitle", // titles are capitalized on save
            "text": "attrText"
        });
    });

    it("should set the form to pristine after the attribute is successfully saved", function () {
        scope.attrCtrl.profile = {"uuid": "profileId1"};
        scope.attrCtrl.attributes = [{"saving": "false", "uuid": "uuid1", "title": "attrTitle", "text": "attrText"}];
        var form = {
            $setPristine: function () {
            }
        };
        spyOn(form, "$setPristine");

        saveAttrDefer.resolve(JSON.parse(saveAttributeResponse));

        scope.attrCtrl.saveAttribute(0, form);
        scope.$apply();

        expect(form.$setPristine).toHaveBeenCalled();
    });

    it("should raise an alert message if the attribute could not be saved", function () {
        scope.attrCtrl.profile = {"uuid": "profileId1"};
        scope.attrCtrl.attributes = [{"saving": "false", "uuid": "uuid1", "title": "attrTitle", "text": "attrText"}];
        var form = {
            $setPristine: function () {
            }
        };
        spyOn(form, "$setPristine");

        saveAttrDefer.reject();

        scope.attrCtrl.saveAttribute(0, form);
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalled();
    });

    it("should display a success message when the attribute is saved", function () {
        scope.attrCtrl.profile = {"uuid": "profileId1"};
        scope.attrCtrl.attributes = [{"saving": "false", "uuid": "uuid1", "title": "attrTitle", "text": "attrText"}];
        var form = {
            $setPristine: function () {
            }
        };
        spyOn(form, "$setPristine");

        saveAttrDefer.resolve(JSON.parse(saveAttributeResponse));

        scope.attrCtrl.saveAttribute(0, form);
        scope.$apply();

        expect(messageService.success).toHaveBeenCalled();
    });

    it("should update the uuid of the attribute when it is saved", function () {
        scope.attrCtrl.profile = {"uuid": "profileId1"};
        scope.attrCtrl.attributes = [{"saving": "false", "uuid": "uuid1", "title": "attrTitle", "text": "attrText"}];
        var form = {
            $setPristine: function () {
            }
        };
        spyOn(form, "$setPristine");

        saveAttrDefer.resolve(JSON.parse(saveAttributeResponse));

        scope.attrCtrl.saveAttribute(0, form);
        scope.$apply();

        expect(scope.attrCtrl.attributes[0].uuid).toBe("newId");
    });

    it("should display a confirmation dialog when deleteAttribute is invoked", function () {
        scope.attrCtrl.profile = {"uuid": "profileId1"};
        scope.attrCtrl.attributes = [{"saving": "false", "uuid": "uuid1", "title": "attrTitle", "text": "attrText"}];

        deleteAttrDefer.resolve(JSON.parse(deleteAttributeResponse));
        scope.attrCtrl.deleteAttribute(0);
        scope.$apply();

        expect(window.confirm).toHaveBeenCalled();
        expect(scope.attrCtrl.attributes.length).toBe(0);
    });

    it("should not delete the attribute if the confirmation is cancelled", function () {
        scope.attrCtrl.profile = {"uuid": "profileId1"};
        scope.attrCtrl.attributes = [{"saving": "false", "uuid": "uuid1", "title": "attrTitle", "text": "attrText"}];
        window.confirm.and.returnValue(false);

        deleteAttrDefer.resolve(JSON.parse(deleteAttributeResponse));
        scope.attrCtrl.deleteAttribute(0);
        scope.$apply();

        expect(window.confirm).toHaveBeenCalled();
        expect(scope.attrCtrl.attributes.length).toBe(1);
    });

    it("should not invoke profileService if the attribute has not been saved (i.e. Uuid is null)", function () {
        scope.attrCtrl.profile = {"uuid": "profileId1"};
        scope.attrCtrl.attributes = [{"saving": "false", "uuid": "", "title": "attrTitle", "text": "attrText"}];

        deleteAttrDefer.resolve(JSON.parse(deleteAttributeResponse));
        scope.attrCtrl.deleteAttribute(0);
        scope.$apply();

        expect(profileService.deleteAttribute).not.toHaveBeenCalled();
        expect(scope.attrCtrl.attributes.length).toBe(0);
    });

    it("should invoke profileService if the attribute has been saved", function () {
        scope.attrCtrl.profile = {"uuid": "profileId1"};
        scope.attrCtrl.attributes = [{"saving": "false", "uuid": "uuid1", "title": "attrTitle", "text": "attrText"}];

        deleteAttrDefer.resolve(JSON.parse(deleteAttributeResponse));
        scope.attrCtrl.deleteAttribute(0);
        scope.$apply();

        expect(profileService.deleteAttribute).toHaveBeenCalledWith("uuid1", "profileId1");
        expect(scope.attrCtrl.attributes.length).toBe(0);
    });

    it("should raise an alert message if the delete fails", function () {
        scope.attrCtrl.profile = {"uuid": "profileId1"};
        scope.attrCtrl.attributes = [{"saving": "false", "uuid": "uuid1", "title": "attrTitle", "text": "attrText"}];

        deleteAttrDefer.reject();
        scope.attrCtrl.deleteAttribute(0);
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalled();
        expect(scope.attrCtrl.attributes.length).toBe(1);
    });

    it("should set the title and text to the selected audit item when revertAttribute is invoked", function () {
        scope.attrCtrl.attributes = [{title: "originalTitle1", text: "originalText1"},
            {
                title: "originalTitle2",
                text: "originalText2",
                audit: [{object: {title: "auditTitle1", text: "auditText1"}},
                    {object: {title: "auditTitle2", text: "auditText2"}}]
            }];

        var form = {
            $setDirty: function () {
            }
        };
        spyOn(form, "$setDirty");

        scope.attrCtrl.revertAttribute(1, 1, form);

        expect(scope.attrCtrl.attributes[1].text).toBe("auditText2");
        expect(scope.attrCtrl.attributes[1].title).toBe("auditTitle2");
        expect(form.$setDirty).toHaveBeenCalled();
    });

    it("should populate the audit and auditShowing properties of the selected attribute when showAudit is invoked", function () {
        scope.attrCtrl.attributes = [{title: "originalTitle1", text: "originalText1"},
            {title: "originalTitle2", text: "originalText2"}];

        showAuditDefer.resolve(JSON.parse(showAuditResponse));
        scope.attrCtrl.showAudit(1);
        scope.$apply();

        expect(scope.attrCtrl.attributes[0].audit).not.toBeDefined();
        expect(scope.attrCtrl.attributes[1].audit).toEqual([{
            userId: "1",
            object: {text: "auditText1", title: "auditTitle1"}
        }, {userId: "2", object: {text: "auditText2", title: "auditTitle2"}}]);
    });

    it("should raise an alert message when the audit history cannot be retrieved", function () {
        scope.attrCtrl.attributes = [{title: "originalTitle1", text: "originalText1"},
            {title: "originalTitle2", text: "originalText2"}];

        showAuditDefer.reject();
        scope.attrCtrl.showAudit(1);
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the audit history.");
    });

    it("should set the auditShowing property of the selected attribute to false when hideAudit is invoked", function() {
        scope.attrCtrl.attributes = [{title: "originalTitle1", text: "originalText1"},
            {title: "originalTitle2", text: "originalText2"}];

        scope.attrCtrl.hideAudit(1);

        expect(scope.attrCtrl.attributes[1].auditShowing).toBe(false);
    });

    it("should return true if the vocabulary is NOT strict when isValid is invoked with a value not in the vocab", function() {
        scope.attrCtrl.vocabularyStrict = false;
        scope.attrCtrl.attributeTitles = ["term1", "term2", "term3"];

        var valid = scope.attrCtrl.isValid("something else");

        expect(valid).toBe(true);
    });

    it("should return false if the vocabulary IS strict when isValid is invoked with a value not in the vocab", function() {
        scope.attrCtrl.vocabularyStrict = true;
        scope.attrCtrl.attributeTitles = ["term1", "term2", "term3"];

        var valid = scope.attrCtrl.isValid("something else");

        expect(valid).toBe(false);
    });

    it("should return true if the vocabulary IS strict when isValid is invoked with a value in the vocab", function() {
        scope.attrCtrl.vocabularyStrict = true;
        scope.attrCtrl.attributeTitles = ["term1", "term2", "term3"];

        var valid = scope.attrCtrl.isValid("term2");

        expect(valid).toBe(false);
    });
});