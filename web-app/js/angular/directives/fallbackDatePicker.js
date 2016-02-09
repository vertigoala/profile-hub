profileEditor.directive('fallbackDatePicker', function ($browser) {
  return {
    restrict: 'E',
    require: 'ngModel',
    scope: {
      'ngModel': '=',
      'ngRequired': '=?',
      'dateOptions': '=?fallbackOptions',
      'size': '@',
      'format': '@',
      'fieldId': '@'
    },
    bindToController: true,
    templateUrl: $browser.baseHref() + 'static/templates/fallbackDatePicker.html',
    controllerAs: 'ctrl',
    controller: function() {

      var self = this;

      self.format = self.format || "dd/MM/yyyy"; // TODO user locale -> date format.
      self.size = self.size || '';
      self.dateOptions = self.dateOptions || {};
      self.dateOptions.type = self.dateOptions.type || 'date';
      if (angular.isUndefined(self.dateOptions.datepickerAppendToBody) || self.dateOptions.datepickerAppendToBody === null) { self.dateOptions.datepickerAppendToBody = false; }
      if (angular.isUndefined(self.dateOptions.showButtonBar) || self.dateOptions.showButtonBar === null) { self.dateOptions.showButtonBar = true; }

      switch (self.size) {
        case 'small':
          self.inputClass = 'input-sm';
          self.btnClass = 'btn-sm';
          break;
        default:
          self.inputClass = '';
          self.btnClass = '';
      }

      self.datePopupOpen = false;

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