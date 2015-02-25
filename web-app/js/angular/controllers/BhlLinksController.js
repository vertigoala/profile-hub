/**
 * BHL Links controller
 */
profileEditor.controller('BHLLinksEditor', function ($scope, profileService) {

    $scope.bhl = [];

    $scope.init = function (opus, profile, edit) {
        $scope.readonly = edit != 'true';

        var future = profileService.getProfile(util.getPathItem(util.LAST));

        future.then(function (data) {
            $scope.bhl = data.bhl;
            for (var i = 0; i < $scope.bhl.length; i++) {
                $scope.bhl[i].thumbnail = profiles.urls.bhlThumbUrl + extractPageId($scope.bhl[i].url);
            }
        });
    };

    function extractPageId(url) {
        console.log("URL " + url);
        var anchorIdx = url.lastIndexOf("#");
        if (anchorIdx > 0) {
            url = url.substring(0, anchorIdx - 1);
        }
        console.log("URL - stripped: " + url);

        var lastSlash = url.lastIndexOf("/");
        var pageId = url.substring(lastSlash + 1);
        console.log("URL - pageId " + pageId);
        return pageId;
    }

    $scope.hasThumbnail = function (idx) {
        return $scope.bhl[idx].thumbnail !== undefined && $scope.bhl[idx].thumbnail != '';
    };

    $scope.updateThumbnail = function (idx) {
        console.log("Updating...");
        var url = $scope.bhl[idx].url.trim();
        if (url != "") {
            //remove any anchors
            console.log("URL " + url);
            var pageId = extractPageId(url);
            $scope.bhl[idx].thumbnail = profiles.url.bhlThumbUrl + pageId;

            $.ajax({
                type: "GET",
                url: profiles.urls.bhlLookupUrl + "/" + pageId,
                success: function (data) {
                    $scope.bhl[idx].fullTitle = data.Result.FullTitle;
                    $scope.bhl[idx].edition = data.Result.Edition;
                    $scope.bhl[idx].publisherName = data.Result.PublisherName;
                    $scope.bhl[idx].doi = data.Result.Doi;
                    $scope.$apply();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log("There was a problem retrieving profile..." + textStatus);
                }
            })
        }
    };

    $scope.addLink = function () {
        $scope.bhl.unshift(
            {
                url: "",
                description: "",
                title: "",
                thumbnail: ""
            });
    };

    $scope.deleteLink = function (idx) {
        $scope.bhl.splice(idx, 1);
    };

    $scope.saveLinks = function () {
        //ajax post
        //alert("Saving BHL links");
        $.ajax({
            type: "POST",
            url: profiles.urls.bhlUpdateUrl + "/" + $scope.profile.uuid,
            dataType: "json",
            contentType: 'application/json',
            data: JSON.stringify({profileId: $scope.profile.uuid, links: $scope.bhl}),
            success: function (data) {
                console.log(data)
                $scope.$apply();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("Errored " + textStatus);
                $scope.$apply();
            }
        });
    }
});
