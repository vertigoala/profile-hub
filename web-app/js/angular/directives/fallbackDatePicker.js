profileEditor.directive('fallbackDatePicker', function ($browser) {
  return {
    restrict: 'E',
    require: 'ngModel',
    scope: {
      'ngModel': '=',
      'format': '@',
      'fieldId': '@'
    },
    templateUrl: $browser.baseHref() + 'static/templates/fallbackDatePicker.html',
    controller: ['$scope', function($scope) {

      if (!$scope.format) $scope.format = "dd/MM/yyyy";  // TODO user locale -> date format.

      $scope.datePopupOpen = false;
      $scope.dateOptions = {
        startingDay: 1
      };

      $scope.openDatePicker = function($event) {
        if ($event) {
          $event.preventDefault();
          $event.stopPropagation();
        }
        $scope.datePopupOpen = true;
      };

      $scope.isDateInputSupported = Modernizr.inputtypes.date;
    }]
  };
});