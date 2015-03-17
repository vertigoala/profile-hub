/**
 * The primary purpose of these tests is to ensure that the correct urls are being constructed based on the provided parameters.
 */
describe("ProfileService tests", function () {
    var service;
    var http;
    var mockUtil = {
        contextRoot: function () {
            return "/someContext"
        },
        toStandardPromise: function() {
            return {then: function() {}};
        },
        isUuid: function() {
            return true;
        }
    };

    beforeAll(function () {
        console.log("****** Profile Service Tests ******")
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(function () {
        module("profileEditor", function ($provide) {
            $provide.value("util", mockUtil);
        });
    });

    beforeEach(inject(function (_profileService_, _$httpBackend_) {
        service = _profileService_;
        http = _$httpBackend_;
    }));

    afterEach(function () {
        http.flush();
        http.verifyNoOutstandingExpectation();
        http.verifyNoOutstandingRequest();
    });

    it("should invoke the get profile as json service on the context root when getProfile is called", function () {
        service.getProfile("profileId1");

        http.expectGET("/someContext/profile/json/profileId1").respond("bla");
    });

    it("should invoke the get opus as json service on the context root when getOpus is called", function () {
        service.getOpus("opusId");

        http.expectGET("/someContext/opus/json/opusId").respond("bla");
    });

    it("should invoke the update opus as json service on the context root when saveOpus is called", function () {
        var data = {opusId: "opusId", imageSources: ["one", "two"]};
        service.saveOpus("opusId", data);

        http.expectPOST("/someContext/opus/opusId", data).respond("bla");
    });

    it("should invoke the get opus vocab service on the context root when getOpusVocabulary is called", function () {
        service.getOpusVocabulary("vocabId1");

        http.expectGET("/someContext/vocab/vocabId1").respond("bla")
    });


    it("should invoke the update vocab service on the context root when updateVocabulary is called", function () {
        var data = {name: "vocab1", "strict": "true", "terms": []};
        service.updateVocabulary("vocabId1", data);

        http.expectPOST("/someContext/vocab/vocabId1", data).respond("bla")
    });

    it("should invoke the find term usages service on the context root when findUsagesOfVocabTerm is called", function () {
        service.findUsagesOfVocabTerm("vocabId1", "termName");

        http.expectGET("/someContext/vocab/usages/find?vocabId=vocabId1&termName=termName").respond("bla")
    });

    it("should invoke the get opus vocab service on the context root when getOpusVocabulary is called", function () {
        var replacements = [{vocabId: "vocab1", existingTermName: "old name", newTermName: "new name"}];
        service.replaceUsagesOfVocabTerm(replacements);

        http.expectPOST("/someContext/vocab/usages/replace", replacements).respond("bla")
    });

    it("should invoke the get object audit service on the context root when getAuditForAttribute is called", function () {
        service.getAuditForAttribute("attrId1");

        http.expectGET("/someContext/audit/object/attrId1").respond("bla");
    });

    it("should invoke the delete attribute service on the context root when deleteAttribute is called", function () {
        service.deleteAttribute("attrId1", "profileId1");

        http.expectDELETE("/someContext/profile/deleteAttribute/attrId1?profileId=profileId1").respond("bla");
    });

    xit("should clear the http cache after calling delete attribute", function () {
        service.deleteAttribute("attrId1", "profileId1");

        http.expectDELETE("/someContext/profile/deleteAttribute/attrId1?profileId=profileId1").respond("bla");
        scope.$apply();
        expect(httpCache.removeAll).toHaveBeenCalled();
    });

    it("should invoke the update attribute service on the context root when saveAttribute is called", function () {
        var data = "{attribute data}";
        service.saveAttribute("profileId1", "attrId1", data);

        http.expectPOST("/someContext/profile/updateAttribute/profileId1", data).respond("bla");
    });

    it("should invoke the retrieve images service on the context root when retrieveImages is called", function () {
        service.retrieveImages("searchId1", "sourceList");

        http.expectGET("/someContext/profile/images?searchIdentifier=searchId1&imageSources=sourceList").respond("bla");
    });

    it("should invoke the retrieve lists service on the context root when retrieveLists is called", function () {
        service.retrieveLists("guid1");

        http.expectGET("/someContext/profile/lists?guid=guid1").respond("bla");
    });

    it("should invoke the retrieve classifications service on the context root when getClassifications is called", function () {
        service.getClassifications("guid1", "opus1");

        http.expectGET("/someContext/profile/classifications?guid=guid1&opusId=opus1").respond("bla");
    });

    it("should invoke the retrieve species profile service on the context root when getSpeciesProfile is called", function () {
        service.getSpeciesProfile("guid1");

        http.expectGET("/someContext/profile/speciesProfile?guid=guid1").respond("bla");
    });

    it("should invoke the update links service on the context root when updateLinks is called", function() {
        var data = "list of links";
        service.updateLinks("profileId", data);

        http.expectPOST("/someContext/profile/updateLinks/profileId", data).respond("bla");
    });

    it("should invoke the update BHL links service on the context root when updateBhlLinks is called", function() {
        var data = "list of links";
        service.updateBhlLinks("profileId", data);

        http.expectPOST("/someContext/profile/updateBHLLinks/profileId", data).respond("bla");
    });

    it("should invoke the BHL lookup service on the context root when lookupBhlPage is called", function() {
        service.lookupBhlPage("pageId");

        http.expectGET("/someContext/bhl/pageId").respond("bla");
    });

    it("should invoke the collectory data resource service on the context root when getResource ", function() {
        service.getResource("resId");

        http.expectGET("/someContext/dataResource/resId").respond("bla");
    });

    it("should invoke the list collectory data resource service on the context root when listResources", function() {
        service.listResources();

        http.expectGET("/someContext/dataResource/list").respond("bla");
    });

    it("should invoke the profile search operation on the context root when profileSearch is called ", function() {
        service.profileSearch("opusId", "scientificName");

        http.expectGET("/someContext/profile/search?opusId=opusId&scientificName=scientificName").respond("bla");
    });

    it("should invoke the user search operation on the context root when userSearch is called ", function() {
        service.userSearch("fred");

        http.expectPOST("/someContext/opus/findUser", {userName: "fred"}).respond("bla");
    });

    it("should invoke the update user operation on the context root when updateUser is called ", function() {
        service.updateUsers("opus1", "admins", "editors");

        http.expectPOST("/someContext/opus/updateUsers", {opusId: "opus1", admins: "admins", editors: "editors"}).respond("bla");
    })
});