<g:if test="${profile.links && !edit}">
    <div class="bs-docs-example" id="browse_links" data-content="Links">
        <ul>
            <g:each in="${profile.links}" var="link">
                <li><a href="${link.url}">${link.title}</a>${link.description ? ' - ' + link.description : ''}
                </li>
            </g:each>
        </ul>
        <g:if test="${edit}">
            <a class="btn" href="javascript:alert('not implemented yet')"><i
                    class="icon icon-plus"></i> Add link</a>
        </g:if>
    </div>
</g:if>
<g:elseif test="${edit}">
    <div ng-controller="LinksEditor" class="bs-docs-example ng-cloak" id="browse_links" data-content="Links">
        <div style="margin-bottom: 10px;">
            <button class="btn" ng-click="saveLinks()">Save changes</button>
            <button class="btn" ng-click="addLink()"><i class="icon icon-plus"></i> Add new link
            </button>
        </div>
        <table class="table table-striped">
            <tr ng-repeat="link in links">
                <td>
                    <label>URL</label>
                    <input type="text" class="input-xxlarge" value="{{link.url}}"/><br/>
                    <label>Title</label>
                    <input type="text" class="input-xxlarge" value="{{link.title}}"/><br/>
                    <label>Description</label>
                    <textarea rows="3" class="input-xxlarge">{{link.description}}</textarea>
                </td>
                <td><button class="btn" ng-click="deleteLink($index)"><i
                        class="icon icon-minus"></i> Remove</button></td>
            </tr>
        </table>
    </div>
</g:elseif>