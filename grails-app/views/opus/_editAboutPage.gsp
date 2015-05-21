<div ng-controller="AboutController as aboutCtrl" class="well" ng-form="AboutForm" ng-cloak>

    <div class="row-fluid">
        <div text-angular text-angular-name="about" ng-model="aboutCtrl.aboutHtml" ta-toolbar="{{richTextToolbarFull}}"></div>
    </div>

    <button class="btn btn-primary" ng-click="aboutCtrl.saveAboutHtml(AboutForm)" >
        <span ng-show="AboutForm.$dirty">*</span> Save</span>
    </button>

</div>