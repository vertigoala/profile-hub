<div ng-controller="ImagesController as imageCtrl" ng-init="imageCtrl.init('${edit}')" ng-cloak>
    <div class="bs-docs-example" id="viewImages" data-content="Images"
         ng-show="imageCtrl.images.length > 0 && imageCtrl.readonly">
        <div ng-repeat="image in imageCtrl.images" class="imgCon" ng-if="!image.excluded">
            <a href="${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}{{image.occurrenceId}}"
               target="_self" ng-if="image.largeImageUrl">
                <img ng-src="{{image.largeImageUrl}}" ng-if="image.largeImageUrl"/>
            </a>

            <div class="meta">{{ image.dataResourceName }}</div>
        </div>
    </div>


    <div class="bs-docs-example" id="editImages" data-content="Images" ng-form="ImageForm"
         ng-show="imageCtrl.images.length > 0 && !imageCtrl.readonly">
        <div class="span6">
            <h5>Image</h5>
        </div>

        <div class="span2">
            <h5>Display on public view</h5>
        </div>

        <div class="span2">
            <h5>Use as the main image</h5>
        </div>

        <div ng-repeat="image in imageCtrl.images" class="row-fluid border-bottom">
            <div class="span6">
                <div class="imgCon">
                    <a href="${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}{{image.occurrenceId}}"
                       target="_self" ng-if="image.largeImageUrl">
                        <img ng-src="{{image.largeImageUrl}}" ng-if="image.largeImageUrl"/>
                    </a>

                    <div class="meta">{{ image.dataResourceName }}</div>
                </div>
            </div>

            <div class="span2">
                <div class="small center">
                    <div class="btn-group">
                        <label class="btn btn-mini" ng-class="image.excluded ? '' : 'btn-success'"
                               ng-model="image.excluded" btn-radio="false"
                               ng-change="imageCtrl.changeImageDisplay(ImageForm)">Yes</label>
                        <label class="btn btn-mini" ng-class="image.excluded ? 'btn-danger' : ''"
                               ng-model="image.excluded" btn-radio="true"
                               ng-change="imageCtrl.changeImageDisplay(ImageForm)">No</label>
                    </div>
                </div>
            </div>

            <div class="span2">
                <div class="small center">
                    <div class="btn-group">
                        <label class="btn btn-mini"
                               ng-class="image.primary ? 'btn-success' : ''"
                               ng-model="image.primary"
                               ng-click="imageCtrl.changePrimaryImage(image.imageId, ImageForm)" btn-radio="true">Yes</label>
                        <label class="btn btn-mini"
                               ng-class="image.primary ? '' : 'btn-danger'"
                               ng-model="image.primary"
                               ng-click="imageCtrl.changePrimaryImage(image.imageId, ImageForm)" btn-radio="false">No</label>
                    </div>
                </div>
            </div>
        </div>

        <button class="btn btn-primary" ng-click="imageCtrl.saveProfile(ImageForm)"><span
                ng-show="ImageForm.$dirty">*</span> Save</button>
    </div>

</div>