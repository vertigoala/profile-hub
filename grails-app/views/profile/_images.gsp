<div class="panel panel-default" ng-controller="ImagesController as imageCtrl" ng-init="imageCtrl.init('${edit}')">
    <div ng-if="imageCtrl.images.length > 0 && imageCtrl.readonly" ng-cloak>
        <a name="view_images"></a>

        <div class="panel-heading">
            <div class="row">
                <div class="col-sm-12">
                    <h4 class="section-panel-heading">Images</h4>
                </div>
            </div>
        </div>

        <div class="panel-body">
            <div class="row">
                <div class="col-sm-12">
                    <div ng-repeat="image in imageCtrl.images" class="col-md-6 col-sm-6 margin-bottom-2"
                         ng-show="!image.excluded">
                        <div class="imgCon ">
                            <a href="" ng-click="imageCtrl.showMetadata(image)" target="_blank" ng-if="image.thumbnailUrl" title="View details">
                                <img ng-src="{{image.thumbnailUrl}}" ng-if="image.thumbnailUrl && image.type.name == 'OPEN'"
                                     class="thumbnail"/>
                                <img ng-src="${request.contextPath}{{image.thumbnailUrl}}"
                                     ng-if="image.thumbnailUrl && image.type.name != 'OPEN'" class="thumbnail"/>
                            </a>

                            <p class="caption">{{ image.dataResourceName }}</p>

                            <p class="caption" ng-if="imageCtrl.imageCaption(image)">"{{ imageCtrl.imageCaption(image) }}"
                                <span class="caption"
                                      ng-if="image.metadata.creator">by {{ image.metadata.creator }}<span
                                        ng-if="image.metadata.dateCreated">, {{ image.metadata.dateCreated | date: 'dd/MM/yyyy' }}</span>
                                </span>
                                <span ng-if="image.metadata.rightsHolder">(&copy; {{ image.metadata.rightsHolder }})</span>
                            </p>

                            <a class="caption"
                               href="" ng-click="imageCtrl.showMetadata(image)">View image details</a>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div ng-form="ImageForm" ng-if="!imageCtrl.readonly" ng-cloak>
        <a name="edit_images"></a>

        <div class="panel-heading">
            <div class="row">
                <div class="col-sm-12">
                    <h4 class="section-panel-heading">Images</h4>
                </div>
            </div>
        </div>

        <div class="panel-body">
            <div class="col-sm-12">
                <div class="row section-no-para" ng-if="imageCtrl.images.length > 0">
                    <div class="col-sm-4">
                        <h5>Image</h5>
                    </div>

                    <div class="col-sm-2">
                        <h5>Display on public view</h5>
                    </div>

                    <div class="col-sm-2">
                        <h5>Use as the main image</h5>
                    </div>

                    <div class="col-sm-4">
                        <h5>Override caption</h5>
                    </div>

                    <div class="clearfix"></div>

                    <div ng-repeat="image in imageCtrl.images" class="row border-bottom margin-bottom-1">
                        <div class="col-sm-4">
                            <div class="imgCon">
                                <a href="" ng-click="imageCtrl.showMetadata(image)" target="_blank" ng-if="image.largeImageUrl" title="View details">
                                    <img ng-src="{{image.largeImageUrl}}" ng-if="image.largeImageUrl && image.type.name == 'OPEN'"
                                         class="thumbnail"/>
                                    <img ng-src="${request.contextPath}{{image.largeImageUrl}}"
                                         ng-if="image.largeImageUrl && image.type.name != 'OPEN'" class="thumbnail"/>
                                </a>
                                <span class="pill"
                                      ng-class="image.type.name == 'OPEN' ? 'pill-blue' : image.type.name == 'PRIVATE' ? 'pill-green' : 'pill-yellow'"
                                      title="This image is {{image.type.name == 'OPEN' ? 'available in the Atlas of Living Australia image library' : image.type.name == 'PRIVATE' ? 'only visible within this collection' : 'only visible in draft mode'}}">{{image.type.name}}</span>

                                <div class="meta inline-block">{{ image.dataResourceName }}</div>

                                <div>
                                    <button class="btn btn-link" ng-click="imageCtrl.deleteLocalImage(image.imageId, image.type.name)"
                                            ng-show="image.type.name != 'OPEN'"><i
                                            class="fa fa-trash-o color--red"></i> Delete image</button>

                                    <button class="btn btn-link" ng-click="imageCtrl.publishPrivateImage(image.imageId)"
                                            ng-show="image.type.name == 'PRIVATE'"><i
                                            class="fa fa-paper-plane color--green"></i> Push image to open repository</button>
                                </div>

                            </div>
                        </div>

                        <div class="col-sm-2">
                            <div class="small center">
                                <div class="btn-group">
                                    <label class="btn btn-xs" ng-class="image.displayOption == 'INCLUDE' ? 'btn-success' : 'btn-default'"
                                           ng-model="image.displayOption" btn-radio="'INCLUDE'"
                                           ng-change="imageCtrl.changeImageDisplay(ImageForm)">Yes</label>
                                    <label class="btn btn-xs" ng-class="image.displayOption == 'EXCLUDE' ? 'btn-danger' : 'btn-default'"
                                           ng-model="image.displayOption" btn-radio="'EXCLUDE'"
                                           ng-change="imageCtrl.changeImageDisplay(ImageForm)">No</label>
                                </div>
                            </div>
                        </div>

                        <div class="col-sm-2">
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

                        <div class="col-sm-4">
                            <div class="form-group">
                                <label class="sr-only" for="{{image.imageId}}-caption">Caption</label>
                                <input type="text" class="form-control" id="{{image.imageId}}-caption" ng-model="image.caption" placeholder="Alternative caption">
                            </div>
                        </div>
                    </div>

                    <div class="small margin-top-1 well" ng-show="!imageCtrl.readonly">
                        <i class="fa fa-info-circle color--medium-blue margin-bottom-1"></i>

                        <p>
                            When your profile is locked for major revision, images will only be uploaded to a temporary location. This is referred to as 'staging'. If your profile is not locked for major revision, images will be published immediately.
                        </p>

                        <p>
                            Only staged <span ng-show="imageCtrl.opus.keepImagesPrivate">or private</span> images can be deleted, as published images are stored in the central Atlas of Living Australia image repository and are accessible by other systems.
                        </p>
                    </div>
                </div>
            </div>
        </div>

        <div class="panel-footer" ng-show="!imageCtrl.readonly">
            <div class="row">
                <div class="col-md-12">
                    <g:if test="${grailsApplication.config.feature.imageUpload == 'true'}">
                        <button class="btn btn-default" ng-click="imageCtrl.uploadImage()"><i
                                class="fa fa-plus"></i>&nbsp;Add Image</button>
                    </g:if>
                    <g:elseif test="${grailsApplication.config.deployment_env.toLowerCase() != 'prod' && grailsApplication.config.deployment_env.toLowerCase() != 'production'}">
                        <span class="small">Image uploads are not available in ${grailsApplication.config.deployment_env}</span>
                    </g:elseif>

                    <save-button ng-click="imageCtrl.saveProfile(ImageForm)" form="ImageForm"></save-button>
                </div>
            </div>
        </div>

        <script type="text/ng-template" id="imageUpload.html">

        </script>
    </div>
</div>
