/**
 * The lazyTab directive is designed to work with the ui-bootstrap-tpls tabset and tab directive.
 * It replaces the tabs contents with a ng-include which is activated via setting the src
 * when the tab "select" event is fired.
 */
profileEditor.directive('lazyTab', ['$templateCache', function ($templateCache) {

    var count = 0;
    var templateNamePrefix = "lazy-tab-template-";

    var compile = function(tElement, tAttrs, transclude) {

        // Give each tabs contents a unique key in the template cache.
        count++;
        var templateName = templateNamePrefix+count;

        // Bind to the tabs public API for detecting select events so we can use it to start our lazy load.
        // Save any existing callback to execute after we load our content.
        var existingOnSelectAttribute = tElement.attr("select");
        // Override the select attribute to call our method.
        tAttrs.select = "lazyTabCtrl.doSelect()";

        // Remove the original tab content from the DOM and save it as a template.  This template will be loaded
        // when the tab select event is fired.
        $templateCache.put(templateName, tElement.html());
        tElement.contents().remove();

        var placeholderElement = angular.element('<div ng-include src="lazyTabCtrl.tabTemplate" onload="lazyTabCtrl.finishedLoading()"></div>');
        var loadingElement = angular.element('<loading state="lazyTabCtrl.loading"></loading>');
        tElement.append(placeholderElement);
        tElement.append(loadingElement);

        return function(scope, element, attrs) {
            scope.lazyTabCtrl = {
                loading:true,
                performOriginalOnSelectCallback: function() {
                    scope.$parent.$eval(existingOnSelectAttribute);
                },
                finishedLoading: function() {
                    this.loading = false;
                    this.performOriginalOnSelectCallback();
                },
                doSelect: function() {
                    if (!this.tabTemplate) {
                        this.tabTemplate = templateName;
                    }
                    else {
                        this.performOriginalOnSelectCallback();
                    }
                }
            }
        };
    };

    return {
        restrict: 'A',
        require: 'tab',
        scope:true,
        compile: compile
    }
}]);