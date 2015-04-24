<div ng-controller="ProfileController as profileCtrl" ng-init="profileCtrl.loadProfile()" ng-form="AuthorForm" ng-cloak>
    <div class="bs-docs-example" data-content="Authors and Acknowledgements">
        <div class="row-fluid" ng-repeat="authorship in profileCtrl.profile.authorship" ng-if="profileCtrl.readonly()">
            <h4>{{authorship.category}}</h4>
            {{authorship.text}}
        </div>

        <div class="row-fluid" ng-repeat="authorship in profileCtrl.profile.authorship" ng-if="!profileCtrl.readonly()">
            <label for="category">Type of contribution:</label>
            <input id="category" type="text" ng-model="authorship.category" ng-disabled="authorship.category == 'Author'"/>

            <button class="btn btn-danger pull-right" ng-click="profileCtrl.deleteAuthorship($index, AuthorForm)" ng-if="authorship.category != 'Author'" style="margin-bottom: 10px">Delete</button>

            <label for="text">Name:</label>
            <textarea id="text" ng-model="authorship.text" rows="3" class="input-fullwidth"></textarea>

            <hr ng-if="!$last"/>
        </div>

        <div class="row-fluid" ng-if="!profileCtrl.readonly()">
            <button class="btn btn-info" ng-click="profileCtrl.addAuthorship(AuthorForm)"><i
                    class="icon icon-plus icon-white"></i> Add section</button>
            <button class="btn btn-primary" ng-click="profileCtrl.saveAuthorship(AuthorForm)"><span
                    ng-show="AuthorForm.$dirty">*</span> Save</button>
        </div>
    </div>
</div>