<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile Admin</title>
</head>

<body>

<div>
    <g:if test="${flash.message}">
        <div class="alert alert-info">${flash.message}</div>
    </g:if>

    <div class="panel-heading">
        <h3>General admin</h3>
    </div>

    <div class="panel-body">
        <a href="${request.contextPath}/alaAdmin2" class="btn btn-primary">Go to ALA admin page</a>
    </div>

    <hr/>

    <div class="panel-heading">
        <h3>Search index</h3>
    </div>

    <div ng-controller="ALAAdminController as adminCtrl" ng-cloak class="panel-body">
        <p>This lets you rebuild the Elastic Search index used for the free-text search feature</p>
        <button class="btn btn-primary" ng-click="adminCtrl.reindex()">Rebuild search index</button>
    </div>
    <hr/>

    <div class="panel-heading">
        <h3>Rematch names</h3>
    </div>

    <div ng-controller="ALAAdminController as adminCtrl" ng-cloak class="panel-body">
        <p>This lets you rematch all taxa names for all profiles in selected collections</p>
        <dualmultiselect options="adminCtrl.collectionMultiSelectOptions"></dualmultiselect>

        <button class="btn btn-primary" ng-click="adminCtrl.rematchNames()">Rematch profile names</button>
    </div>
    <hr/>
</div>
</body>
</html>




