/**
 * Created by Temi Varghese on 15/07/15.
 */
profileEditor.directive('publication', function ($browser) {
    return {
        restrict: 'E',
        require: [],
        scope: {
            publication: '=data',
            opusId: '=',
            profileId: '='
        },
        templateUrl: $browser.baseHref() + 'static/templates/publication.html',
        controller: ['$scope', 'config', function ($scope, config) {
            $scope.context = config.profileServiceUrl;
        }],
        link: function (scope, element, attrs, ctrl) {

        }
    }
});