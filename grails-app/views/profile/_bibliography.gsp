<div ng-controller="ProfileController as profileCtrl" ng-cloak ng-form="BiblioForm" ng-init="profileCtrl.loadProfile()">
    <div class="bs-docs-example ng-cloak"
         data-content="Bibliography" ng-show="profileCtrl.profile.bibliography.length > 0 || !profileCtrl.readonly()"
         ng-cloak>
        <div class="row-fluid" ng-repeat="bibliography in profileCtrl.profile.bibliography">
            <div ta-bind ng-model="bibliography.text" ng-if="bibliography.uuid" class="inline-block"></div>

            <div text-angular text-angular-name="bibliography" ng-model="bibliography.text"
                 ta-toolbar="{{richTextToolbarSimple}}" class="single-line-editor input-xxxlarge inline-block"
                 ng-enter=""
                 ng-if="!bibliography.uuid"
                 ta-max-text="300"></div>

            <div ng-if="!profileCtrl.readonly()" class="inline-block">
                <button class="btn btn-link fa fa-trash-o"
                        ng-click="profileCtrl.deleteBibliography($index, BiblioForm)"
                        title="Delete this bibliography entry"></button>
                <button class="btn btn-link fa fa-arrow-down"
                        ng-if="!$last"
                        ng-click="profileCtrl.moveBibliographyDown($index, BiblioForm)"
                        title="Move this bibliography entry down"></button>
                <button class="btn btn-link fa fa-arrow-up" ng-if="!$first"
                        ng-click="profileCtrl.moveBibliographyUp($index, BiblioForm)"
                        title="Move this bibliography entry up"></button>
            </div>
        </div>


        <div class="row-fluid" ng-if="!profileCtrl.readonly()">
            <button class="btn btn-info" ng-click="profileCtrl.addBibliography(BiblioForm)"><i
                    class="icon icon-plus icon-white"></i>Add bibliography</button>
            <button class="btn btn-primary" ng-click="profileCtrl.saveProfile(BiblioForm)"><span
                    ng-show="BiblioForm.$dirty">*</span> Save</button>
        </div>
    </div>
</div>