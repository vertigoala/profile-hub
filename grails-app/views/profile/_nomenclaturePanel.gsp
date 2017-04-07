<div class="panel panel-default" ng-cloak ng-form="NomenclatureForm" ng-show="!profileCtrl.readonly()">
    <navigation-anchor anchor-name="edit_nomenclature" title="Nomenclature" condition="!profileCtrl.readonly()"></navigation-anchor>
    <div class="panel-heading">
        <div class="row">
            <div class="col-sm-12">
                <h4 class="section-panel-heading">Nomenclature</h4>
                <p:help help-id="profile.edit.nomenclature" show="${edit}"/>
            </div>
        </div>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-sm-12">
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
                    <save-button ng-click="profileCtrl.saveProfile(NomenclatureForm)" form="NomenclatureForm"></save-button>
                </div>
            </div>
        </div>
    </div>
</div>