/**
 * Angular service for interacting with the profile service application
 */
profileEditor.factory('messageService', function ($rootScope) {

    var SUCCESS = "success";
    var ALERT = "danger";
    var INFO = "info";

    $rootScope.messages = [];

    function success(message, leaveExisting) {
        msg(SUCCESS, message, leaveExisting);
    }

    function alert(message, leaveExisting) {
        msg(ALERT, message, leaveExisting);
    }

    function info(message, leaveExisting) {
        msg(INFO, message, leaveExisting);
    }

    function msg(type, message, leaveExisting) {
        if (!leaveExisting) {
            pop();
        }
        $rootScope.messages.push({type: type, msg: message});
    }

    function pop() {
        $rootScope.messages.pop();
    }

    /**
     * Public API
     */
    return {
        success: success,
        info: info,
        alert: alert,
        pop: pop,

        SUCCESS: SUCCESS,
        ERROR: ALERT,
        INFO: INFO
    }
});