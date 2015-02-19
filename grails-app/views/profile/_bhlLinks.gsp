<g:if test="${edit}">
    <div ng-controller="BHLLinksEditor" class="bs-docs-example ng-cloak" id="browse_bhllinks"
         data-content="Biodiversity Heritage Library references">

        <p class="lead">
            Add links to the biodiversity heritage library. Links should be of the form:
            <b>http://biodiversitylibrary.org/page/29003916</b>
        </p>

        <div style="margin-bottom: 10px;">
            <button class="btn" ng-click="saveLinks()">Save changes</button>
            <button class="btn" ng-click="addLink()"><i class="icon icon-plus"></i> Add new reference
            </button>
        </div>

        <table class="table table-striped">
            <tr ng-repeat="link in bhl">
                <td>
                    <input type="hidden" name="link.uuid" value="{{link.uuid}}"/>
                    <label>URL</label>
                    <input type="text" class="input-xxlarge" ng-model="link.url" value="{{link.url}}"
                           ng-change="updateThumbnail($index)"/><br/>
                    <label>Title</label>
                    <input type="text" class="input-xxlarge" ng-model="link.title"
                           value="{{link.title}}"/><br/>
                    <label>Description</label>
                    <textarea rows="3" class="input-xxlarge"
                              ng-model="link.description">{{link.description}}</textarea>
                    <br/>
                    <cite ng-show="hasThumbnail($index)">
                        <span><b>BHL metadata</b></span><br/>
                        <span>
                            Title: {{link.fullTitle}}
                        </span>
                        <br/>
                        <span>
                            Edition: {{link.edition}}
                        </span>
                        <br/>
                        <span>
                            Publisher: {{link.publisherName}}
                        </span>
                        <br/>
                        <span>
                            DOI: <a href="http://dx.doi.org/{{link.bhlDoi}}">{{link.doi}}</a>
                        </span>
                    </cite>
                </td>
                <td>
                    <button class="btn" ng-click="deleteLink($index)"><i
                            class="icon icon-minus"></i> Remove</button>
                    <br/>

                    <div ng-show="hasThumbnail($index)">
                        <a href="{{link.url}}" target="_blank">
                            <img
                                    style="margin-top:20px; max-height:200px;"
                                    ng-src="{{link.thumbnail}}"
                                    alt="{{link.title}}"
                                    class="img-rounded"/>
                        </a>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</g:if>
<g:elseif test="${profile.bhl}">
    <div ng-controller="BHLLinksEditor" class="bs-docs-example ng-cloak" id="browse_bhllinks"
         data-content="Biodiversity Heritage Library references">
        <table class="table">
            <tr ng-repeat="link in bhl">
                <td>
                    <h4 ng-show="link.title != ''" style="margin-bottom: 0; padding-bottom:0;">
                        Title: {{link.title}}
                    </h4>
                    <span ng-show="link.description != ''">
                        Description: {{link.description}}
                    </span>
                    <cite ng-show="hasThumbnail($index)">
                        <span>
                            BHL title: {{link.fullTitle}}
                        </span>
                        <br/>
                        <span>
                            Edition: {{link.edition}}
                        </span>
                        <br/>
                        <span>
                            Publisher: {{link.publisherName}}
                        </span>
                        <br/>
                        <span>
                            DOI: <a href="http://dx.doi.org/{{link.bhlDoi}}">{{link.doi}}</a>
                        </span>
                    </cite>
                </td>
                <td>
                    <div ng-show="hasThumbnail($index)">
                        <a href="{{link.url}}" target="_blank">
                            <img ng-model="link.thumbnail" src="{{link.thumbnail}}"
                                 style="max-height:150px;" alt="{{link.title}}" class="img-rounded"/>
                        </a>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</g:elseif>