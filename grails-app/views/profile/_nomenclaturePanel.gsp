<div class="panel panel-default" ng-cloak ng-form="NomenclatureForm" ng-show="!profileCtrl.readonly()">
    <a name="{{profileCtrl.readonly() ? 'view_' : 'edit_'}}nomenclature"></a>
    <div class="panel-body">
        <div class="row">
            <div class="col-sm-2"><strong>Nomenclature</strong></div>

            <div class="col-sm-10">
                <nomenclature nsl-name-id="profileCtrl.profile.nslNameIdentifier"
                              nsl-nomenclature-id="profileCtrl.profile.nslNomenclatureIdentifier"
                              readonly="{{profileCtrl.readonly()}}"
                              save-function="profileCtrl.saveProfile"></nomenclature>
            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <div class="pull-right">
                    <button class="btn btn-primary" ng-disabled="NomenclatureForm.$invalid"
                            ng-click="profileCtrl.saveProfile(NomenclatureForm)">
                        <span ng-show="NomenclatureForm.$dirty">*</span> Save</button>
                </div>
            </div>
        </div>
    </div>
</div>


<div class="row padding-bottom-1" ng-if="profileCtrl.readonly()">
    <nomenclature nsl-name-id="profileCtrl.profile.nslNameIdentifier"
                  nsl-nomenclature-id="profileCtrl.profile.nslNomenclatureIdentifier"
                  readonly="{{profileCtrl.readonly()}}"
                  save-function="profileCtrl.saveProfile"></nomenclature>
</div>