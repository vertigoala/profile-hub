profileEditor.controller('CustomAlertController', function CustomAlertController($rootScope, $scope, $http, $location, $timeout) {
    $scope.alerts = [];

    $rootScope.$on('displayAlerts', function (event, alerts, leaveExisting) {
        if (!leaveExisting) {
            pop();
        }

        $scope.alerts = alerts;

        var length = 0;

        for (var i = 0; i < alerts.length; i++) {
            length += alerts[i]['msg'].length;
        }

        length = (length * 80) + 1000;
        if (!$.isEmptyObject(alerts)) {
            var timeout = $timeout(function () {
                $('#generalAlert div.alert').fadeOut({duration: 2000});
                $timeout.cancel(timeout);
            }, length);
        }
    });

    function pop() {
        $scope.alerts.pop();
    }

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };
});

