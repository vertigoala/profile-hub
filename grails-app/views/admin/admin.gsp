<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile Admin</title>
</head>

<body>
<div class="row-fluid">
    <div class="span6">
        <ol class="breadcrumb" role="navigation">
            <li><i class="fa fa-arrow-left"></i><span class="divider"/><a href="${request.contextPath}/"
                                                                          target="_self">View all profile collections</a>
            </li>
        </ol>
    </div>
    <g:render template="../layouts/login"/>
</div>

<div class="row-fluid" ng-app="profileEditor" ng-controller="ALAAdminController as adminCtrl" ng-cloak>
    Message: <input type="text" ng-model="adminCtrl.message" class="input-xxxlarge"/>
    <button class="btn btn-primary" ng-click="adminCtrl.postMessage()">Post message</button>
</div>

</body>
</html>




