describe("Util tests", function () {
    var service;
    var location;
    var browser;

    beforeAll(function () {
        console.log("****** Util Tests ******")
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function (_util_, _$location_, _$browser_) {
        service = _util_;
        location = _$location_;
        browser = _$browser_;
    }));

    it("should return the first item in the path if given 0 OR FIRST", function () {
        location.path("/part1/part2/part3");

        expect(service.getPathItem(0)).toBe("part1");
        expect(service.getPathItem(service.FIRST)).toBe("part1");
    });

    it("should return the last item in the path if given the last index OR LAST", function () {
        location.path("/part1/part2/part3");

        expect(service.getPathItem(2)).toBe("part3");
        expect(service.getPathItem(service.LAST)).toBe("part3");
    });

    it("should ignore the query string", function () {
        location.path("/part1/part2/part3?foo=baa");

        expect(service.getPathItem(2)).toBe("part3");
        expect(service.getPathItem(service.LAST)).toBe("part3");
    });

});

