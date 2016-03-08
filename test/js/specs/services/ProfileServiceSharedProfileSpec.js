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

});