<%--
 Created by Temi Varghese on 14/07/15.
--%>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Publication</title>

    <r:require module="profiles"/>
    <style>
    .main-panel {
        min-height: 400px
    }
    </style>
</head>

<body>
<div ng-controller="DoiController as doiCtrl" ng-init="doiCtrl.loadPublications()">

    <ol class="breadcrumb" ng-cloak>
        <li><a class="font-xxsmall" href="${request.contextPath}/">Profile Collections</a></li>
        <li><a class="font-xxsmall"
               href="${request.contextPath}/opus/{{doiCtrl.profile.opusId}}">{{doiCtrl.profile.opusName}}</a>
        </li>
        <li class="font-xxsmall active">{{doiCtrl.profile.scientificName}}</li>
    </ol>

    <div class="panel main-panel">
        <div class="panel-body col-sm-12">
            <div>
                <div class="h1" ng-show="doiCtrl.selectedPublication.title">
                    <a class="btn-link"
                       href="${request.contextPath}/opus/{{doiCtrl.opusId}}/profile/{{doiCtrl.profile.scientificName}}">{{doiCtrl.selectedPublication.title}}</a>
                    <span ng-show="doiCtrl.selectedPublication.version">&nbsp;({{doiCtrl.selectedPublication.version}})</span>
                </div>
            </div>

            <table class="table">
                <tr>
                    <td>

                        <p class="lead" ng-show="doiCtrl.selectedPublication.authors">
                            <strong>{{doiCtrl.selectedPublication.authors}}</strong><br/>
                            Published on {{doiCtrl.selectedPublication.publicationDate | date:"dd/MM/yyyy HH:mm"}} <br/>
                            DOI is  <a class=""
                                       href="{{doiCtrl.selectedPublication.doi}}"
                                       target="_blank">{{doiCtrl.selectedPublication.doi}}</a>
                        </p>

                        <ol class="list-inline margin-top-1">
                            <li><a class="btn-link"
                                   ng-href="${grailsApplication.config.profile.service.url}/opus/{{doiCtrl.opusId}}/profile/{{doiCtrl.uuid}}/publication/{{doiCtrl.selectedPublication.uuid}}/file"
                                   target="_blank">download</a></li>
                            <li ng-show="doiCtrl.selectedPublication.doi"><a class="btn-link"
                                                                             href="{{doiCtrl.selectedPublication.doi}}"
                                                                             target="_blank">doi</a></li>
                        </ol>

                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div class="" style="margin-top: 30px">
        <h4 class="h4">Other Available Versions</h4>
        <table class="table table-striped" ng-show="doiCtrl.publications.length > 0">
            <tr ng-repeat="pub in doiCtrl.publications">
                <td ng-class="{info: pub.uuid == doiCtrl.pubId}">
                    <publication title="pub.title" publication-date="pub.publicationDate"
                                 authors="pub.authors" uuid="pub.uuid" doi="pub.doi">

                    </publication>
                </td>
            </tr>
        </table>
    </div>
</div>
</body>
</html>