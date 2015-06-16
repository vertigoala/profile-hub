<div class="panel panel-default" ng-controller="ImagesController as imageCtrl" ng-init="imageCtrl.init('${edit}')"
     ng-show="imageCtrl.images.length > 0 && imageCtrl.readonly" ng-cloak>
    <a name="view_images"></a>
    <div class="panel-body">
        <div class="col-sm-2"><strong>Images</strong></div>

        <div class="col-sm-10">
            <div class="row">
                <div ng-repeat="image in imageCtrl.images" class="col-md-6 col-sm-6" ng-if="!image.excluded">
                    <div class="imgCon">
                        <a href="${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}{{image.occurrenceId}}"
                           target="_self" ng-if="image.largeImageUrl">
                            <img ng-src="{{image.largeImageUrl}}" ng-if="image.largeImageUrl" class="thumbnail"/>
                        </a>

                        <p class="font-xxsmall"><strong>{{ image.dataResourceName }}</strong></p>
                    </div>
                </div>
            </div>
        </div>
    </div>

</div>

<div class="panel panel-default" ng-form="ImageForm" ng-controller="ImagesController as imageCtrl"
     ng-init="imageCtrl.init('${edit}')" ng-show="!imageCtrl.readonly" ng-cloak>
    <a name="edit_images"></a>
    <div class="panel-body">
        <div class="col-sm-2"><strong>Images</strong></div>

        <div class="col-sm-10">
            <div class="row" ng-if="imageCtrl.images.length > 0">
                <div class="col-sm-6">
                    <h5>Image</h5>
                </div>

                <div class="col-sm-3">
                    <h5>Display on public view</h5>
                </div>

                <div class="col-sm-3">
                    <h5>Use as the main image</h5>
                </div>

                <div ng-repeat="image in imageCtrl.images" class="row border-bottom">
                    <div class="col-sm-6">
                        <div class="imgCon">
                            <a href="${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}{{image.occurrenceId}}"
                               target="_self" ng-if="image.largeImageUrl">
                                <img ng-src="{{image.largeImageUrl}}" ng-if="image.largeImageUrl"/>
                            </a>

                            <div class="meta">{{ image.dataResourceName }}</div>
                        </div>
                    </div>

                    <div class="col-sm-3">
                        <div class="small center">
                            <div class="btn-group">
                                <label class="btn btn-xs" ng-class="image.excluded ? 'btn-default' : 'btn-success'"
                                       ng-model="image.excluded" btn-radio="false"
                                       ng-change="imageCtrl.changeImageDisplay(ImageForm)">Yes</label>
                                <label class="btn btn-xs" ng-class="image.excluded ? 'btn-danger' : 'btn-default'"
                                       ng-model="image.excluded" btn-radio="true"
                                       ng-change="imageCtrl.changeImageDisplay(ImageForm)">No</label>
                            </div>
                        </div>
                    </div>

                    <div class="col-sm-3">
                        <div class="small center">
                            <div class="btn-group">
                                <label class="btn btn-xs"
                                       ng-class="image.primary ? 'btn-success' : 'btn-default'"
                                       ng-model="image.primary"
                                       ng-click="imageCtrl.changePrimaryImage(image.imageId, ImageForm)"
                                       btn-radio="true">Yes</label>
                                <label class="btn btn-xs"
                                       ng-class="image.primary ? 'btn-default' : 'btn-danger'"
                                       ng-model="image.primary"
                                       ng-click="imageCtrl.changePrimaryImage(image.imageId, ImageForm)"
                                       btn-radio="false">No</label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>


    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <button class="btn btn-primary pull-right" ng-click="imageCtrl.saveProfile(ImageForm)"><span
                        ng-show="ImageForm.$dirty">*</span> Save</button>
            </div>
        </div>
    </div>
</div>
