<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile Admin</title>
</head>

<body>

<div ng-controller="ALAAdminController as adminCtrl" ng-cloak>
    <div class="row">
        <div class="col-md-12">
            <label for="outageMessage">Message</label>

            <div class="input-group">
                <input id="outageMessage" type="text" ng-model="adminCtrl.message" class="form-control"/>
                <span class="input-group-btn">
                    <button class="btn btn-primary " ng-click="adminCtrl.postMessage()">Post message</button>
                </span>
            </div>

        </div>

        <hr class="col-md-12"/>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="form-group">
                <button class="btn btn-primary" ng-click="adminCtrl.reloadConfig()">Reload external config</button>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="form-group">
                <button class="btn btn-primary" ng-click="adminCtrl.reindex()">Rebuild search index</button>
            </div>
        </div>
    </div>
</div>
</body>
</html>




