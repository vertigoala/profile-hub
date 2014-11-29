<!DOCTYPE html>
<html>
<head>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.3/angular.min.js" type="text/javascript" ></script>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${opus.title} | Profile collections</title>
</head>
<body>

<div ng-app="searchProfiles">

    <div class="row-fluid">

     <div class="span8">

        <div id="opusInfo" style="margin-top:20px;">
            <p class="lead">
                ${dataResource.pubDescription}
            </p>
        </div>

        <h2>Quick search</h2>
        <div ng-controller="ProfileSearch">
            <div class="input-append">
                <input ng-change="search()" ng-model="searchTerm" name="searchTerm" class="input-xxlarge" id="searchTerm" type="text" ng-model="scientificName" />
                <button class="btn" type="button">Search</button>
            </div>

            <table class="table table-striped" ng-show="profiles.length > 0">
                <tr>
                    <th>Taxon</th>
                </tr>
                <tr ng-repeat="profile in profiles">
                    <td><a href="${createLink(mapping: 'viewProfile')}/{{ profile.uuid }}">{{profile.scientificName}}</a></td>
                </tr>
            </table>

            <div ng-show="profiles.length == 0">
                <p>No matching results</p>
            </div>
        </div>
    </div>
    <div class="span4">
        <div class="well" style="margin-top:20px;">
            <div>
                <g:if test="${!edit}">
                    <g:link class="btn pull-right" mapping="editOpus"  params="[uuid:opus.uuid]"><i class="icon-edit"></i>&nbsp;Edit</g:link>
                </g:if>
                <g:else>
                    <g:link class="btn pull-right" mapping="viewOpus"  params="[uuid:opus.uuid]">Public view</g:link>
                </g:else>
            </div>

            <h3>Image sources</h3>
            <ul>
                <g:each in="${opus.imageSources}" var="imageSource">
                    <li><a href="http://collections.ala.org.au/public/show/${imageSource}">${dataResources[imageSource]}</a></li>
                </g:each>
            </ul>
            <h3>Specimen sources</h3>
            <ul>
                <g:each in="${opus.recordSources}" var="recordSource">
                    <li><a href="http://collections.ala.org.au/public/show/${recordSource}">${dataResources[recordSource]}</a></li>
                </g:each>
            </ul>
        </div>
    </div>
</div>

<script>

    var searchModule = angular.module('searchProfiles', [])
        .controller('ProfileSearch', ['$scope', function($scope) {

            $scope.search = function() {

                $.ajax({
                    url: "${grailsApplication.config.profile.service.url}/profile/search",
                    jsonp: "callback",
                    dataType: "jsonp",
                    data: {
                        opusUuid: "${opus.uuid}",
                        scientificName: $scope.searchTerm,
                        format: 'json'
                    },
                    success: function( data, status ) {
                        console.log("Success - " + data.length );
                        $scope.profiles = data;
                        if(data.length > 0){
                            console.log("Success - " + data[0].scientificName );
                        }
                        $scope.$apply();
                    },
                    error: function(jqXHR, textStatus, errorThrown){
                        console.log("Error completing images - JSON - " + errorThrown);
                    }
                });
            };
        }]);
</script>



</body>

</html>