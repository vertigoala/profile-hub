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
        service.getProfile("opusId1", "profileId1");

        http.expectGET("/someContext/opus/opusId1/profile/profileId1/json").respond("bla");
    });

    it("should invoke the delete profile service on the context root when deleteProfile is called", function () {
        service.deleteProfile("opusId", "profileId");

        http.expectDELETE("/someContext/opus/opusId/profile/profileId/delete").respond("bla");
    });

    it("should invoke the createProfile service on the context root when createProfile is called", function() {
        service.createProfile("opusId", "scientificName");

        http.expectPUT("/someContext/opus/opusId/profile/create", {opusId: "opusId", scientificName: "scientificName"}).respond("bla");
    });

    it("should invoke the updateProfile service on the context root when udateProfile is called", function() {
        var data = {primaryImage: "one"};
        service.updateProfile("opusId", "profileId", data);

        http.expectPOST("/someContext/opus/opusId/profile/profileId/update", data).respond("bla");
    });

    it("should invoke the get opus as json service on the context root when getOpus is called", function () {
        service.getOpus("opusId");

        http.expectGET("/someContext/opus/opusId/json").respond("bla");
    });

    it("should invoke the list opus service on the context root when listOpus is called", function() {
        service.listOpus();

        http.expectGET("/someContext/opus/list").respond("bla");
    });

    it("should invoke the update opus as json service on the context root when saveOpus is called", function () {
        var data = {opusId: "opusId", imageSources: ["one", "two"]};
        service.saveOpus("opusId", data);

        http.expectPOST("/someContext/opus/opusId/update", data).respond("bla");
    });

    it("should invoke the create opus service on the context root when saveOpus is called with no opusid", function () {
        var data = {imageSources: ["one", "two"]};
        service.saveOpus(null, data);

        http.expectPUT("/someContext/opus/create", data).respond("bla");
    });

    it("should invoke the delete opus operation on the context root when deleteOpus is called", function() {
        service.deleteOpus("opus1");

        http.expectDELETE("/someContext/opus/opus1/delete").respond("bla");
    });

    it("should invoke the get opus vocab service on the context root when getOpusVocabulary is called", function () {
        service.getOpusVocabulary("opusId", "vocabId1");

        http.expectGET("/someContext/opus/opusId/vocab/vocabId1").respond("bla")
    });

    it("should invoke the update vocab service on the context root when updateVocabulary is called", function () {
        var data = {name: "vocab1", "strict": "true", "terms": []};
        service.updateVocabulary("opusId", "vocabId1", data);

        http.expectPOST("/someContext/opus/opusId/vocab/vocabId1/update", data).respond("bla")
    });

    it("should invoke the find term usages service on the context root when findUsagesOfVocabTerm is called", function () {
        service.findUsagesOfVocabTerm("opusId", "vocabId1", "termName");

        http.expectGET("/someContext/opus/opusId/vocab/vocabId1/findUsages?termName=termName").respond("bla")
    });

    it("should invoke the get opus vocab service on the context root when getOpusVocabulary is called", function () {
        var replacements = [{vocabId: "vocab1", existingTermName: "old name", newTermName: "new name"}];
        service.replaceUsagesOfVocabTerm("opusId", "vocabId", replacements);

        http.expectPOST("/someContext/opus/opusId/vocab/vocabId/replaceUsages", replacements).respond("bla")
    });

    it("should invoke the get object audit service on the context root when getAuditForAttribute is called", function () {
        service.getAuditForAttribute("attrId1");

        http.expectGET("/someContext/audit/object/attrId1").respond("bla");
    });

    it("should invoke the delete attribute service on the context root when deleteAttribute is called", function () {
        service.deleteAttribute("opusId", "profileId1", "attrId1");

        http.expectDELETE("/someContext/opus/opusId/profile/profileId1/attribute/attrId1/delete").respond("bla");
    });

    it("should invoke the update attribute service on the context root when saveAttribute is called", function () {
        var data = "{attribute data}";
        service.saveAttribute("opusId", "profileId1", "attrId1", data);

        http.expectPOST("/someContext/opus/opusId/profile/profileId1/attribute/attrId1/update", data).respond("bla");
    });

    it("should invoke the retrieve images service on the context root when retrieveImages is called", function () {
        service.retrieveImages("opusId", "profileId", "searchId1", "sourceList");

        http.expectGET("/someContext/opus/opusId/profile/profileId/images?searchIdentifier=searchId1&imageSources=sourceList").respond("bla");
    });

    it("should invoke the retrieve lists service on the context root when retrieveLists is called", function () {
        service.retrieveLists("opusId", "profileId", "guid1");

        http.expectGET("/someContext/opus/opusId/profile/profileId/lists?guid=guid1").respond("bla");
    });

    it("should invoke the retrieve classifications service on the context root when getClassifications is called", function () {
        service.getClassifications("opusId", "profileId", "guid1");

        http.expectGET("/someContext/opus/opusId/profile/profileId/classifications?guid=guid1").respond("bla");
    });

    it("should invoke the retrieve species profile service on the context root when getSpeciesProfile is called", function () {
        service.getSpeciesProfile("opusId", "profileId", "guid1");

        http.expectGET("/someContext/opus/opusId/profile/profileId/speciesProfile?guid=guid1").respond("bla");
    });

    it("should invoke the update links service on the context root when updateLinks is called", function() {
        var data = "list of links";
        service.updateLinks("opusId", "profileId", data);

        http.expectPOST("/someContext/opus/opusId/profile/profileId/links/update", data).respond("bla");
    });

    it("should invoke the update BHL links service on the context root when updateBhlLinks is called", function() {
        var data = "list of links";
        service.updateBhlLinks("opusId", "profileId", data);

        http.expectPOST("/someContext/opus/opusId/profile/profileId/bhllinks/update", data).respond("bla");
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

        http.expectGET("/someContext/dataResource").respond("bla");
    });

    it("should invoke the profile search operation on the context root when profileSearch is called ", function() {
        service.profileSearch("opusId", "scientificName");

        http.expectGET("/someContext/profile/search?opusId=opusId&scientificName=scientificName&useWildcard=true").respond("bla");
    });

    it("should invoke the profile search operation on the context root with wildcard=false if false is passed in when profileSearch is called ", function() {
        service.profileSearch("opusId", "scientificName", false);

        http.expectGET("/someContext/profile/search?opusId=opusId&scientificName=scientificName&useWildcard=false").respond("bla");
    });

    it("should invoke the user search operation on the context root when userSearch is called ", function() {
        service.userSearch("fred");

        http.expectGET("/someContext/user/search?userName=fred").respond("bla");
    });

    it("should invoke the update user operation on the context root when updateUser is called ", function() {
        service.updateUsers("opus1", "admins", "editors");

        http.expectPOST("/someContext/opus/opus1/users/update", {opusId: "opus1", admins: "admins", editors: "editors"}).respond("bla");
    });

    it("should invoke the upload glossary operation on the context root when uploadGlossary is called", function() {
        var formData = new FormData();
        service.uploadGlossary("opus1", formData);

        http.expectPOST("/someContext/opus/opus1/glossary/upload", formData).respond("bla");
    });

    it("should invoke the get glossary operation on the PROFILE SERVICE when getGlossary is called", function() {
        service.getGlossary("opus1", "prefix");

        http.expectGET("http://profileService/glossary/opus1/prefix").respond("bla");
    });

    it("should invoke the delete glossary item operation on the context root when deleteGlossaryItem is called", function() {
        service.deleteGlossaryItem("opus1", "item1");

        http.expectDELETE("/someContext/opus/opus1/glossary/item/item1/delete").respond("bla");
    });

    it("should invoke the create glossary item operation on the context root when saveGlossaryItem is called with no item id", function() {
        var data = {term: "something"};
        service.saveGlossaryItem("opus1", null, data);

        http.expectPUT("/someContext/opus/opus1/glossary/item/create", data).respond("bla");
    });

    it("should invoke the create glossary item operation on the context root when saveGlossaryItem is called with no item id", function() {
        var data = {term: "something"};
        service.saveGlossaryItem("opus1", "item1", data);

        http.expectPOST("/someContext/opus/opus1/glossary/item/item1/update", data).respond("bla");
    });
});