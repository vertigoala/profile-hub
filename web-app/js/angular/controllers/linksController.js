/**
 *  Links controller
 */
profileEditor.controller('LinksEditor', function ($scope) {
    $scope.links = [];

    $scope.init = function (opus, profile, edit) {
        $scope.opus = $.parseJSON(opus);
        $scope.profile = $.parseJSON(profile);
        $scope.readonly = edit != 'true';
        initialiseUrls();

        $.ajax({
            type: "GET",
            url: profiles.urls.profileServiceBaseUrl + "/profile/" + $scope.profile.uuid,
            success: function (data) {
                $scope.links = data.links;
                $scope.$apply();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log("There was a problem retrieving profile..." + textStatus);
            }
        })
    };

    $scope.addLink = function () {
        $scope.links.unshift({uuid: "", url: "http://", description: "Add description", title: "Title"});
    };

    $scope.deleteLink = function (idx) {
        $scope.links.splice(idx, 1);
    };

    $scope.saveLinks = function () {
        //ajax post
        $.ajax({
            type: "POST",
            url: profiles.urls.linksUpdateUrl + "/" + $scope.profile.uuid,
            dataType: "json",
            contentType: 'application/json',
            data: JSON.stringify({profileUuid: $scope.profile.uuid, links: $scope.links}),
            success: function (data) {
                $scope.$apply();
            },
            error: function (jqXHR, textStatus, errorThrown) {
                alert("Errored " + textStatus);
                $scope.$apply();
            }
        });
    }
});