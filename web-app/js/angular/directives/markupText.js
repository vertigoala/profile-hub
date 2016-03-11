/**
 * This directive will take a block of text that may contain html and:
 *
 * 1) sanitize the html
 * 2) look for any img tag and wrap add an ng-click handler to display the image in the image viewer (assumes the images
 * have a url containing the image id)
 *
 * Usage: <div markup-text="[expression]"></div>
 */
profileEditor.directive('markupText', function ($compile) {
    var imageRegex = /<img (.*) src=(.*)\/image\/([0-9a-zA-Z-]+)\/(.*)>/g;

    var imageReplacementPattern = "<a href='' ng-click='imageCtrl.showMetadata(\'$2\')' title=\'View details\'><img $1 src=$2/image/$3/$4</a>";

    return function (scope, element, attrs) {
        scope.$watch(
            function (scope) {
                return scope.$eval(attrs.markupText);
            }, function (newValue) {
                if (newValue && angular.isDefined(newValue) && newValue.indexOf) {
                    console.log(newValue + " ------ " + newValue.indexOf("<img src=")) // TODO remove me!!
                    if (newValue.indexOf("<img") > -1) {
                        console.log("here 2 '" + newValue + "'" + typeof newValue) // TODO remove me!!
                        newValue = newValue.replace(imageRegex, imageReplacementPattern)
                        console.log("after " + newValue) // TODO remove me!!
                    }

                    element.html(newValue);
                    $compile(element.contents())(scope);
                }
            }
        );
    };
});