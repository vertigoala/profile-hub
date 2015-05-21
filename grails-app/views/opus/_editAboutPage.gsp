<div ng-controller="AboutController as aboutCtrl" class="well" ng-form="AboutForm" ng-cloak>

    <h3>About page content</h3>
    <p>
        Enter the formatted content that you wish to be included in the 'about' page for your collection.
    </p>
    <div class="row-fluid">
        <div text-angular text-angular-name="about" ng-model="aboutCtrl.aboutHtml" ta-max-text="5000"></div>
    </div>
    <div class="small">(Maximum of 5000 characters)</div>

    <button class="btn btn-primary" ng-click="aboutCtrl.saveAboutHtml(AboutForm)" ng-disabled="AboutForm.$invalid">
        <span ng-show="AboutForm.$dirty">*</span> Save</span>
    </button>

</div>