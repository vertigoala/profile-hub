<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile Admin</title>
</head>

<body>

<div class="row" ng-controller="ALAAdminController as adminCtrl" ng-cloak>
    <div class="form-group">
        Message: <input type="text" ng-model="adminCtrl.message" class="form-control"/>
        <button class="btn btn-primary" ng-click="adminCtrl.postMessage()">Post message</button>
    </div>
</div>

</body>
</html>




