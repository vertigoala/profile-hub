<div class="well control-group" ng-form="ImageForm">
    <h3>Approved Image sources</h3>

    <p>Configure the image sources to be included in your profile pages. These are image data resources accessible via Atlas API's.</p>

    <ul>
        <li ng-repeat="imageSource in opusCtrl.opus.imageSources">
            <a href="${grailsApplication.config.collectory.base.url}/public/show/{{imageSource}}">{{opusCtrl.dataResources[imageSource] | default:'Loading...'}}</a>
            <a class="btn btn-mini btn-danger" title="Remove this resource"
                    ng-click="opusCtrl.removeImageSource($index, 'existing', ImageForm)">
                <i class="icon-minus icon-white"></i>
            </a>
        </li>

        <li ng-repeat="imageSource in opusCtrl.newImageSources">
            <input placeholder="Image source name..."
                   ng-model="imageSource.dataResource"
                   class="input-xlarge"
                   typeahead="source as source.name for source in opusCtrl.dataResourceList | filter:$viewValue | limitTo:10"/>
            <span class="fa fa-ban red" ng-if="imageSource.dataResource && !imageSource.dataResource.id"></span>
            <button class="btn btn-mini btn-danger" title="Remove this resource"
                    ng-click="opusCtrl.removeImageSource($index, 'new', ImageForm)">
                <i class="icon-minus icon-white"></i>
            </button>
        </li>
    </ul>
    <button class="btn" ng-click="opusCtrl.saveImageSources(ImageForm)">
        <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="ImageForm.$dirty">*</span> Save</span>
        <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
    </button>
    <button class="btn" ng-click="opusCtrl.addImageSource()"><i class="icon icon-plus"></i>  Add image source</button>
</div>