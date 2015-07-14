<div class="panel panel-default" ng-controller="ImagesController as imageCtrl" ng-init="imageCtrl.init('${edit}')">
    <div ng-if="imageCtrl.images.length > 0 && imageCtrl.readonly" ng-cloak>
        <a name="view_images"></a>

        <div class="panel-body">
            <div class="row">
                <div class="col-sm-2"><strong>Images</strong></div>

                <div class="col-sm-10">
                    <div class="row">
                        <div ng-repeat="image in imageCtrl.images" class="col-md-6 col-sm-6 padding-bottom-1"
                             ng-show="!image.excluded">
                            <div class="imgCon ">
                                <a href="${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}{{image.occurrenceId}}"
                                   target="_blank" ng-if="image.thumbnailUrl" title="View occurrence record">
                                    <img ng-src="{{image.largeImageUrl}}" ng-if="image.thumbnailUrl && !image.staged"
                                         class="thumbnail"/>
                                    <img ng-src="${request.contextPath}{{image.largeImageUrl}}"
                                         ng-if="image.thumbnailUrl && image.staged" class="thumbnail"/>
                                </a>

                                <p class="caption">{{ image.dataResourceName }}</p>

                                <p class="caption" ng-if="image.metadata.title">"{{ image.metadata.title }}"
                                    <span class="caption" ng-if="image.metadata.creator">by {{ image.metadata.creator }}<span ng-if="image.metadata.dateCreated">, {{ image.metadata.dateCreated | date: 'dd/MM/yyyy' }}</span>
                                    </span>
                                    <span ng-if="image.metadata.rightsHolder">(&copy; {{ image.metadata.rightsHolder }})</span>
                                </p>


                                <a class="caption"
                                   href="${grailsApplication.config.images.service.url}/image/details?imageId={{image.imageId}}"
                                   target="_blank" ng-if="!image.staged">View image details</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div ng-form="ImageForm" ng-if="!imageCtrl.readonly" ng-cloak>
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
                                   target="_blank" ng-if="image.thumbnailUrl">
                                    <img ng-src="{{image.largeImageUrl}}" ng-if="image.thumbnailUrl && !image.staged"
                                         class="thumbnail"/>
                                    <img ng-src="${request.contextPath}{{image.largeImageUrl}}"
                                         ng-if="image.thumbnailUrl && image.staged" class="thumbnail"/>
                                </a>

                                <div class="meta">{{ image.dataResourceName }}</div>
                                <button class="btn btn-link" ng-click="imageCtrl.deleteStagedImage(image.imageId)"
                                        ng-show="image.staged"><i
                                        class="fa fa-trash-o color--red"></i> Delete image</button>
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

                    <div class="small margin-top-1 well" ng-show="!imageCtrl.readonly">
                        <i class="fa fa-info-circle color--medium-blue margin-bottom-1"></i>

                        <p>
                            When your profile is locked for major revision, images will only be uploaded to a temporary location. This is referred to as 'staging'. If your profile is not locked for major revision, images will be published immediately.
                        </p>

                        <p>
                            Only staged images can be deleted, as published images are stored in the central Atlas of Living Australia image repository and are accessible by other systems.
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
                    <button class="btn btn-primary pull-right" ng-click="imageCtrl.saveProfile(ImageForm)"><span
                            ng-show="ImageForm.$dirty">*</span> Save</button>
                </div>
            </div>
        </div>

        <script type="text/ng-template" id="imageUpload.html">
        <div ng-form="UploadForm">
            <div class="modal-header">
                <h4 class="modal-title">Upload image</h4>
            </div>

            <div class="modal-body">
                <alert class="alert-danger" ng-show="imageUploadCtrl.error">{{ imageUploadCtrl.error }}</alert>

                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="file" class="col-sm-3 control-label">Image *</label>

                        <div class="col-sm-9">
                            <input id="file" type="file" ngf-select="" ng-model="imageUploadCtrl.files" name="file"
                                   accept="image/*" alt="preview" title="Image preview" required="true"
                                   ng-required="true">
                        </div>
                    </div>

                    <div class="form-group" ng-show="imageUploadCtrl.files[0] != null">
                        <label class="col-sm-3 control-label">Preview</label>

                        <div class="col-sm-9">
                            <div class="upload-image-preview">
                                <img ngf-src="imageUploadCtrl.files[0]" class="thumbnail">
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="title" class="col-sm-3 control-label">Title *</label>

                        <div class="col-sm-9">
                            <input id="title" type="text" ng-model="imageUploadCtrl.metadata.title" class="form-control"
                                   required ng-required="true"/>
                        </div>
                    </div>
                </div>

                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="dateCreated" class="col-sm-3 control-label">Date Taken</label>

                        <div class="col-sm-9">
                            <input id="dateCreated" type="date" ng-model="imageUploadCtrl.metadata.dateCreated"
                                   class="form-control"/>
                        </div>
                    </div>
                </div>

                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="description" class="col-sm-3 control-label">Description</label>

                        <div class="col-sm-9">
                            <textarea id="description" ng-model="imageUploadCtrl.metadata.description"
                                      class="form-control" rows="3" maxlength="500" ng-maxlength="500"></textarea>
                        </div>
                    </div>
                </div>

                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="owner" class="col-sm-3 control-label" required>Owner</label>

                        <div class="col-sm-9">
                            <input id="owner" type="text" ng-model="imageUploadCtrl.metadata.creator"
                                   class="form-control"
                                   placeholder="Photographer/illustrator"/>
                        </div>
                    </div>
                </div>

                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="rights" class="col-sm-3 control-label" required>Rights</label>

                        <div class="col-sm-9">
                            <input id="rights" type="text" ng-model="imageUploadCtrl.metadata.rights"
                                   class="form-control"
                                   placeholder="e.g. All rights reserved"/>
                        </div>
                    </div>
                </div>

                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="rightsHolder" class="col-sm-3 control-label" required>Rights Holder</label>

                        <div class="col-sm-9">
                            <input id="rightsHolder" type="text" ng-model="imageUploadCtrl.metadata.rightsHolder"
                                   class="form-control" placeholder="e.g. {{imageUploadCtrl.opus.title}}"/>
                        </div>
                    </div>
                </div>

                <div class="form-horizontal">
                    <div class="form-group">
                        <label for="licence" class="col-sm-3 control-label" required>Licence *</label>

                        <div class="col-sm-9">
                            <select id="licence"
                                    ng-options="licence.name for licence in imageUploadCtrl.licences | orderBy:'name'"
                                    ng-model="imageUploadCtrl.metadata.licence" class="form-control" ng-required="true">
                            </select>
                        </div>
                    </div>
                </div>

                <div class="col-md-12">
                    <p class="small pull-right">
                        Fields marked with an asterisk (*) are mandatory.
                    </p>
                </div>
            </div>

            <div class="modal-footer">
                <button class="btn btn-primary" ng-click="imageUploadCtrl.ok()"
                        ng-disabled="UploadForm.$invalid">OK</button>
                <button class="btn btn-default" ng-click="imageUploadCtrl.cancel()">Cancel</button>
            </div>
        </div>
        </script>
    </div>
</div>
