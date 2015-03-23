<div ng-controller="BHLLinksEditor as bhlCtrl" ng-init="bhlCtrl.init('${edit}')" class="bs-docs-example" id="browse_bhllinks"
     data-content="Biodiversity Heritage Library references" ng-show="!bhlCtrl.readonly || bhlCtrl.bhl.length > 0" ng-cloak>
    <div ng-show="bhlCtrl.readonly && bhlCtrl.bhl.length > 0">
        <table class="table">
            <tr ng-repeat="link in bhlCtrl.bhl" ng-if="link.uuid">
                <td>
                    <h4 ng-show="link.title != ''" style="margin-bottom: 0; padding-bottom:0;">
                        Title: {{link.title}}
                    </h4>
                    <span ng-show="link.description != ''">
                        Description: {{link.description}}
                    </span>
                    <cite ng-show="link.thumbnailUrl">
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
                    <div ng-show="link.thumbnailUrl">
                        <a href="{{link.url}}" target="_blank" ng-if="image.thumbnailUrl">
                            <img ng-model="link.thumbnailUrl" ng-src="{{link.thumbnailUrl}}"
                                 style="max-height:150px;" alt="{{link.title}}" class="img-rounded"/>
                        </a>
                    </div>
                </td>
            </tr>
        </table>
    </div>

    <div ng-show="!bhlCtrl.readonly">

        <p class="lead">
            Add links to the biodiversity heritage library. Links should be of the form:
            <b>http://biodiversitylibrary.org/page/29003916</b>
        </p>

        <div style="margin-bottom: 10px;" ng-show="!readonly">
            <button class="btn btn-primary" ng-click="bhlCtrl.saveLinks()">Save changes</button>
            <button class="btn btn-info" ng-click="bhlCtrl.addLink()"><i class="icon icon-plus"></i> Add new reference
            </button>
        </div>

        <table class="table table-striped">
            <tr ng-repeat="link in bhlCtrl.bhl">
                <td>
                    <label>URL</label>
                    <input type="text" class="input-xxlarge" ng-model="link.url" value="{{link.url}}"
                           ng-change="bhlCtrl.updateThumbnail($index)"/><br/>
                    <label>Title</label>
                    <input type="text" class="input-xxlarge" ng-model="link.title"
                           value="{{link.title}}"/><br/>
                    <label>Description</label>
                    <textarea rows="3" class="input-xxlarge"
                              ng-model="link.description">{{link.description}}</textarea>
                    <br/>
                    <cite ng-show="link.thumbnailUrl">
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
                    <button class="btn btn-danger" ng-click="bhlCtrl.deleteLink($index)">Delete</button>
                    <br/>

                    <div ng-show="link.thumbnailUrl">
                        <a href="{{link.url}}" target="_blank">
                            <img
                                    style="margin-top:20px; max-height:200px;"
                                    ng-src="{{link.thumbnailUrl}}"
                                    alt="{{link.title}}"
                                    class="img-rounded"/>
                        </a>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</div>