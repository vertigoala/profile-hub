<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${pageTitle}</title>
</head>

<body>

<div class="container">
    <div class="margin-bottom-2"></div>
    <h2 class="heading-large">${pageTitle}</h2>
    <div class="row" ng-controller="AboutController as aboutCtrl">
        <div class="col-md-12 col-xs-12 col-lg-12">
            <div ta-bind ng-model="aboutCtrl.aboutHtml"></div>
        </div>
        <div class="col-md-12 col-xs-12 col-lg-12 padding-top-1" ng-show="aboutCtrl.hasCitation()">
            <h4>Citations</h4>
            <p>This collection should be cited as:</p>
            <div ta-bind ng-model="aboutCtrl.citationHtml" class="padding-left-1"></div>
        </div>
        <div class="col-md-12 col-xs-12 col-lg-12" ng-show="aboutCtrl.hasCitation()">
            <p>The taxon profiles in this collection should be cited as per the following example:</p>
            <div class="citation-example">
                <p class="padding-left-1">Conn, B.J. ({{aboutCtrl.citationYear}}) Loganiaceae. In: <span ta-bind ng-model="aboutCtrl.citationHtml"></span>. <a href="{{aboutCtrl.citationUrl}}">{{aboutCtrl.citationUrl}}</a>. {{aboutCtrl.citationDate}}</p>
            </div>
        </div>

        <div class="col-md-12 col-xs-12 col-lg-12 padding-top-1" ng-cloak>
            <h4>Collection administration</h4>
            <p>This collection is administered by:</p>
            <ul>
                <li ng-repeat="admin in aboutCtrl.administrators">
                    <a href="mailto:{{admin.email}}">{{ admin.name }}</a>
                </li>
            </ul>
        </div>

        <div class="col-md-12 col-xs-12 col-lg-12 padding-top-1" ng-cloak>
            <a name="copyright"></a>

            <h4>Copyright</h4>

            <p ng-show="aboutCtrl.collectionCopyright">&copy; {{ aboutCtrl.collectionCopyright }}</p>

            <div data-ng-bind-html="aboutCtrl.genericCopyrightHtml"></div>
        </div>
    </div>
</div>

</body>
</html>

