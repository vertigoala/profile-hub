<div class="panel panel-default" ng-controller="AboutController as aboutCtrl" ng-form="AboutForm" ng-cloak>
    <div class="panel-heading">
        <a name="about">
            <h4 class="section-panel-heading">About page content</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-sm-12">
                <p>
                    Enter the formatted content that you wish to be included in the 'about' page for your collection.
                </p>

                <div text-angular text-angular-name="about" ng-model="aboutCtrl.aboutHtml" ta-max-text="5000"></div>

                <div class="small">(Maximum of 5000 characters)</div>
            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <button class="btn btn-primary pull-right" ng-click="aboutCtrl.saveAboutHtml(AboutForm)"
                        ng-disabled="AboutForm.$invalid">
                    <span ng-show="AboutForm.$dirty">*</span> Save</span>
                </button>
            </div>
        </div>
    </div>
</div>