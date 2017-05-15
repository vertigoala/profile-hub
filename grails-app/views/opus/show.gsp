<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile collections</title>
</head>

<body>

<div ng-controller="OpusController as opusCtrl" ng-init="opusCtrl.loadOpus()">
    <div class="row">
        <div class="col-sm-6">
            <!-- Breadcrumb -->
            <ol class="breadcrumb" ng-cloak>
                <li><a class="font-xxsmall" href="${request.contextPath}/">Profile Collections</a></li>
                <li><a class="font-xxsmall" href="${request.contextPath}/opus/{{opusCtrl.urlSuffix}}">{{opusCtrl.opus.title}}</a></li>
            </ol><!-- End Breadcrumb -->
        </div>
        <div class="col-sm-6">
            <h1 class="hidden">Welcome to the eFlora website</h1><!-- Show the H1 on each page -->

            <div class="pull-right">
                <g:if test="${params.isOpusEditor}">
                    <div class="btn-group" ng-controller="ProfileController as profileCtrl">
                        <button id="addProfile" class="btn btn-default"
                                ng-click="profileCtrl.createProfile(opusCtrl.opusId, false)">
                            <span class="fa fa-plus">&nbsp;</span>Add a new profile
                        </button>
                        <button class="btn btn-default dropdown-toggle" data-toggle="dropdown" target="_self">
                            <span class="caret"></span>
                        </button>
                        <ul class="dropdown-menu">
                            <li>
                                <a target="_self" id="duplicateProfile"
                                   ng-click="profileCtrl.createProfile(opusCtrl.opusId, true)">
                                    <span class="fa fa-copy">&nbsp;</span>Copy an existing profile
                                </a>
                            </li>
                        </ul>
                    </div>
                </g:if>
                <g:if test="${params.isOpusAdmin}">
                    <a href="${request.contextPath}/opus/{{opusCtrl.opusId}}/update" target="_self"
                       class="btn btn-default" ng-hide="!config.readonly"><i class="fa fa-edit"></i> Edit configuration
                    </a>
                </g:if>
            </div>
        </div>
    </div>

    <div class="row">
        <div class="col-sm-4">
            <div class="margin-bottom-1">
                <g:render template="explanatoryText"></g:render>
            </div>

            <g:render template="mainNavigationButtons"></g:render>
        </div>

        <div class="col-sm-8">
            <g:render template="imageSlider"></g:render>
            <div class="margin-top-2">
                <g:render template="updatesSection"></g:render>
            </div>
        </div>
    </div>
</div>

</body>

</html>