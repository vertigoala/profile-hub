<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>About ${pageTitle}</title>
</head>

<body>

<div ng-controller="AboutController as aboutCtrl" class="container">
    <div class="margin-bottom-2"></div>
    <div class="row">
        <div class="col-md-12 col-xs-12 col-lg-12">
            <div ta-bind ng-model="aboutCtrl.aboutHtml"></div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12 col-xs-12 col-lg-12">
            <p ng-show="aboutCtrl.hasCitation()">This work should be cited as:</p>
            <div ta-bind ng-model="aboutCtrl.citationHtml"></div>
        </div>
    </div>
</div>

</body>
</html>

