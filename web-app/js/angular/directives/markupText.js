/**
 * This directive will take a block of text that may contain html and:
 *
 * 1) look for any img tag and wrap add an ng-click handler to display the image in the image viewer (assumes the images
 * have a url containing the image id)
 *
 * Usage: <div markup-text="[expression]"></div>
 */
profileEditor.directive('markupText', function ($compile) {
    var imageRegex = /<img(.*?)src=(.*?)\/image\/([0-9a-zA-Z-]+)(\.[a-z]{3,4}[\?"'])(.*?)>/gi;

    var imageReplacementPattern = "<a href=\"\" ng-click=\"imageCtrl.showMetadata(\'$3\')\" title=\"View details\"><img$1src=$2/image/$3$4$5></a>";

    return function (scope, element, attrs) {
        scope.$watch(attrs.markupText, function (newValue) {
                if (newValue && angular.isDefined(newValue) && newValue.indexOf) {
                    var markedUp = newValue;
                    if (newValue.match(imageRegex)) {
                        markedUp = newValue.replace(imageRegex, imageReplacementPattern);
                    }
                    element.html(markedUp);
                    $compile(element.contents())(scope);
                }
            }
        );
    };
});