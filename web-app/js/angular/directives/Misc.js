/**
 * Trigger a function call when the enter key is pressed
 */
profileEditor.directive('ngEnter', function () {
    return function (scope, element, attrs) {
        element.bind("keydown keypress", function (event) {
            if (event.which === 13) {
                scope.$apply(function () {
                    scope.$eval(attrs.ngEnter);
                });

                event.preventDefault();
            }
        });
    };
});

/**
 * Display a loading spinner
 */
profileEditor.directive('loading', function () {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            state: '='
        },
        template: '<div ng-if="state"><span class="fa fa-spin fa-spinner"></span>&nbsp;Loading...</div>'
    }
});

/**
 * Display a span with an icon and optional text to indicate some sort of status
 */
profileEditor.directive('statusIndicator', function () {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            iconClass: '@',
            text: '@',
            title: '@',
            link: '@'
        },
        template: '<span ng-if="!link" class="status {{iconClass}}" title="{{title}}">{{text}}</span>' +
        '<a href="{{link}}" ng-if="link" target="_blank"><span class="status {{iconClass}}" title="{{title}}">{{text}}</span></a>'
}
});