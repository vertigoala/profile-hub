/** Tests the behaviour of the shared profile instance in the ProfileService */
describe("ProfileService shared profile tests", function () {
    var service;
    var http;

    beforeAll(function () {
        console.log("****** Profile Service Shared Profile Tests ******")
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function (_profileService_, _$httpBackend_) {
        service = _profileService_;
        http = _$httpBackend_;
    }));

    var profile = {
        profileId:'p1',
        uuid:'u1',
        attributes: [{
            title:'Common name',
            plainText:'a common name'
        }]
    };

    it("should return a shared reference to the profile when getProfile is called", function () {

        var profile1, profile2;
        var responder = http.when("GET", "/path/opus/opusId1/profile/profileId1/json").respond({profile:{profileId:'p1', uuid:'u1', prop:'1'}, opus:{}});

        service.getProfile("opusId1", "profileId1").then(function(data) {profile1 = data.profile;});

        responder.respond({profile:{profileId:'p1', uuid:'u1', prop:'2'}, opus:{}});
        service.getProfile("opusId1", "profileId1").then(function(data) {profile2 = data.profile;});
        http.flush();

        // The result of the second call should update the object reference returned by the first call
        expect(profile1 === profile2);
        expect(profile1.prop).toBe('2')
    });

    it("should return a shared reference to the profile when updateProfile is called", function () {

        var profile1, profile2;
        http.when("GET", "/path/opus/opusId1/profile/profileId1/json").respond({profile:{profileId:'p1', uuid:'u1', prop:'1'}, opus:{}});
        http.when("POST", "/path/opus/opusId1/profile/profileId1/update").respond({profileId:'p1', uuid:'u1', prop:'2'});

        service.getProfile("opusId1", "profileId1").then(function(data) {profile1 = data.profile;});

        service.updateProfile("opusId1", "profileId1", {}).then(function(data) {profile2 = data});
        http.flush();

        // The result of the second call should update the object reference returned by the first call
        expect(profile1 === profile2);
        expect(profile1.prop).toBe('2')
    });

    it("should determine other names for the profile by inspecting attributes", function () {

        var sharedProfile;
        http.when("GET", "/path/opus/opusId1/profile/profileId1/json").respond({profile:profile});
        service.getProfile("opusId1", "profileId1").then(function(data) {sharedProfile = data.profile;});
        http.flush();

        // The result of the second call should update the object reference returned by the first call
        expect(sharedProfile.otherNames).toEqual(['a common name']);


        sharedProfile.update({attributes:[{title:'name 1', plainText:'common name 1'}, {title:'name 2', plainText:'common name 2'}]});
        expect(sharedProfile.otherNames).toEqual(['common name 1', 'common name 2']);
    });

    it("should update the shared profile when attributes are updated", function() {

        var attribute = {title:'name 1', plainText:'common name 1'};

        http.expectGET("/path/opus/opusId1/profile/profileId1/json").respond({profile:{profileId:'p1', uuid:'u1', prop:'1', attributes:[attribute]}, opus:{}});
        http.when("PUT", "/path/opus/opusId1/profile/profileId1/attribute/create").respond("{}");

        service.saveAttribute("opusId1", "profileId1", null, attribute);

        http.flush();
        http.verifyNoOutstandingExpectation();
        http.verifyNoOutstandingRequest();

    });

    it("should update the shared profile when attributes are deleted", function() {

        http.expectGET("/path/opus/opusId1/profile/profileId1/json").respond({profile:{profileId:'p1', uuid:'u1', prop:'1', attributes:[]}, opus:{}});
        http.when("DELETE", "/path/opus/opusId1/profile/profileId1/attribute/attribute1/delete").respond("{}");

        service.deleteAttribute("opusId1", "profileId1", "attribute1");

        http.flush();
        http.verifyNoOutstandingExpectation();
        http.verifyNoOutstandingRequest();

    });

});