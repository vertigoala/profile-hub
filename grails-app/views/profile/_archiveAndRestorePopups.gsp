<!-- template for the popup displayed when Archive Profile is selected -->
<script type="text/ng-template" id="archiveProfilePopup.html">
<div ng-form="ArchiveForm">
    <div class="modal-header">
        <h4 class="modal-title">Archive Profile</h4>
        <close-modal close="archiveCtrl.cancel()"></close-modal>
    </div>

    <div class="modal-body">
        <p>
            Archived profiles cannot be edited and will not appear in search results. Archiving is used to preserve an old representation of a treatment, such as when species treatments are merged.
        </p>
        <p>
            Provide a reason for archiving this profile. This reason will be displayed on the profile page.
        </p>

        <div class="row">
            <div class="form-horizontal">
                <div class="form-group">
                    <label for="archiveComment" class="col-sm-3 control-label">Reason:</label>

                    <div class="col-sm-8">
                        <textarea id="archiveComment" ng-model="archiveCtrl.archiveComment" rows="4"
                                  required="true" ng-required="true" class="form-control"></textarea>
                    </div>
                </div>
            </div>
        </div>

        <p>
            Archiving may take several minutes.
        </p>
    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" ng-click="archiveCtrl.ok()" ng-disabled="ArchiveForm.$invalid">OK</button>
        <button class="btn btn-default" ng-click="archiveCtrl.cancel()">Cancel</button>
    </div>
</div>
</script>


<!-- template for the popup displayed when Restore Profile is selected -->
<script type="text/ng-template" id="restoreProfilePopup.html">
<div ng-form="RestoreForm">
    <div class="modal-header">
        <h4 class="modal-title">Restore Profile</h4>
        <close-modal close="restoreCtrl.cancel()"></close-modal>
    </div>

    <div class="modal-body">
        <p>
            A profile already exists with this name. You will need to rename this profile before it can be restored.
        </p>

        <profile-name name="restoreCtrl.newName"
                      valid="restoreCtrl.nameIsValid"
                      current-profile-id="restoreCtrl.profileId"
                      manually-matched-guid="restoreCtrl.manuallyMatchedGuid"></profile-name>

    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" ng-click="restoreCtrl.ok()" ng-disabled="RestoreForm.$invalid || !restoreCtrl.nameIsValid">OK</button>
        <button class="btn btn-default" ng-click="restoreCtrl.cancel()">Cancel</button>
    </div>
</div>
</script>