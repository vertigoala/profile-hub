<div ng-controller="LinksEditor as linkCtrl" ng-init="linkCtrl.init('${edit}')">
    <div class="bs-docs-example ng-cloak" id="browse_links" data-content="Links" ng-show="!linkCtrl.readonly || linkCtrl.links.length > 0">
        <ul>
            <li ng-repeat="link in linkCtrl.links" ng-if="link.uuid"><a href="{{link.url}}">{{ link.title }}</a>
                <span ng-if="link.description">&nbsp-&nbsp</span>{{ link.description }}
                <a class="btn btn-mini btn-danger" ng-click="linkCtrl.deleteLink($index)" ng-show="!linkCtrl.readonly" title="Delete">
                    <i class="icon-minus icon-white"></i>
                </a>
            </li>
        </ul>


        <div style="margin-bottom: 10px;" ng-show="!linkCtrl.readonly">
            <button class="btn btn-primary" ng-click="linkCtrl.saveLinks()">Save changes</button>
            <button class="btn btn-info" ng-click="linkCtrl.addLink()"><i class="icon icon-plus"></i> Add new link
            </button>
        </div>
        <table class="table table-striped" ng-show="!linkCtrl.readonly">
            <tr ng-repeat="link in linkCtrl.links" ng-if="!link.uuid">
                <td>
                    <label>URL</label>
                    <input type="text" class="input-xxlarge" ng-model="link.url"/><br/>
                    <label>Title</label>
                    <input type="text" class="input-xxlarge" ng-model="link.title"/><br/>
                    <label>Description</label>
                    <textarea rows="3" class="input-xxlarge" ng-model="link.description"></textarea>
                </td>
                <td><button class="btn btn-danger" ng-click="linkCtrl.deleteLink($index)">Delete</button></td>
            </tr>
        </table>
    </div>
</div>