<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <meta name="logoUrl" content="${logoUrl}"/>
    <title>Profile collections | Atlas of Living Australia</title>

    <r:require module="profiles"/>
</head>

<body>

<div class="container-fluid" ng-app="profileEditor" ng-controller="OpusController as opusCtrl">
    <div class="row-fluid flexbox">
        <div class="span12">
            <div class="span6"></div>
            <g:render template="../layouts/login"/>
        </div>

        <div ng-repeat="opus in opusCtrl.opusList | orderBy: 'title'" class="col-lg-2 col-md-3 col-sm-3">
            <a href="${request.contextPath}/opus/{{opus.uuid}}" target="_self">
                <img src="{{opus.thumbnailUrl | default:'${request.contextPath}/images/generic_flower.png'}}"
                     alt="{{opus.title}} logo" title="{{opus.title}}"
                     class="img-thumbnail img-responsive img-circle collection-thumbnail">
            </a>

            <h4 class="text-center">{{opus.title}}</h4>
        </div>
    </div>

    <g:if test="${params.isALAAdmin}">
        <div class="pull-right">
            <a class="btn btn-info" href="${request.contextPath}/opus/create" target="_self">Create a new collection</a>
        </div>
    </g:if>
</div>
</body>

</html>