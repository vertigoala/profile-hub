<div class="panel panel-default" ng-form="StyleForm" ng-cloak>
    <div class="panel-heading">
        <a name="branding">
            <h4>Branding</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="col-sm-12">
            <div class="form-group">
                <label>Banner image</label>
                <input type="text" class="form-control" name="bannerUrl" ng-model="opusCtrl.opus.bannerUrl"/>
            </div>

            <div class="form-group">
                <label>Logo</label>
                <input type="text" class="form-control" name="logoUrl" ng-model="opusCtrl.opus.logoUrl"/>
            </div>

            <div class="form-group">
                <label>Thumbnail</label>
                <input type="text" class="form-control" name="thumbnailUrl" ng-model="opusCtrl.opus.thumbnailUrl"/>
            </div>

            <div class="form-group">
                <label>Short Name</label>
                <input type="text" class="form-control" name="shortName" ng-model="opusCtrl.opus.shortName"/> <br/>
                <span class="small">The URL for your collection will be http://${request.serverName}/opus/{{opusCtrl.opus.shortName ? opusCtrl.opus.shortName : opusCtrl.opus.uuid | lowercase}}</span>
            </div>

            <div class="form-group">
                <label>Copyright statement</label>

                <div text-angular text-angular-name="copyright" ng-model="opusCtrl.opus.copyrightText"
                     ta-toolbar="{{richTextToolbarSimple}}" class="single-line-editor"
                     ng-enter="" ta-max-text="300"></div>
                <span class="small">(To be displayed at the bottom of each profile page)</span>
            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <button class="btn btn-primary pull-right" ng-click="opusCtrl.saveOpus(StyleForm)">
                    <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="StyleForm.$dirty">*</span> Save
                    </span>
                    <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>
</div>