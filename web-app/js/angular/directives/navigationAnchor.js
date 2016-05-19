/**
 * The navigationAnchor directive will render a link <a name=".."></a> with the navService
 * so that it can be navigated to via the table of contents or otherwise.
 *
 * It uses the attributes:
 * condition: (optional) angular expression that, if truthy, will cause this anchor to be registered with the navService. If ommited, the anchor will always be registered.
 * name: (required) the name to use in the anchor tag.
 * title (required) the title to register this anchor under (will be displayed in the table of contents)
 * category: (optional) the category to register this anchor under
 * onDisplay: (optional) a callback for the first time this anchor is displayed (used to allow lazy initialisation of contents on tabs)
 *
 */
profileEditor.directive('navigationAnchor', ['navService', function (navService) {

    return {
        restrict: 'EA',
        require: '^^?managedTab',
        scope: {
            condition:'&?',
            name:'@',
            title:'@',
            category:'@?',
            onDisplay:'&?'
        },
        bindToController:true,
        controllerAs:'navPointCtrl',
        controller: function($scope) {
            var self = this;

            self.getTab = function() {

                if (!self.managedTab) { // Requiring the managedTab directive isn't working, I believe due to the way the tabset transclusion works. We can require tabset but not tab or managedTab
                    if ($scope.$parent.managedTabCtrl) {
                        self.tab = $scope.$parent.managedTabCtrl.name; // the managedTab directive exposes the tab name for use by this directive
                    }
                }
                else {
                    self.tab = self.managedTab.name;
                }

                return self.tab;
            };

            $scope.$watch(self.condition, function() {
                if (_.isUndefined(self.condition()) || self.condition()) {
                    navService.add(self.title, self.name, self.category, self.getTab(), self.onDisplay);
                }
                else {
                    navService.remove(self.title, self.name);
                }
            });

        },
        template:'<a name="{{name}}"></a>'
    }
}]);