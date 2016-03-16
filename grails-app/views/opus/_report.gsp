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
                ng-show="reportCtrl.selectedReport && reportCtrl.reportData">{{reportCtrl.selectedReport.name}} <span
                    class="small">({{reportCtrl.reportData.recordCount}} row(s))</span>
            </h4>

            <div ng-show="reportCtrl.loading"><span class="fa fa-spin fa-spinner"></span>&nbsp;Loading...</div>

            <ng-include src="'mismatchedNamesReport.html'"
                        ng-if="reportCtrl.selectedReport.id == 'mismatchedNames'"></ng-include>
            <ng-include src="'draftProfilesReport.html'"
                        ng-if="reportCtrl.selectedReport.id == 'draftProfiles'"></ng-include>
            <ng-include src="'archivedProfilesReport.html'"
                        ng-if="reportCtrl.selectedReport.id == 'archivedProfiles'"></ng-include>
            <ng-include src="'recentChanges.html'"
                        ng-if="reportCtrl.selectedReport.id == 'recentChanges'"></ng-include>
            <ng-include src="'recentComments.html'"
                        ng-if="reportCtrl.selectedReport.id == 'recentComments'"></ng-include>
        </div>
    </div>

    <script type="text/ng-template" id="mismatchedNamesReport.html">
    <div class="table-responsive">
        <table class="table table-striped" ng-show="reportCtrl.reportData.records.length > 0">
            <thead>
            <tr>
                <th>Profile Name</th>
                <th>Matched Name</th>
                <th>In NSL?</th>
            </tr>
            </thead>
            <tbody>
            <tr ng-repeat="profile in reportCtrl.reportData.records">
                <td>
                    <a href="${request.contextPath}/opus/{{ reportCtrl.opusId }}/profile/{{ profile.profileName.scientificName }}"
                       target="_blank"><span data-ng-bind-html="profile.profileName | formatProfileName | sanitizeHtml"></span></a>
                </td>
                <td>
                    <span data-ng-bind-html="profile.matchedName | formatProfileName | default:'Not matched' | sanitizeHtml"></span>
                </td>
                <td>
                    <a href="${grailsApplication.config.nsl.name.url.prefix}{{profile.nslNameId}}"
                       ng-if="profile.nslNameId" title="Click to view the NSL name" target="_blank"><i
                            class="fa fa-check color--green"></i></a>
                    <i title="This name was not found in the NSL" class="fa fa-close color--red"
                       ng-if="!profile.nslNameId"></i>
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

    <script type="text/ng-template" id="archivedProfilesReport.html">
    <div class="table-responsive">
        <table class="table table-striped" ng-show="reportCtrl.reportData.records.length > 0">
            <thead>
            <tr><th>Profile</th><th>Date archived</th><th>Archived By</th></tr>
            </thead>
            <tbody>
            <tr ng-repeat="profile in reportCtrl.reportData.records">
                <td>
                    <a href="${request.contextPath}/opus/{{ reportCtrl.opusId }}/profile/{{ profile.profileId }}"
                       target="_blank" class="scientific-name">{{profile.scientificName}}</a>
                </td>
                <td>
                    {{ profile.archivedDate | date:'dd/MM/yyyy h:mm a' }}
                </td>
                <td>
                    {{ profile.archivedBy }}
                </td>
            </tr>
            </tbody>
        </table>
    </div>
    </script>

    <script type="text/ng-template" id="recentChanges.html">
    <div class="">
        <label class="control-label">Show updates from:</label>

        <div class="btn-group" role="group" aria-label="List the most recent changes with the following options.">
            <button type="button" class="btn btn-default btn-sm" ng-repeat="period in reportCtrl.periods"
                    ng-click="reportCtrl.setPeriod(period)"
                    ng-class="{active: reportCtrl.selectedPeriod.id == period.id}">{{ period.name }}</button>
        </div>
    </div>

    <div class="margin-top-1" ng-show="reportCtrl.selectedPeriod.id == 'custom'">
        <div class="row">
            <div class="col-md-12">
                <div class="form-inline">
                    <div class="form-group">
                        <label class="control-label" for="inputFromDate">Find profiles updated between</label>

                        <fallback-date-picker field-id="inputFromDate" ng-required="true" ng-model="reportCtrl.dates.from" format="dd-MMMM-yyyy" fallback-options="reportCtrl.datePickerOptions" size="small" ></fallback-date-picker>
                    </div>

                    <div class="form-group">
                        <label class="control-label" for="inputToDate">and</label>

                        <fallback-date-picker field-id="inputToDate" ng-required="true" ng-model="reportCtrl.dates.to" format="dd-MMMM-yyyy" fallback-options="reportCtrl.datePickerOptions" size="small" ></fallback-date-picker>
                    </div>

                    <div class="form-group">
                        <button type="button" class="btn btn-default btn-sm"
                                ng-disabled="!reportCtrl.checkFormValid()"
                                ng-click="reportCtrl.loadCustomDateReport()">
                            <i class="glyphicon glyphicon-search"></i> Run report
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="table-responsive">
        <table class="table table-striped" ng-show="reportCtrl.reportData.records.length > 0">
            <thead>
            <tr><th>Profile</th><th>Date modified</th><th>Editor</th></tr>
            </thead>
            <tbody>
            <tr ng-repeat="profile in reportCtrl.reportData.records">
                <td>
                    <a href="${request.contextPath}/opus/{{ reportCtrl.opusId }}/profile/{{ profile.scientificName }}"
                       target="_blank" class="scientific-name">{{profile.scientificName}}</a>
                </td>
                <td>
                    {{ profile.lastUpdated | date:'dd/MM/yyyy h:mm a' }}
                </td>
                <td>
                    {{ profile.editor | default:'Unknown' }}
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

    <script type="text/ng-template" id="recentComments.html">
    <div class="">
        <label class="control-label">Show comments from:</label>

        <div class="btn-group" role="group" aria-label="List the most recent comments with the following options.">
            <button type="button" class="btn btn-default btn-sm" ng-repeat="period in reportCtrl.periods"
                    ng-click="reportCtrl.setPeriod(period)"
                    ng-class="{active: reportCtrl.selectedPeriod.id == period.id}">{{ period.name }}</button>
        </div>
    </div>

    <div class="margin-top-1" ng-show="reportCtrl.selectedPeriod.id == 'custom'">
        <div class="row">
            <div class="col-md-12">
                <div class="form-inline">
                    <div class="form-group">
                        <label class="control-label" for="inputFromDate">Find comments made between</label>

                        <fallback-date-picker field-id="inputFromDate" ng-required="true" ng-model="reportCtrl.dates.from" format="dd-MMMM-yyyy" fallback-options="reportCtrl.datePickerOptions" size="small" ></fallback-date-picker>
                    </div>

                    <div class="form-group">
                        <label class="control-label" for="inputToDate">and</label>

                        <fallback-date-picker field-id="inputToDate" ng-required="true" ng-model="reportCtrl.dates.to" format="dd-MMMM-yyyy" fallback-options="reportCtrl.datePickerOptions" size="small" ></fallback-date-picker>
                    </div>

                    <div class="form-group">
                        <button type="button" class="btn btn-default btn-sm"
                                ng-disabled="!reportCtrl.checkFormValid()"
                                ng-click="reportCtrl.loadCustomDateReport()">
                            <i class="glyphicon glyphicon-search"></i> Run report
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="table-responsive">
        <table class="table table-striped" ng-show="reportCtrl.reportData.records.length > 0">
            <thead>
            <tr><th style="width:40%">Comment</th><th>Scientific Name</th><th>Date</th><th>Author</th></tr>
            </thead>
            <tbody>
            <tr ng-repeat="record in reportCtrl.reportData.records">
                %{--<td ng-bind-html="record.comment"></td>--}%
                <td>{{ record.comment | plainText | words:20:ignoreSpaces }}</td>
                <td>
                    <a href="${request.contextPath}/opus/{{ reportCtrl.opusId }}/profile/{{ record.scientificName }}"
                       target="_blank" class="scientific-name">{{ record.scientificName }}</a>
                </td>
                <td>
                    {{ record.lastUpdated | date:'dd/MM/yyyy h:mm a' }}
                </td>
                <td>
                    {{ record.editor | default:'Unknown' }}
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

</div>
