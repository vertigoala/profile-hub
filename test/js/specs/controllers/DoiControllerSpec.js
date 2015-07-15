/**
 * Created by temi varghese on 15/07/15.
 */
describe("DoiController tests", function () {
    var controller;
    var scope;
    var messageService;
    var profileService;
    var util = {
        getQueryParameter: function (param) {
        },
        confirm: function (msg) {
        },
        getEntityId: function (entity) {
            return "8fb8eecb-6e73-4383-b4a7-da98f6510cbc"
        }
    };
    var config;
    var loadPubDefer;

    var getPublicationsResponse = '{"uuid":"12d47e91-80ee-4317-989e-bd7024adda15","opusId":"fe3c9c46-29a7-4765-a3ba-c6941cff672c","scientificName":"Acacia dealbata","publications":[{"uuid":"8fb8eecb-6e73-4383-b4a7-da98f6510cbc","title":"Acacia dealbata","description":null,"publicationDate":"2015-07-13T07:06:45Z","uploadDate":null,"authors":"P.G. Kodela","doi":"tempDOI_1436771205845"},{"uuid":"5377de5c-64c3-45fc-9394-a59fbad56f48","title":"Acacia dealbata","description":null,"publicationDate":"2015-07-14T12:07:09Z","uploadDate":null,"authors":"P.G. Kodela","doi":"tempDOI_1436875629489"},{"uuid":"080ff79d-b2d9-4fdf-91cf-67a8ef10b88b","title":"Acacia dealbata","description":null,"publicationDate":"2015-07-14T12:17:12Z","uploadDate":null,"authors":"P.G. Kodela","doi":"tempDOI_1436876232042"}]}';

    beforeAll(function () {
        console.log("****** DOI Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module('profileEditor'));

    beforeEach(inject(function ($controller, $rootScope, _profileService_, $q, _messageService_, _$filter_) {
        scope = $rootScope.$new();
        profileService = _profileService_;
        messageService = jasmine.createSpyObj(_messageService_, ["success", "info", "alert", "pop"]);

        loadPubDefer = $q.defer();

        spyOn(profileService, "getPublicationsFromId").and.returnValue(loadPubDefer.promise);

        controller = $controller("DoiController as doiCtrl", {
            $scope: scope,
            $filter: _$filter_,
            profileService: profileService,
            util: util,
            messageService: messageService
        });
    }));

    it("when loadPublication is called, it should call profileService.getPulicationFromId", function () {
        loadPubDefer.resolve(JSON.parse(getPublicationsResponse));
        scope.doiCtrl.loadPublications()
        scope.$apply();

        expect(profileService.getPublicationsFromId).toHaveBeenCalled()
        expect(scope.doiCtrl.publications.length).toEqual(2);
        expect(scope.doiCtrl.selectedPublication.uuid).toEqual("8fb8eecb-6e73-4383-b4a7-da98f6510cbc");
    });
});
