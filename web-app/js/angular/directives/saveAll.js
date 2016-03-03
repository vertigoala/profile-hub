/**
 * Directive to display a 'save all' button on the page which, when clicked, will find all buttons with the 'save-button'
 * class (see saveButton.js for a directive to create standard save buttons) in 'dirty' forms and trigger the ng-click
 * event handler.
 */
profileEditor.directive('saveAll', function ($browser) {
    var dirtySaveButtonSelector1 = ":not(form).ng-dirty button.save-button";

    return {
        restrict: 'E',
        require: [],
        templateUrl: $browser.baseHref() + 'static/templates/saveAllButton.html',
        controllerAs: "saveAllCtrl",
        controller: ['$scope', '$timeout', function($scope, $timeout) {
            var self = this;

            self.saveAll = function() {
                if ($scope.saveAllCtrl.dirtySaveButtons) {
                    var buttons = $(dirtySaveButtonSelector1);

                    $timeout(function() {
                        angular.forEach(buttons, function(button) {
                            angular.element($(button)).triggerHandler("click");
                        });
                    });
                }
            }
        }],
        link: function postLink(scope) {
            scope.$watch(function() {
                return $(dirtySaveButtonSelector1).length > 0;
            }, function(newVal) {
                scope.saveAllCtrl.dirtySaveButtons = newVal;
            });
        }
    };
});