describe("Filter: capitalize", function () {
    var filter;

    beforeEach(module("profileEditor"));

    beforeEach(inject(function($filter) {
        filter = $filter("capitalize");
    }));

    it("should convert the first letter of the first word to upper case", function() {
        expect(filter("hello world")).toBe("Hello world")
    });

});


describe("Filter: default", function () {
    var filter;

    beforeEach(module("profileEditor"));

    beforeEach(inject(function($filter) {
        filter = $filter("default");
    }));

    it("should return the input value unmodified if it is defined", function() {
        expect(filter("input", "hello world")).toBe("input")
    });

    it("should return the default value if the input value is not truthy", function() {
        expect(filter("", "hello world")).toBe("hello world");
        expect(filter("   ", "hello world")).toBe("hello world");
        expect(filter(null, "hello world")).toBe("hello world");
    });
});