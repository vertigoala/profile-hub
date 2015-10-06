<div class="panel panel-default" ng-form="ImageForm" ng-cloak>
    <div class="panel-heading">
        <a name="imageSources">
            <h4 class="section-panel-heading">Approved Image sources</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="row" ng-show="opusCtrl.opus.privateCollection">
            <div class="col-sm-12">
                <div class="checkbox padding-bottom-1">
                    <label for="privateImages" class="inline-label">
                        <input id="privateImages" type="checkbox" name="privateImages"
                               ng-change="opusCtrl.privateImageModeChanged()"
                               ng-model="opusCtrl.opus.keepImagesPrivate" ng-false-value="false">
                        Do not publish images
                    </label>
                    <div class="small padding-left-1" ng-show="opusCtrl.opus.keepImagesPrivate">
                        When this option is selected, any images uploaded to this collection will NOT be published to the Atlas of Living Australia's public image repository. This option is only available to Private collections.
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <div class="col-sm-12">
                <p>Configure additional image sources to be included in your profile pages. These are image data resources accessible via Atlas API's.</p>
                <ul>
                    <li ng-repeat="imageSource in opusCtrl.opus.imageSources">
                        <a href="${grailsApplication.config.collectory.base.url}/public/show/{{imageSource}}">{{opusCtrl.dataResources[imageSource] | default:'Loading...'}}</a>
                        <a class="btn btn-mini btn-link " title="Remove this resource"
                           ng-click="opusCtrl.removeImageSource($index, 'existing', ImageForm)">
                            <i class="fa fa-trash-o color--red"></i>
                        </a>
                    </li>

                    <li ng-repeat="imageSource in opusCtrl.newImageSources">
                        <div class="form-inline">
                            <div class="form-group">
                                <input placeholder="Image source name..."
                                       ng-model="imageSource.dataResource"
                                       autocomplete="off"
                                       size="70"
                                       class="form-control"
                                       typeahead="source as source.name for source in opusCtrl.dataResourceList | filter:$viewValue | limitTo:10"/>
                                <span class="fa fa-ban color--red"
                                      ng-if="imageSource.dataResource && !imageSource.dataResource.id"></span>
                                <button class="btn btn-mini btn-link" title="Remove this resource"
                                        ng-click="opusCtrl.removeImageSource($index, 'new', ImageForm)">
                                    <i class="fa fa-trash-o color--red"></i>
                                </button>
                            </div>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <div class="btn-group">
                    <button class="btn btn-default" ng-click="opusCtrl.addImageSource()"><i
                            class="fa fa-plus"></i>  Add image source</button>
                </div>
                <button class="btn btn-primary pull-right" ng-click="opusCtrl.saveImageSources(ImageForm)">
                    <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="ImageForm.$dirty">*</span> Save</span>
                    <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>

</div>