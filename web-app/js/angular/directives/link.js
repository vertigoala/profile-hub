/**
 * Renders either a link (<a href>) if disable=false or a span if disable=true.
 */
profileEditor.directive('alaLink', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            disable: '@',
            href: '@',
            target: '@',
            title: '@'
        },
        transclude: true,
        templateUrl: $browser.baseHref() + 'static/templates/link.html'
    }
});