<div class="row" ng-cloak>
    <div class="panel panel-default" ng-show="profileCtrl.showProfileAudit" ng-cloak>
        <div class="panel-body">
            <div ng-if="profileCtrl.loading"><span class="fa fa-spin fa-spinner"></span>&nbsp;Loading...</div>
            <div class="table-responsive" ng-if="!profileCtrl.loading">
                <table class="table table-striped">
                    <thead>
                    <th>Date updated</th>
                    <th>Editor</th>
                    <th></th>
                    </thead>
                    <tbody>
                    <tr ng-repeat="audit in profileCtrl.audit">
                        <td>{{audit.date | date:'dd/MM/yyyy h:mm a'}}</td>
                        <td>{{audit.userDisplayName | default:'Unknown'}}</td>
                        <td><button class="btn btn-link pull-right"
                                    ng-click="profileCtrl.showAuditComparison($index)"
                                    ng-show="!$last">Compare with previous revision</button></td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="panel-footer">
            <div class="row">
                <div class="col-md-12">
                    <button class="btn btn-default pull-right" ng-click="profileCtrl.toggleAudit()">Hide revision history</button>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/ng-template" id="auditComparisonPopup.html">
<div class="modal-header">
    <h4 class="modal-title">Comparison</h4>
</div>

<div class="modal-body">
    <profile-comparison left="compareCtrl.left.object"
                        left-title="{{compareCtrl.left.date | date:'dd/MM/yyyy h:mm a'}}"
                        right="compareCtrl.right.object"
                        right-title="{{compareCtrl.right.date | date:'dd/MM/yyyy h:mm a'}}"></profile-comparison>
</div>

<div class="modal-footer">
    <button class="btn btn-default" ng-click="compareCtrl.close()">Close</button>
</div>
</script>