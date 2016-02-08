<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <meta name="logoUrl" content="${logoUrl}"/>
    <title>Profile search | Atlas of Living Australia</title>

    <r:require module="profiles"/>
</head>

<body>
<div>
    <ol class="breadcrumb" ng-cloak>
        <li><a class="font-xxsmall" href="${request.contextPath}/">Profile Collections</a></li>
        <li class="font-xxsmall active">Profile Search</li>
    </ol>
</div>

<div class="margin-bottom-3">
    <div class="row">
        <div class="col-md-12">

            <g:include controller="opus" action="searchPanel" params="[opusId: params.opusId]"/>

        </div>
    </div>
</div>
</body>

</html>