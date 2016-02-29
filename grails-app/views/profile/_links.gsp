<div class="panel panel-default" ng-controller="LinksEditor as linkCtrl" ng-init="linkCtrl.init('${edit}')" ng-cloak
     ng-show="!linkCtrl.readonly || linkCtrl.links.length > 0" ng-form="LinkForm">
    <a name="{{profileCtrl.readonly() ? 'view_' : 'edit_'}}links"></a>

    <div class="panel-heading">
        <div class="row">
            <div class="col-sm-12">
                <h4 class="section-panel-heading">Links</h4>
            </div>
        </div>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-sm-12">
                <ul>
                    <li ng-repeat="link in linkCtrl.links" ng-if="link.uuid"><a href="{{link.url}}">{{ link.title }}</a>
                        <span ng-if="link.description">&nbsp;-&nbsp;</span>{{ link.description }}
                        <a class="btn btn-link" ng-click="linkCtrl.deleteLink($index, LinkForm)"
                           ng-show="!linkCtrl.readonly" title="Delete">
                            <i class="fa fa-trash-o color--red"></i>
                        </a>
                    </li>
                </ul>

                <div ng-show="!linkCtrl.readonly">
                    <div class="col-sm-12" ng-repeat="link in linkCtrl.links" ng-if="!link.uuid">
                        <div class="form-group">
                            <label>URL</label>
                            <input type="text" class="form-control" ng-model="link.url"/><br/>
                        </div>

                        <div class="form-group">
                            <label>Title</label>
                            <input type="text" class="form-control" ng-model="link.title"/><br/>
                        </div>

                        <div class="form-group">
                            <label>Description</label>
                            <textarea rows="3" class="form-control" ng-model="link.description"></textarea>
                        </div>
                        <button class="btn btn-danger pull-right"
                                ng-click="linkCtrl.deleteLink($index, LinkForm)">Delete</button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="panel-footer" ng-show="!linkCtrl.readonly">
        <div class="row">
            <div class="col-md-12">
                <button class="btn btn-default" ng-click="linkCtrl.addLink(LinkForm)"><i
                        class="fa fa-plus"></i> Add new link
                </button>
                <button class="btn btn-primary pull-right" ng-click="linkCtrl.saveLinks(LinkForm)" ng-disabled="!LinkForm.$dirty">
                    <span id="saved"><span ng-show="LinkForm.$dirty">*</span> Save</span>
                </button>
            </div>
        </div>
    </div>
</div>