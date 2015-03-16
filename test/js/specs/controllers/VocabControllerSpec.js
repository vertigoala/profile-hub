describe("VocabController tests", function () {
    var controller;
    var scope;
    var modal;
    var mockUtil = {
        getPathItem: function () {
            return "12345";
        },
        LAST: "last"
    };
    var mockPopup = {
        result: {
            then: function() {

            }
        }
    };

    var form;
    var messageService;
    var profileService;
    var opusDefer, findUsagesDefer, replaceUsagesDefer, updateVocabDefer, getVocabDefer;

    var getOpusResponse = '{"title": "OpusName", "dataResourceUid":"dataUid1", "imageSources": ["source1", "source2", "source3"], "recordSources": ["source1", "source2", "source3"], "mapPointColour": "12345"}';
    var getVocabResponse = '{"name":"vocab name", "strict":"true","terms":[{"name":"term1", "termId":"term1"},{"name":"term2", "termId":"term2"},{"name":"term3", "termId":"term3"}]}';

    beforeAll(function () {
        console.log("****** Vocab Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_, _$modal_) {
        scope = $rootScope.$new();
        profileService = _profileService_;
        modal = _$modal_;

        opusDefer = $q.defer();
        replaceUsagesDefer = $q.defer();
        findUsagesDefer = $q.defer();
        updateVocabDefer = $q.defer();
        getVocabDefer = $q.defer();

        spyOn(profileService, "getOpus").and.returnValue(opusDefer.promise);
        spyOn(profileService, "getOpusVocabulary").and.returnValue(getVocabDefer.promise);
        spyOn(profileService, "updateVocabulary").and.returnValue(updateVocabDefer.promise);
        spyOn(profileService, "findUsagesOfVocabTerm").and.returnValue(findUsagesDefer.promise);
        spyOn(profileService, "replaceUsagesOfVocabTerm").and.returnValue(replaceUsagesDefer.promise);

        spyOn(modal, "open").and.returnValue(mockPopup);

        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        controller = $controller("VocabController as vocabCtrl", {
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
        expect(scope.vocabCtrl.opus).toBeDefined();
    });

    it("should raise an alert message when the call to getOpus fails", function () {
        opusDefer.reject();

        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while retrieving the opus.");
    });

    it("should set the vocabulary on the scope when the controller loads", function () {
        opusDefer.resolve(JSON.parse(getOpusResponse));
        getVocabDefer.resolve(JSON.parse(getVocabResponse));

        scope.$apply();

        expect(profileService.getOpusVocabulary).toHaveBeenCalled();
        expect(scope.vocabCtrl.vocabulary).toBeDefined()
    });

    it("should raise an alert message when the call to getOpusVocabulary fails", function () {
        getVocabDefer.reject();

        scope.vocabCtrl.loadVocabulary("12345", form);
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while loading the vocabulary.");
    });

    it("should create a new term record on the vocabulary when addVocabTerm is invoked", function () {
        scope.vocabCtrl.opus = {attributeVocabUuid: "vocabId"};
        scope.vocabCtrl.vocabulary = {terms: []};
        scope.vocabCtrl.newVocabTerm = "NEW";
        scope.vocabCtrl.addVocabTerm(form);

        expect(scope.vocabCtrl.vocabulary.terms.length).toBe(1);
        expect(scope.vocabCtrl.vocabulary.terms[0].termId).toBe("");
        expect(scope.vocabCtrl.vocabulary.terms[0].name).toBe("NEW");
    });

    it("should capitalise the first word of the new term record on the vocabulary when addVocabTerm is invoked", function () {
        scope.vocabCtrl.opus = {attributeVocabUuid: "vocabId"};
        scope.vocabCtrl.vocabulary = {terms: []};
        scope.vocabCtrl.newVocabTerm = "should be capitalised";
        scope.vocabCtrl.addVocabTerm(form);

        expect(scope.vocabCtrl.vocabulary.terms[0].name).toBe("Should be capitalised");
    });

    it("should set the form to dirty when addVocabTerm is invoked", function () {
        scope.vocabCtrl.opus = {attributeVocabUuid: "vocabId"};
        scope.vocabCtrl.vocabulary = {terms: []};
        scope.vocabCtrl.newVocabTerm = "should be capitalised";
        scope.vocabCtrl.addVocabTerm(form);

        expect(form.$setDirty).toHaveBeenCalled();
    });

    it("should return true if the provided term is in the list of terms to be replaced when termisInReplacementList is invoked", function() {
        scope.vocabCtrl.replacements = [{vocabId: "vocab1", existingTermName: "old term", newTermName: "new term"}];
        var match = scope.vocabCtrl.termIsInReplacementList({vocabId: "vocab1", name: "old term"});

        expect(match).toBe(true);
    });

    it("should return false if the provided term is not in the list of terms to be replaced when termisInReplacementList is invoked", function() {
        scope.vocabCtrl.replacements = [{vocabId: "vocab1", existingTermName: "old term", newTermName: "new term"}];
        var match = scope.vocabCtrl.termIsInReplacementList({vocabId: "vocab1", name: "something else"});

        expect(match).toBe(false);
    });

    it("should return false if the list of terms to be replaced is empty when termisInReplacementList is invoked", function() {
        scope.vocabCtrl.replacements = [];
        var match = scope.vocabCtrl.termIsInReplacementList({vocabId: "vocab1", name: "old term"});

        expect(match).toBe(false);
    });

    it("should remove the term from the vocabulary's list of terms if there are 0 usages, and set the form to dirty", function() {
        findUsagesDefer.resolve(JSON.parse('{"usageCount": 0}'));

        scope.vocabCtrl.opus = {attributeVocabUuid: "vocabId"};
        scope.vocabCtrl.vocabulary = {terms: [{name:"term1", termId:"term1"},{name:"term2", termId:"term2"},{name:"term3", termId:"term3"}]};

        scope.vocabCtrl.removeVocabTerm(1, form);
        scope.$apply();

        expect(profileService.findUsagesOfVocabTerm).toHaveBeenCalledWith("vocabId", "term2");
        expect(scope.vocabCtrl.vocabulary.terms.length).toBe(2);
        expect(scope.vocabCtrl.vocabulary.terms[0].name).toBe("term1");
        expect(scope.vocabCtrl.vocabulary.terms[1].name).toBe("term3");
        expect(form.$setDirty).toHaveBeenCalled();
    });

    it("should display the modal remove term popup if there are more than 0 usages when removeVocabTerm is invoked", function() {
        findUsagesDefer.resolve(JSON.parse('{"usageCount": 2}'));

        scope.vocabCtrl.opus = {attributeVocabUuid: "vocabId"};
        scope.vocabCtrl.vocabulary = {terms: [{name:"term1", termId:"term1"},{name:"term2", termId:"term2"},{name:"term3", termId:"term3"}]};

        scope.vocabCtrl.removeVocabTerm(1, form);
        scope.$apply();

        expect(modal.open).toHaveBeenCalledWith(jasmine.objectContaining({templateUrl: "removeTermPopup.html"}));
        expect(modal.open).toHaveBeenCalledWith(jasmine.objectContaining({controller: "RemoveTermController"}));
        expect(modal.open).toHaveBeenCalledWith(jasmine.objectContaining({controllerAs: "removeTermCtrl"}));
    });

    it("should display the modal edit term popup when editVocabTerm is invoked", function() {
        scope.vocabCtrl.editVocabTerm(1, form);

        expect(modal.open).toHaveBeenCalledWith(jasmine.objectContaining({templateUrl: "editTermPopup.html"}));
        expect(modal.open).toHaveBeenCalledWith(jasmine.objectContaining({controller: "VocabModalController"}));
        expect(modal.open).toHaveBeenCalledWith(jasmine.objectContaining({controllerAs: "vocabModalCtrl"}));
    });

    it("should invoke the updateVocabulary service method, then reload the vocabulary when saveVocabulary is invoked", function() {
        updateVocabDefer.resolve({});

        var vocab = {terms: [{name:"term1", termId:"term1"},{name:"term2", termId:"term2"},{name:"term3", termId:"term3"}]};

        scope.vocabCtrl.opus = {attributeVocabUuid: "vocabId"};
        scope.vocabCtrl.vocabulary = vocab;

        scope.vocabCtrl.saveVocabulary(form);
        scope.$apply();

        expect(profileService.updateVocabulary).toHaveBeenCalledWith("vocabId", vocab);
        expect(profileService.getOpusVocabulary).toHaveBeenCalledWith("vocabId");
    });

    it("should raise an alert message if the call to updateVocabulary fails", function() {
        updateVocabDefer.reject();

        scope.vocabCtrl.opus = {attributeVocabUuid: "vocabId"};
        scope.vocabCtrl.vocabulary = {terms: [{name:"term1", termId:"term1"},{name:"term2", termId:"term2"},{name:"term3", termId:"term3"}]};

        scope.vocabCtrl.saveVocabulary(form);
        scope.$apply();

        expect(messageService.alert).toHaveBeenCalledWith("An error occurred while updating the vocabulary.");
    });

    it("should invoke the replaceUsagesOfVocabTerm service method if there are replacements when saveVocabulary is invoked",function() {
        updateVocabDefer.resolve({});
        replaceUsagesDefer.resolve(JSON.parse('{"usages":{"Test":1}}'));

        var vocab = {terms: [{name:"term1", termId:"term1"},{name:"term2", termId:"term2"},{name:"term3", termId:"term3"}]};
        var replacements = [{vocabId: "vocab1", existingTermName: "old term", newTermName: "new term"}];

        scope.vocabCtrl.opus = {attributeVocabUuid: "vocabId"};
        scope.vocabCtrl.replacements = replacements;
        scope.vocabCtrl.vocabulary = vocab;

        scope.vocabCtrl.saveVocabulary(form);
        scope.$apply();

        expect(profileService.replaceUsagesOfVocabTerm).toHaveBeenCalledWith(replacements);
    });
});
