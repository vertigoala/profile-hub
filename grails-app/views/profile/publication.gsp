<%--
 Created by Temi Varghese on 14/07/15.
--%>

<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Publication</title>

    <r:require module="doi"/>

</head>

<body>
<div ng-controller="DoiController as doiCtrl" ng-init="doiCtrl.loadPublications()">
    <h1><a href="${request.contextPath}/opus/{{doiCtrl.opusId}}/profile/{{doiCtrl.scientificName}}">{{doiCtrl.scientificName}}</a></h1>
    <p>You can download snapshots of a profile from this page. <em>Selected Version</em> section highlights file selected.
        <em>Other Versions Available</em> section lists the snapshots available for download.
    </p>
    <div class="panel panel-default">
        <div class="panel-body">
            <div class="row">
                <div class="col-sm-2"><strong>Selected Version</strong></div>

                <div class="col-sm-10">
                    <div class="row">
                        <div class="col-md-10">
                            <strong ng-show="doiCtrl.selectedPublication.title != ''">
                                Title: {{doiCtrl.selectedPublication.title}}
                            </strong>

                            <div ng-show="doiCtrl.selectedPublication.publicationDate != ''">
                                <strong>Publication Date:&nbsp;</strong>{{doiCtrl.selectedPublication.publicationDate | date:"dd/MM/yyyy HH:mm"}}
                            </div>

                            <div ng-show="doiCtrl.selectedPublication.authors != ''">
                                <strong>Authors:&nbsp;</strong>{{doiCtrl.selectedPublication.authors}}
                            </div>

                            <div ng-show="doiCtrl.selectedPublication.doi">
                                <strong>Unique ID:&nbsp;</strong>{{doiCtrl.selectedPublication.uuid}}
                            </div>
                        </div>

                        <div class="col-md-2">
                            <a ng-href="${grailsApplication.config.profile.service.url}/opus/{{doiCtrl.opusId}}/profile/{{doiCtrl.uuid}}/publication/{{doiCtrl.selectedPublication.uuid}}/file"
                               target="_blank"><span class="glyphicon glyphicon-download-alt">&nbsp;Download</span></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="panel panel-default" ng-show="doiCtrl.publications.length > 0">
        <div class="panel-body">
            <div class="row">
                <div class="col-sm-2"><strong>Other Available Versions</strong></div>

                <div class="col-sm-10">
                    <div class="row  margin-bottom-1" ng-repeat="publication in doiCtrl.publications">
                        <div class="col-md-10">
                            <strong ng-show="publication.title != ''">
                                Title: {{publication.title}}
                            </strong>

                            <div ng-show="publication.publicationDate != ''">
                                <strong>Publication Date:&nbsp;</strong>{{publication.publicationDate | date:"dd/MM/yyyy HH:mm"}}
                            </div>

                            <div ng-show="doiCtrl.selectedPublication.authors != ''">
                                <strong>Authors:&nbsp;</strong>{{publication.authors}}
                            </div>

                            <div ng-show="doiCtrl.selectedPublication.doi">
                                <strong>Unique ID:&nbsp;</strong>{{publication.uuid}}
                            </div>
                        </div>

                        <div class="col-md-2">
                            <a ng-href="${grailsApplication.config.profile.service.url}/opus/{{doiCtrl.opusId}}/profile/{{doiCtrl.uuid}}/publication/{{publication.uuid}}/file"
                               target="_blank"><span class="glyphicon glyphicon-download-alt">&nbsp;Download</span></a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>