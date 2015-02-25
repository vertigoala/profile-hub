describe("MessageService tests", function () {
    var service;
    var rootScope;

    beforeAll(function () {
        console.log("****** Message Service Tests ******")
    });
    afterAll(function () {
        console.log("----------------------------");
    });

    beforeEach(module("profileEditor"));

    beforeEach(inject(function (_messageService_, _$rootScope_) {
        service = _messageService_;
        rootScope = _$rootScope_;
    }));

    it("should add a new success message to the root scope when success is called", function () {
        service.success("test success message");

        expect(rootScope.messages.length).toBe(1);
        expect(rootScope.messages[0].type).toBe(service.SUCCESS);
        expect(rootScope.messages[0].msg).toBe("test success message");
    });

    it("should add a new info message to the root scope when info is called", function () {
        service.info("test info message");

        expect(rootScope.messages.length).toBe(1);
        expect(rootScope.messages[0].type).toBe(service.INFO);
        expect(rootScope.messages[0].msg).toBe("test info message");
    });

    it("should add a new alert message to the root scope when alert is called", function () {
        service.alert("test alert message");

        expect(rootScope.messages.length).toBe(1);
        expect(rootScope.messages[0].type).toBe(service.ERROR);
        expect(rootScope.messages[0].msg).toBe("test alert message");
    });

    it("should remove existing messages before adding the new one by default", function () {
        service.info("test info message 1");
        service.info("test info message 2");

        expect(rootScope.messages.length).toBe(1);
        expect(rootScope.messages[0].type).toBe(service.INFO);
        expect(rootScope.messages[0].msg).toBe("test info message 2");
    });

    it("should not remove existing messages before adding the new one if leaveExisting = true", function () {
        service.info("test info message 1", true);
        service.info("test info message 2", true);

        expect(rootScope.messages.length).toBe(2);
        expect(rootScope.messages[0].msg).toBe("test info message 1");
        expect(rootScope.messages[1].msg).toBe("test info message 2");
    });

    it("should remove the last message added when pop is called", function () {
        service.info("test info message 1", true);
        service.info("test info message 2", true);

        service.pop();

        expect(rootScope.messages.length).toBe(1);
        expect(rootScope.messages[0].msg).toBe("test info message 1");
    });
});

