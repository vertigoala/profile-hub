<div class="panel panel-default" ng-form="opusCtrl.LandingPage" ng-cloak>
    <div class="panel-heading">
        <a name="landingpage">
            <h4 class="section-panel-heading">Home Page</h4>
            <p:help help-id="opus.edit.landingpage"/>
        </a>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-sm-12">
                <div class="form-group">
                    <label for="explanatoryText">Explanatory text</label>
                    <textarea id="explanatoryText" ng-model="opusCtrl.opus.opusLayoutConfig.explanatoryText" name="explanatoryText" ckeditor="richTextFullToolbar"></textarea>
                    <div class="small">
                        Enter a short description about this collection.
                    </div>
                </div>

                <div class="form-group">
                    <label>Image Slider</label>
                    <table class="table">
                        <thead>
                        <th>Image and Credit</th>
                        <th>Options</th>
                        </thead>
                        <tbody>
                        <tr ng-repeat="image in opusCtrl.opus.opusLayoutConfig.images" class="repeat-items">
                            <td>
                                <div class="margin-bottom-1" ng-if="image.imageUrl">
                                    <img class="img-thumbnail" ng-src="{{image.imageUrl}}">
                                </div>
                                <div class="input-group margin-bottom-1">
                                    <span class="input-group-addon">Image URL</span>
                                    <input type="text" class="form-control" name="imageUrl" ng-model="image.imageUrl"/>
                                </div>
                                <div class="input-group">
                                    <span class="input-group-addon">Credit</span>
                                    <input type="text" class="form-control" name="credit" ng-model="image.credit"/>
                                </div>
                            </td>
                            <td>
                                <button class="btn btn-link btn-xs fa fa-trash-o color--red" title="Delete image" ng-click="opusCtrl.removeItem($index, opusCtrl.opus.opusLayoutConfig.images, opusCtrl.LandingPage)"></button>
                                <button class="btn btn-link btn-xs fa fa-arrow-down ng-scope" ng-if="!$last" ng-click="opusCtrl.moveItemDown($index, opusCtrl.opus.opusLayoutConfig.images, opusCtrl.LandingPage)" title="Move this image down"></button>
                                <button class="btn btn-link btn-xs fa fa-arrow-up ng-scope" ng-if="!$first " ng-click="opusCtrl.moveItemUp($index, opusCtrl.opus.opusLayoutConfig.images, opusCtrl.LandingPage)" title="Move this image up"></button>
                            </td>
                        </tr>
                        </tbody>
                        <tfoot>
                        <tr>
                            <td colspan="2">
                                <button class="btn btn-sm btn-default margin-top-1 margin-bottom-1" ng-click="opusCtrl.addAnEmptyImage()" ><i class="fa fa-plus"></i> Add an external image</button>
                                <button class="btn btn-sm btn-default margin-top-1 margin-bottom-1 ignore-save-warning" ng-model="opusCtrl.showUpload.imageSlider" btn-checkbox >Upload a file</button>
                                <div ng-if="opusCtrl.showUpload.imageSlider" class="clearfix">
                                    <image-upload opus="opusCtrl.opus" on-upload-complete="opusCtrl.imageUploaded"
                                                  url-generator="opusCtrl.generateFileUploadUrl" show-metadata="false" disable-source="true"></image-upload>
                                </div>
                                <div class="small">
                                    Note! All images must be of the same dimension. It must be at least 900 pixels wide and 500 pixels high.
                                </div>
                            </td>
                        </tr>
                        </tfoot>
                    </table>
                    <div>
                        <label for="duration">Time duration</label>
                        <div class="input-group">
                            <span class="input-group-addon">Duration</span>
                            <input id="duration" type="number" class="form-control"  name="duration" ng-model="opusCtrl.opus.opusLayoutConfig.duration" min="1"/>
                            <span class="input-group-addon">ms</span>
                        </div>
                        <div class="small">
                            The time duration spent by the image slider on an image in milli-seconds.
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label for="updatesSection">Updates section</label>
                    <textarea id="updatesSection" ng-model="opusCtrl.opus.opusLayoutConfig.updatesSection" name="updatesSection" ckeditor="richTextFullToolbar"></textarea>
                    <div class="small">
                        Enter the formatted content that you wish to be included in the 'update section' for this collection.
                    </div>
                </div>

                <div class="form-group">
                    <label>Help text for sections in home page</label>
                    <div class="input-group margin-bottom-1">
                        <span class="input-group-addon">Search</span>
                        <input type="text" class="form-control"  name="helpTextSearch" ng-model="opusCtrl.opus.opusLayoutConfig.helpTextSearch"/>
                    </div>
                    <div class="input-group margin-bottom-1">
                        <span class="input-group-addon">Browse</span>
                        <input type="text" class="form-control"  name="helpTextBrowse" ng-model="opusCtrl.opus.opusLayoutConfig.helpTextBrowse"/>
                    </div>
                    <div class="input-group margin-bottom-1">
                        <span class="input-group-addon">Identify</span>
                        <input type="text" class="form-control"  name="helpTextIdentify" ng-model="opusCtrl.opus.opusLayoutConfig.helpTextIdentify"/>
                    </div>
                    <div class="input-group">
                        <span class="input-group-addon">Context</span>
                        <input type="text" class="form-control"  name="helpTextDocuments" ng-model="opusCtrl.opus.opusLayoutConfig.helpTextDocuments"/>
                    </div>
                    <div class="small">
                        Enter the formatted content that you wish to appear on hover on search, browse, identify and context buttons on collection home page.
                    </div>
                </div>

            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <save-button ng-click="opusCtrl.saveOpus(opusCtrl.LandingPage)" form="opusCtrl.LandingPage"></save-button>
            </div>
        </div>
    </div>
</div>