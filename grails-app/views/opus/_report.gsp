<div class="padding-top-1" ng-controller="ReportController as reportCtrl" ng-cloak>
    <div class="row" ng-cloak>
        <div class="col-md-3 margin-bottom-1 stay-on-screen">
            <ul class="nav nav-stacked" id="sidebar">
                <h4 class="font-xxsmall heading-underlined"><strong>Reports</strong></h4>
                <li ng-repeat="report in reportCtrl.reports | orderBy:'name'">
                    <a href="" class="font-xxsmall" ng-click="reportCtrl.loadReport(report.id, 0)">{{report.name}}</a>
                </li>
            </ul>
        </div>

        <div class="col-lg-9 col-md-8 col-xs-12" ng-cloak>
            <div ng-show="!reportCtrl.selectedReport">
                <p>
                    Select the report to display from the menu to the left
                </p>

                <p ng-if="reportCtrl.selectedReport && !reportCtrl.reportData.records">
                    There are no records in the {{reportCtrl.selectedReport.name}} report.
                </p>
            </div>
            <h4 class="heading-underlined"
                ng-show="reportCtrl.selectedReport && reportCtrl.reportData">{{reportCtrl.selectedReport.name}} <span class="small">({{reportCtrl.reportData.recordCount}} row(s))</span>
            </h4>

            <ng-include src="'mismatchedNamesReport.html'"
                        ng-if="reportCtrl.selectedReport.id == 'mismatchedNames'"></ng-include>
            <ng-include src="'draftProfilesReport.html'"
                        ng-if="reportCtrl.selectedReport.id == 'draftProfiles'"></ng-include>
        </div>
    </div>

    <script type="text/ng-template" id="mismatchedNamesReport.html">
    <div class="table-responsive">
        <table class="table table-striped" ng-show="reportCtrl.reportData.records.length > 0">
            <thead>
            <tr>
                <th>Profile Name</th>
                <th>Matched Name</th>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="profile in reportCtrl.reportData.records">
                <td>
                    <a href="${request.contextPath}/opus/{{ reportCtrl.opusId }}/profile/{{ profile.profileName.scientificName }}"
                       target="_blank"><span data-ng-bind-html="profile.profileName | formatProfileName"></span></a>
                </td>
                <td>
                    <span data-ng-bind-html="profile.matchedName | formatProfileName | default:'Not matched'"></span>
                </td>
            </tr>
            </tbody>
        </table>
        <pagination total-items="reportCtrl.reportData.recordCount"
                    ng-change="reportCtrl.loadReport(reportCtrl.selectedReport.id, (reportCtrl.page - 1) * reportCtrl.pageSize)"
                    ng-model="reportCtrl.page" max-size="10" class="pagination-sm"
                    items-per-page="reportCtrl.pageSize"
                    previous-text="Prev" boundary-links="true"
                    ng-show="reportCtrl.reportData.recordCount > reportCtrl.pageSize"></pagination>
    </div>
    </script>

    <script type="text/ng-template" id="draftProfilesReport.html">
    <div class="table-responsive">
        <table class="table table-striped" ng-show="reportCtrl.reportData.records.length > 0">
            <thead>
            <tr><th>Profile</th><th>Date draft created</th><th>Editor</th></tr>
            </thead>
            <tbody>
            <tr ng-repeat="profile in reportCtrl.reportData.records">
                <td>
                    <a href="${request.contextPath}/opus/{{ reportCtrl.opusId }}/profile/{{ profile.scientificName }}"
                       target="_blank" class="scientific-name">{{profile.scientificName}}</a>
                </td>
                <td>
                    {{ profile.draftDate | date:'dd/MM/yyyy h:mm a' }}
                </td>
                <td>
                    {{ profile.createdBy }}
                </td>
            </tr>
            </tbody>
        </table>
    </div>

    </script>
</div>