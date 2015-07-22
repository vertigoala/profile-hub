<div class="panel panel-default" ng-form="StyleForm" ng-cloak>
    <div class="panel-heading">
        <a name="branding">
            <h4 class="section-panel-heading">Branding</h4>
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
                <label>Email contact</label>
                <input type="text" class="form-control" name="email" ng-model="opusCtrl.opus.contact.email"/> <br/>
                <span class="small">Setting the email contact will add a 'contact by email' icon to the page footer</span>
            </div>

            <div class="form-group">
                <label for="facebook">Facebook URL</label>
                <input id="facebook" type="text" class="form-control" name="facebook" ng-model="opusCtrl.opus.contact.facebook"/> <br/>
                <span class="small">Setting the facebook URL will add a 'contact by facebook' icon to the page footer</span>
            </div>

            <div class="form-group">
                <label for="twitter">Twitter URL</label>
                <input id="twitter" type="text" class="form-control" name="twitter" ng-model="opusCtrl.opus.contact.twitter"/> <br/>
                <span class="small">Setting the twitter URL will add a 'contact by twitter' icon to the page footer</span>
            </div>

            <div class="form-group">
                <label>Copyright statement</label>

                <div text-angular text-angular-name="copyright" ng-model="opusCtrl.opus.copyrightText"
                     ta-toolbar="{{richTextToolbarSimple}}" ta-max-text="1000"></div>

                <span class="small">To be displayed at the bottom of each profile page</span>
            </div>

            <div class="form-group">
                <label for="footerText">Footer text</label>

                <textarea id="footerText" ng-model="opusCtrl.opus.footerText" rows="4"
                          class="form-control" ng-maxlength="300" maxlength="300"></textarea>
                <span class="small">To be displayed in the page footer</span>
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