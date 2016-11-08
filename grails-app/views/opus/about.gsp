<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${pageTitle}</title>
</head>

<body>

<div class="container" ng-controller="AboutController as aboutCtrl" ng-cloak>
    <ol class="breadcrumb" ng-cloak>
        <li><a class="font-xxsmall" href="${request.contextPath}/">Profile Collections</a></li>
        <li><a class="font-xxsmall"
               href="${request.contextPath}/opus/{{aboutCtrl.opusId}}">${opusTitle}</a>
        </li>
        <li ng-show="aboutCtrl.fromProfile"><a class="font-xxsmall"
               href="${request.contextPath}/opus/{{aboutCtrl.opusId}}/profile/{{aboutCtrl.fromProfile}}">{{aboutCtrl.fromProfile}}</a>
        </li>
        <li class="font-xxsmall active">About</li>
    </ol>
    <div class="margin-bottom-2"></div>
    <h2 class="heading-large">${pageTitle}</h2>
    <div class="row">
        <div class="col-md-12 col-xs-12 col-lg-12">
            <div data-ng-bind-html="aboutCtrl.aboutHtml | sanitizeHtml"></div>
        </div>
        <div class="col-md-12 col-xs-12 col-lg-12 padding-top-1" ng-show="aboutCtrl.hasCitation()">
            <h4>Citations</h4>
            <p>This collection should be cited as:</p>
            <div data-ng-bind-html="aboutCtrl.citationHtml | sanitizeHtml" class="padding-left-1"></div>
        </div>
        <div class="col-md-12 col-xs-12 col-lg-12" ng-show="aboutCtrl.hasCitation()">
            <p>The taxon profiles in this collection should be cited as per the following example:</p>
            <div class="citation-example">
                <p class="padding-left-1">Conn, B.J. ({{aboutCtrl.citationYear}}) Loganiaceae. In: <span data-ng-bind-html="aboutCtrl.citationHtml | sanitizeHtml"></span>. <a href="{{aboutCtrl.citationUrl}}">{{aboutCtrl.citationUrl}}</a>. {{aboutCtrl.citationDate}}</p>
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

        <div class="col-xs-12">
            <h4>Collection Statistics</h4>
            <div class="main-stats" ng-cloak ng-controller="StatisticsController as statsCtrl">
                <div class="main-stats__stat col-lg-2 col-md-2 col-sm-4 col-xs-6"
                     ng-repeat="stat in statsCtrl.statistics">
                    <h4 class="stat__title heading-underlined">{{stat.name}}
                        <span class="small fa fa-info-circle color--medium-blue" title="{{stat.caveat}}" ng-show="stat.caveat"></span>
                    </h4>
                    <div class="stat__number" title="{{stat.tooltip}}">{{stat.value}}</div>
                </div>
            </div>
        </div>

        <div class="col-md-12 col-xs-12 col-lg-12 padding-top-1" ng-cloak>
            <a name="copyright"></a>

            <h4>Copyright</h4>

            <p ng-show="aboutCtrl.collectionCopyright">&copy; {{ aboutCtrl.collectionCopyright }}</p>

            <div data-ng-bind-html="aboutCtrl.genericCopyrightHtml | sanitizeHtml"></div>
        </div>
    </div>
</div>

</body>
</html>

