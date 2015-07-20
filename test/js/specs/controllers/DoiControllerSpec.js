/**
 * Created by temi varghese on 15/07/15.
 */
describe("DoiController tests", function () {
    var controller;
    var scope;
    var util = {
        getQueryParameter: function (param) {
        },
        confirm: function (msg) {
        },
        getEntityId: function (entity) {
            return "pub2"
        }
    };

    beforeAll(function () {
        console.log("****** DOI Controller Tests ******");
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module('profileEditor'));

    beforeEach(inject(function ($controller, $rootScope, _$filter_) {
        scope = $rootScope.$new();

        controller = $controller("DoiController as doiCtrl", {
            $scope: scope,
            $filter: _$filter_,
            util: util
        });
    }));

    it("should select the current publication when the controller is loaded", function() {
        var publications = [{uuid: "pub1"}, {uuid: "pub2"}];
        var profile = {};
        var opus = {};

        scope.doiCtrl.init(publications, profile, opus);

        expect(scope.doiCtrl.selectedPublication.uuid).toBe("pub2")
    });

});
