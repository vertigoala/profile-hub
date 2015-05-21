<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile collections</title>
</head>

<body>

<div ng-app="profileEditor" ng-controller="AboutController as aboutCtrl">
    <div ta-bind ng-model="aboutCtrl.aboutHtml"></div>
</div>

</body>
</html>

