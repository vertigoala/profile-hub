profileEditor.directive('fallbackDatePicker', function ($browser) {
  return {
    restrict: 'E',
    require: 'ngModel',
    scope: {
      'ngModel': '=',
      'format': '@',
      'fieldId': '@'
    },
    bindToController: true,
    templateUrl: $browser.baseHref() + 'static/templates/fallbackDatePicker.html',
    controllerAs: 'ctrl',
    controller: function() {

      var self = this;

      if (!self.format) self.format = "dd/MM/yyyy";  // TODO user locale -> date format.

      self.datePopupOpen = false;
      self.dateOptions = {
        startingDay: 1
      };

      self.openDatePicker = function($event) {
        if ($event) {
          $event.preventDefault();
          $event.stopPropagation();
        }
        self.datePopupOpen = true;
      };

      self.isDateInputSupported = Modernizr.inputtypes.date;
    }
  };
});