describe("ReportController tests", function () {
    var controller;
    var scope;
    var messageService;
    var profileService;
    var util = {
        getQueryParameter: function(param) {},
        confirm: function(msg) {},
        getEntityId: function(entity) {return "1234"}
    };
    var config;
    var uploadDefer, loadReportDefer;

    var getReportResponse = '{"recordCount":10961,"records":[{"editor":"Unknown","scientificName":"Acetosa","profileId":"32a56222-596d-4da3-b299-d9e423f36d51","lastUpdated":"2015-07-03T04:03:04Z"},{"editor":null,"scientificName":"Abelia x grandiflora","profileId":"f9388454-1b1a-4acb-b0f0-c8e25d722ec5","lastUpdated":"2015-07-03T03:51:30Z"}]}';

    beforeAll(function () {
        console.log("****** Report Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module('profileEditor'));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_) {
        scope = $rootScope.$new();
        profileService = _profileService_;
        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        loadReportDefer = $q.defer();

        spyOn(profileService, "loadReport").and.returnValue(loadReportDefer.promise);

        controller = $controller("ReportController as reportCtrl", {
            $scope: scope,
            profileService: profileService,
            util: util,
            config: {},
            messageService: messageService
        });
    }));

    it("check if report controller initialised properly", function() {
        loadReportDefer.resolve(JSON.parse(getReportResponse))

        var reportId = scope.reportCtrl.reports[2].id;
        console.log(reportId);
        scope.reportCtrl.loadReport(reportId,0);

        expect(profileService.loadReport).toHaveBeenCalled();
        expect(scope.reportCtrl.reportData).toBeDefined();
        expect(scope.reportCtrl.reportData['recordCount']).toBeDefined();
    });

});