<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile Admin</title>
</head>

<body>

<div ng-controller="ALAAdminController as adminCtrl" ng-cloak>
    <g:if test="${flash.message}">
        <div class="alert alert-info">${flash.message}</div>
    </g:if>

    <g:render template="/ala-admin-form" plugin="ala-admin-plugin"/>

    <hr/>

    <div class="panel-heading">
        <h3>Search index</h3>
    </div>
    <div class="panel-body">
        <g:form controller="alaAdmin" action="reloadConfig">
            <p>This lets you rebuild the Elastic Search index used for the free-text search feature</p>
            <button class="btn btn-primary" ng-click="adminCtrl.reindex()">Rebuild search index</button>
        </g:form>
    </div>
    <hr/>
</div>
</body>
</html>




