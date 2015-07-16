/**
 * Created by Temi Varghese on 15/07/15.
 */
profileEditor.directive('publication', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            authors: '=',
            description: '=',
            doi: '=',
            publicationDate: '=',
            title: '=',
            uploadDate: '=',
            uuid: '=',
            profileId: '=',
            opusId: '=',
            context: '=',
            version:'='
        },
        templateUrl: $browser.baseHref() + 'static/templates/publication.html',
        controller: ['$scope', function ($scope) {
            $scope.load = function (pub) {
                $scope.authors = pub.authors;
                $scope.description = pub.description;
                $scope.doi = pub.doi;
                $scope.publicationDate = pub.publicationDate;
                $scope.title = pub.title;
                $scope.uploadDate = pub.uploadDate;
                $scope.uuid = pub.uuid;
            };
        }],
        link: function (scope, element, attrs, ctrl) {

        }
    }
});