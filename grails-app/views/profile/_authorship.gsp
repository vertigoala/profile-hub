<div class="panel panel-default" ng-controller="ProfileController as profileCtrl" ng-init="profileCtrl.loadProfile()"
     ng-form="AuthorForm" ng-cloak ng-if="!profileCtrl.readonly()">
    <a name="{{profileCtrl.readonly() ? 'view_' : 'edit_'}}authorship"></a>
    <div class="panel-body">
        <div class="col-sm-2"><strong>Authors and Acknowledgements</strong></div>

        <div class="col-sm-10">

            <div class="row-fluid" ng-repeat="authorship in profileCtrl.profile.authorship"
                 ng-if="profileCtrl.readonly()">
                <h5>{{authorship.category}}</h5>
                {{authorship.text}}
            </div>

            <div class="row" ng-repeat="authorship in profileCtrl.profile.authorship"
                 ng-if="!profileCtrl.readonly()">
                <div class="form-group">
                    <label for="category">Type of contribution</label>
                    <input id="category" type="text" ng-model="authorship.category" class="form-control"
                           ng-disabled="authorship.category == 'Author'"/>
                </div>

                <div class="form-group">
                    <label for="text">Name</label>
                    <textarea id="text" ng-model="authorship.text" rows="3" class="form-control"></textarea>
                </div>

                <div class="row">
                <div class="col-sm-12">
                    <button class="btn btn-danger pull-right" ng-click="profileCtrl.deleteAuthorship($index, AuthorForm)"
                            ng-if="authorship.category != 'Author'" style="margin-bottom: 10px">Delete</button>
                </div>
                </div>
                <hr ng-if="!$last"/>
            </div>
        </div>
    </div>

    <div class="panel-footer" ng-if="!profileCtrl.readonly()">
        <div class="row">
            <div class="col-md-12">
                <button class="btn btn-default" ng-click="profileCtrl.addAuthorship(AuthorForm)"><i
                        class="fa fa-plus"></i> Add section</button>
                <button class="btn btn-primary pull-right" ng-click="profileCtrl.saveAuthorship(AuthorForm)"><span
                        ng-show="AuthorForm.$dirty">*</span> Save</button>
            </div>
        </div>
    </div>
</div>