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
        <div class="col-md-12 col-xs-12 col-lg-12 padding-top-1" ng-show="aboutCtrl.hasCitation()">
            <p >This work should be cited as:</p>
            <div ta-bind ng-model="aboutCtrl.citationHtml"></div>
        </div>
        <div class="col-md-12 col-xs-12 col-lg-12" ng-show="aboutCtrl.hasCitation()">
            <p>E.g.:</p>
            <div class="citation-example">
                <p>Conn, B.J. ({{aboutCtrl.citationYear}}) Loganiaceae. In: <span ta-bind ng-model="aboutCtrl.citationHtml"></span> <a href="{{aboutCtrl.citationUrl}}">{{aboutCtrl.citationUrl}}</a>. {{aboutCtrl.citationDate}}</p>
            </div>
        </div>
    </div>
</div>

</body>
</html>

