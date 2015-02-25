<div ng-controller="LinksEditor" ng-init="init('${edit}')">
    <div class="bs-docs-example ng-cloak" id="browse_links" data-content="Links" ng-show="links.length > 0">
        <ul>
            <li ng-repeat="link in links" ng-if="link.uuid"><a href="link.url">{{ link.title }}</a>
                <span ng-if="link.description">&nbsp-&nbsp</span>{{ link.description }}
                <a class="btn" ng-click="deleteLink($index)" ng-show="!readonly">
                    <i class="icon icon-minus"></i> Remove
                </a>
            </li>
        </ul>


        <div style="margin-bottom: 10px;" ng-show="!readonly">
            <button class="btn" ng-click="saveLinks()">Save changes</button>
            <button class="btn" ng-click="addLink()"><i class="icon icon-plus"></i> Add new link
            </button>
        </div>
        <table class="table table-striped" ng-show="!readonly">
            <tr ng-repeat="link in links" ng-if="!link.uuid">
                <td>
                    <label>URL</label>
                    <input type="text" class="input-xxlarge" ng-model="link.url"/><br/>
                    <label>Title</label>
                    <input type="text" class="input-xxlarge" ng-model="link.title"/><br/>
                    <label>Description</label>
                    <textarea rows="3" class="input-xxlarge" ng-model="link.description"></textarea>
                </td>
                <td><button class="btn" ng-click="deleteLink($index)"><i class="icon icon-minus"></i> Remove</button></td>
            </tr>
        </table>
    </div>
</div>