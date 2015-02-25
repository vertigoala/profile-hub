/**
 * The primary purpose of these tests is to ensure that the correct urls are being constructed based on the provided parameters.
 */
describe("ProfileService tests", function () {
    var service;
    var http;
    var mockUtil = {
        contextRoot: function () {
            return "/someContext"
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

    it("should invoke the get opus vocab service on the context root when getOpusVocabulary is called", function () {
        service.getOpusVocabulary("vocabId1");

        http.expectGET("/someContext/vocab/vocabId1").respond("bla")
    });

    it("should invoke the get object audit service on the context root when getAuditForAttribute is called", function () {
        service.getAuditForAttribute("attrId1");

        http.expectGET("/someContext/audit/object/attrId1").respond("bla");
    });

    it("should invoke the delete attribute service on the context root when deleteAttribute is called", function () {
        service.deleteAttribute("attrId1", "profileId1");

        http.expectDELETE("/someContext/profile/deleteAttribute/attrId1?profileId=profileId1").respond("bla");
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
        service.getClassifications("guid1");

        http.expectGET("/someContext/profile/classifications?guid=guid1").respond("bla");
    });

    it("should invoke the retrieve species profile service on the context root when getSpeciesProfile is called", function () {
        service.getSpeciesProfile("guid1");

        http.expectGET("/someContext/profile/speciesProfile?guid=guid1").respond("bla");
    });
});