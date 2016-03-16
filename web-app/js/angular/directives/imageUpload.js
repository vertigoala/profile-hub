profileEditor.directive('imageUpload', function ($browser) {
    return {
        restrict: 'AE',
        require: [],
        scope: {
            opus: '=',
            callbackFn: '&onUploadComplete', // function to be invoked when the upload is complete - takes a single parameter: the image metadata object
            uploadOnEvent: '@' // name of the event to be $broadcast by the parent scope to trigger the upload (e.g. when embedding the upload form in a larger form with a single OK button)
        },
        templateUrl: $browser.baseHref() + 'static/templates/imageUpload.html',
        controller: ['$scope', 'profileService', 'util', 'Upload', '$cacheFactory', '$filter', function ($scope, profileService, util, Upload, $cacheFactory, $filter) {
            $scope.metadata = {rightsHolder: $scope.opus.title};
            $scope.files = null;
            $scope.error = null;

            $scope.callbackHandler = $scope.callbackFn();

            $scope.licences = null;

            $scope.valid = false;

            var orderBy = $filter("orderBy");

            profileService.getLicences().then(function (data) {
                $scope.licences = orderBy(data, "name");
                $scope.metadata.licence = $scope.licences[0];
            });

            $scope.doUpload = function () {
                if ($scope.files.length > 0) {
                    $scope.metadata.dataResourceId = $scope.opus.dataResourceUid;
                    $scope.metadata.licence = $scope.metadata.licence.name;

                    Upload.upload({
                        url: util.contextRoot() + "/opus/" + util.getEntityId("opus") + "/profile/" + util.getEntityId("profile") + "/image/upload",
                        fields: $scope.metadata,
                        file: $scope.files[0]
                    }).success(function (imageMetadata) {
                        $scope.image = {};
                        $scope.file = null;
                        $cacheFactory.get('$http').removeAll();

                        if (angular.isDefined($scope.callbackHandler)) {
                            $scope.callbackHandler(imageMetadata);
                        }
                    }).error(function () {
                        $scope.error = "An error occurred while uploading your image."
                    });
                } else {
                    console.error("'performUpload' event broadcase when there are 0 files to be uploaded");
                }
            };

            $scope.$on($scope.uploadOnEvent, $scope.doUpload);
        }]
    };
});