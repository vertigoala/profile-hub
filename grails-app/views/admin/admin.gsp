<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile Admin</title>
</head>

<body>

<div>
    <g:if test="${flash.message}">
        <div class="alert alert-info">${flash.message}</div>
    </g:if>

    <div class="panel-heading">
        <h3>General admin</h3>
    </div>

    <div class="panel-body">
        <a href="${request.contextPath}/alaAdmin2" class="btn btn-primary">Go to ALA admin page</a>
    </div>

    <hr/>

    <div ng-controller="ALAAdminController as adminCtrl" ng-cloak>
        <div class="panel-heading">
            <h3>Search index</h3>
        </div>

        <div class="panel-body">
            <p>This lets you rebuild the Elastic Search index used for the free-text search feature</p>
            <button class="btn btn-primary" ng-click="adminCtrl.reindex()">Rebuild search index</button>
        </div>
        <hr/>

        <div class="panel-heading">
            <h3>Rematch names</h3>
        </div>

        <div class="panel-body">
            <p>This lets you rematch all taxa names for all profiles in selected collections</p>
            <dualmultiselect options="adminCtrl.collectionMultiSelectOptions"></dualmultiselect>

            <button class="btn btn-primary" ng-click="adminCtrl.rematchNames()">Rematch profile names</button>
        </div>
        <hr/>

        <div class="panel-heading">
            <h3>Pending Jobs</h3>
        </div>

        <div class="panel-body">
            <p>This lets you view and delete any pending async jobs</p>

            <div ng-show="adminCtrl.loadingPendingJobs"><span class="fa fa-spin fa-spinner">&nbsp;</span>Loading...</div>
            <div ng-show="!adminCtrl.loadingPendingJobs">
                <div ng-show="adminCtrl.jobs.length == 0">There are no pending jobs.</div>
                <div class="table-responsive" ng-show="adminCtrl.jobs.length > 0">
                    <table class="table table-striped">
                        <thead>
                        <th>Job Id</th>
                        <th>User</th>
                        <th>Job Type</th>
                        <th>Attempts</th>
                        <th>Created</th>
                        <th></th>
                        </thead>
                        <tbody>
                        <tr ng-repeat="job in adminCtrl.jobs">
                            <td>{{ job.jobId }}</td>
                            <td>{{ job.userEmail }}</td>
                            <td>{{ job.jobType.name }}</td>
                            <td>{{ job.attempts }} <span ng-show="job.error">Last error: {{ job.error }}, {{ job.lastUpdated }}</span></td>
                            <td>{{ job.dateCreated }}</td>
                            <td>
                                <button class="btn btn-sm btn-link" ng-click="adminCtrl.deleteJob(job.jobType.name, job.jobId)"><span class="fa fa-trash-o color--red"></span></button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <hr/>
    </div>
</div>
</body>
</html>




