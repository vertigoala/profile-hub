<div class="panel panel-default" ng-controller="MultimediaController as mmCtrl"
     ng-init="mmCtrl.init('${model}', '${edit}')" >
    <div ng-show="mmCtrl.documents.length > 0 || !mmCtrl.readonly" ng-cloak>
        <navigation-anchor anchor-name="view_multimedia" title="Multimedia"
                           condition="mmCtrl.documents.length > 0 && mmCtrl.readonly" ></navigation-anchor>
        <navigation-anchor anchor-name="edit_multimedia" title="Multimedia"
                           condition="!mmCtrl.readonly" ></navigation-anchor>
        <div class="panel-heading">
            <div class="row">
                <div class="col-sm-12">
                    <h4 class="section-panel-heading">Multimedia</h4>
                </div>
            </div>
        </div>

        <div id="multimediaPanelBody" class="panel-body">
            <div class="row">
                <div class="col-sm-6">
                    <table class="table">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Service</th>
                            <th></th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr ng-repeat="doc in mmCtrl.documents track by doc.uuid">
                            <td ng-bind="doc.name"></td>
                            <td ng-bind="doc.service"></td>
                            <td>
                                <button class="btn btn-sm" aria-label="Show" ng-click="mmCtrl.play(doc)"><i class="fa fa-show"></i></button>
                                <button ng-if="!mmCtrl.readonly" class="btn btn-sm" aria-label="Edit" ng-click="mmCtrl.edit(doc)"><i class="fa fa-edit"></i></button>
                                <button ng-if="!mmCtrl.readonly" class="btn btn-sm btn-danger" aria-label="Delete" ng-click="mmCtrl.delete(doc)"><i class="fa fa-trash"></i></button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </div>
                <div class="col-sm-6">
                    <div class="well">
                        <span ng-show="!mmCtrl.selectedMultimedia">
                            No multimedia selected
                        </span>
                        <span ng-show="mmCtrl.selectedMultimedia && !mmCtrl.selectedEmbed">
                            <i aria-label="Loading..." class="fa fa-cog fa-spin fa-4x"></i>
                        </span>
                        <span ng-show="mmCtrl.selectedMultimedia && mmCtrl.selectedEmbed" ng-bind-html="mmCtrl.selectedEmbed.html"></span>
                    </div>
                </div>
                <div class="col-sm-12" ng-if="!mmCtrl.readonly">
                    <button class="btn btn-primary" ng-click="mmCtrl.add()"><i class="fa fa-plus"></i> Add multimedia</button>
                </div>
            </div>
        </div>
    </div>
</div>