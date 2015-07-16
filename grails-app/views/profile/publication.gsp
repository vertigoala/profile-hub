<%--
 Created by Temi Varghese on 14/07/15.
--%>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Publication</title>
    <style rel="stylesheet">
    .selected {
        background-color: white;
    }

    .spacing {
        min-height: 50px;
    }
    </style>

    <r:require module="profiles"/>

</head>

<body>
<div ng-controller="DoiController as doiCtrl" ng-init="doiCtrl.init(${publication})">

    <ol class="breadcrumb" ng-cloak>
        <li><a class="font-xxsmall" href="${request.contextPath}/">Profile Collections</a></li>
        <li><a class="font-xxsmall"
               href="${request.contextPath}/opus/{{doiCtrl.profile.opusId}}">{{doiCtrl.profile.opusName}}</a>
        </li>
        <li class="font-xxsmall active">{{doiCtrl.profile.scientificName}}</li>
    </ol>

    <div class="row">
        <div class="col-sm-12 selected padding-bottom-1">
            <h2 class="h1" ng-show="doiCtrl.selectedPublication.title">
                {{doiCtrl.selectedPublication.title}}
                <span ng-show="doiCtrl.selectedPublication.version">&nbsp;v. {{doiCtrl.selectedPublication.version}}</span>
            </h2>

            <div class="citation">{{doiCtrl.selectedPublication.authors}}, published on {{doiCtrl.selectedPublication.publicationDate | date:"dd/MM/yyyy HH:mm"}}</div>

            <p class="" ng-show="doiCtrl.selectedPublication.authors">
                DOI -  <a class=""
                          href="{{doiCtrl.selectedPublication.doi}}"
                          target="_blank">{{doiCtrl.selectedPublication.doi}}</a>
            </p>

            <ol class="list-inline margin-top-1">
                <li><a class="btn btn-primary"
                       ng-href="${grailsApplication.config.profile.service.url}/opus/{{doiCtrl.opusId}}/profile/{{doiCtrl.uuid}}/publication/{{doiCtrl.selectedPublication.uuid}}/file"
                       target="_blank"><span class="glyphicon glyphicon-download"></span> Download PDF</a></li>
                <li><a class="btn btn-default"
                       href="${request.contextPath}/opus/{{doiCtrl.opusId}}/profile/{{doiCtrl.profile.scientificName}}">View Profile</a>
                </li>
            </ol>
        </div>
    </div>

    <div class="row">
        <div class="spacing"></div>
    </div>

    <div class="row">
        <div class=" col-sm-12">
            <h4 class="h4">Other Available Versions</h4>
            <table class="table table-striped" ng-show="doiCtrl.publications.length > 0">
                <tr ng-repeat="pub in doiCtrl.publications">
                    <td ng-class="{selected: pub.uuid == doiCtrl.pubId}">
                        <publication title="pub.title" publication-date="pub.publicationDate"
                                     authors="pub.authors" uuid="pub.uuid" doi="pub.doi"
                                     version="pub.version" opus-id="doiCtrl.opusId" profile-id="doiCtrl.profileId"
                                     context="'${grailsApplication.config.profile.service.url}'">

                        </publication>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>
</body>
</html>