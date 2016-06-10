/**
 * Standard format for a button to save a form/subform. Using this directive will ensure that the button is structured
 * consistently, has consistent behaviour around disabling and marking dirty changes etc, and that it will work with the
 * saveAllButton directive.
 */
profileEditor.directive('saveButton', function ($browser) {
    return {
        restrict: 'E',
        templateUrl: $browser.baseHref() + 'static/templates/saveButton.html',
        replace: true,
        scope: {
            disabled: "=",
            form: "=?",
            dirty: "=",
            show: "=ngShow",
            hide: "=ngHide",
            btnClass: "@",
            label: "@"
        }
    };
});