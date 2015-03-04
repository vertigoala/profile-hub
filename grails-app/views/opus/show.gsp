<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${opus.title} | Profile collections</title>
</head>

<body>

<div ng-app="profileEditor" ng-controller="OpusController as opusCtrl">

    <div class="row-fluid">

        <div class="span8">

            <div id="opusInfo" class="ng-cloak" ng-if="opusCtrl.opusDescription" ng-cloak style="margin-top:20px;">
                <p class="lead">
                    {{opusCtrl.opusDescription}}
                </p>
            </div>

            <g:include controller="opus" action="searchPanel"/>
        </div>

        <div class="span4">
            <g:include controller="opus" action="opusSummaryPanel"/>
        </div>
    </div>
</div>
</body>

</html>