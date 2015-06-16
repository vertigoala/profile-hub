<div class="panel panel-default" ng-controller="BHLLinksEditor as bhlCtrl" ng-init="bhlCtrl.init('${edit}')" ng-cloak
     ng-show="!bhlCtrl.readonly || bhlCtrl.bhl.length > 0" ng-form="BhlForm">
    <a name="{{profileCtrl.readonly() ? 'view_' : 'edit_'}}bhllinks"></a>
    <div class="panel-body">
        <div class="col-sm-2"><strong>Biodiversity Heritage Library references</strong></div>

        <div class="col-sm-10" ng-if="!bhlCtrl.readonly">
            <p>
                Add links to the biodiversity heritage library. Links should be of the form:
                <b>http://biodiversitylibrary.org/page/29003916</b>
            </p>
        </div>

        <div class="col-sm-2" ng-if="!bhlCtrl.readonly"></div>

        <div class="col-sm-10">
            <div ng-repeat="link in bhlCtrl.bhl">
                <div class="col-sm-10">
                    <div ng-show="!bhlCtrl.readonly" class="form-group">
                        <label for="url">URL</label>
                        <input id="url" type="text" class="form-control" ng-model="link.url" value="{{link.url}}"
                               ng-change="bhlCtrl.updateThumbnail($index)"/>
                    </div>

                    <div>
                        <span ng-if="bhlCtrl.readonly && link.title != ''"><strong>Title:&nbsp;</strong>{{link.title}}</span>

                        <div ng-show="!bhlCtrl.readonly" class="form-group">
                            <label for="title">Title</label>
                            <input id="title" type="text" class="form-control" ng-model="link.title"
                                   value="{{link.title}}"/>
                        </div>
                    </div>

                    <div>
                        <span ng-if="bhlCtrl.readonly && link.description != ''"><strong>Description:&nbsp;</strong>{{link.description}}
                        </span>

                        <div ng-show="!bhlCtrl.readonly" class="form-group">
                            <label for="description">Description</label>
                            <textarea id="description" rows="3" class="form-control"
                                      ng-model="link.description">{{link.description}}</textarea>
                        </div>
                    </div>
                    <div ng-show="link.thumbnailUrl">
                        <div ng-show="link.fullTitle != ''">
                            <strong>BHL Title:&nbsp;</strong>{{link.fullTitle}}
                        </div>

                        <div ng-show="link.edition != ''">
                            <strong>Edition:&nbsp;</strong>{{link.edition}}
                        </div>

                        <div ng-show="link.publisherName != ''">
                            <strong>Publisher:&nbsp;</strong>{{link.publisherName}}
                        </div>

                        <div ng-show="link.bhlDoi != ''">
                            <strong>DOI:&nbsp;</strong><a href="http://dx.doi.org/{{link.bhlDoi}}">{{link.doi}}</a>
                        </div>
                    </div>
                </div>

                <div class="col-sm-2" ng-show="link.thumbnailUrl">
                    <a href="{{link.url}}" target="_blank">
                        <img ng-model="link.thumbnailUrl" ng-src="{{link.thumbnailUrl}}"
                             style="max-height:150px;" alt="{{link.title}}" class="img-rounded"/>
                    </a>
                </div>

                <div class="col-sm-12 padding-top-1">
                    <button class="btn btn-danger pull-right" ng-if="!bhlCtrl.readonly"
                            ng-click="bhlCtrl.deleteLink($index, BhlForm)">Delete</button>
                </div>
                <hr class="col-sm-10" ng-if="!$last"/>
            </div>
        </div>
    </div>

    <div class="panel-footer" ng-show="!bhlCtrl.readonly">
        <div class="row">
            <div class="col-md-12">
                <button class="btn btn-default" ng-click="bhlCtrl.addLink(BhlForm)"><i
                        class="fa fa-plus"></i> Add new reference
                </button>
                <button class="btn btn-primary pull-right" ng-click="bhlCtrl.saveLinks(BhlForm)">
                    <span id="saved"><span ng-show="BhlForm.$dirty">*</span> Save</span>
                </button>
            </div>
        </div>
    </div>
</div>