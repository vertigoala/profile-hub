<div class="panel panel-default" ng-show="profileCtrl.showNameEditControls" ng-cloak>
    <div class="panel-body">
        <div class="row">
            <div class="col-md-12">
                <p>
                    Current matched name: <span data-ng-bind-html="profileCtrl.profile.matchedName.formattedName | default: 'Not matched'"></span>
                </p>
            </div>
        </div>

        <div class="row">
            <div class="col-md-12">
                <profile-name name="profileCtrl.newName"
                              valid="profileCtrl.nameIsValid"
                              current-profile-id="profileCtrl.profile.uuid"
                              manually-matched-guid="profileCtrl.manuallyMatchedGuid"></profile-name>
            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <div class="pull-right">
                    <button class="btn btn-default" ng-show="profileCtrl.profile.matchedName" ng-click="profileCtrl.clearNameMatch()">Remove matched name</button>
                    <button class="btn btn-primary" ng-disabled="!profileCtrl.nameIsValid" ng-click="profileCtrl.saveNameChange()">Update name</button>
                </div>
            </div>
        </div>
    </div>
</div>