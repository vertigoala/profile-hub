<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <meta name="logoUrl" content="${logoUrl}"/>
    <title>Profile collections | Atlas of Living Australia</title>

    <r:require module="profiles"/>
</head>

<body>

<div class="vertical-pad"/>

<div class="container" ng-app="profileEditor" ng-controller="OpusController as opusCtrl">
    <div class="row flexbox">
        <div ng-repeat="opus in opusCtrl.opusList | orderBy: 'title'" class="col-lg-3 col-md-3 col-sm-3">
            <a href="${request.contextPath}/opus/{{opus.uuid}}" target="_self">
                <img src="{{opus.thumbnailUrl | default:'${request.contextPath}/images/generic_flower.png'}}"
                     alt="{{opus.title}} logo" title="{{opus.title}}"
                     class="img-thumbnail img-responsive img-circle collection-thumbnail">
            </a>

            <h3 class="text-center">{{opus.title}}</h3>
        </div>
    </div>
</div>

<div class="pull-right">
    <a href="${request.contextPath}/opus/create" target="_self">Create a new collection</a>
</div>

</body>

</html>