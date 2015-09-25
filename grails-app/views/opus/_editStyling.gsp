<div class="panel panel-default" ng-form="StyleForm" ng-cloak>
    <div class="panel-heading">
        <a name="branding">
            <h4 class="section-panel-heading">Branding</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-sm-12">
                <div class="form-group">
                    <label>Banner image</label>
                    <input type="text" class="form-control" name="bannerUrl" ng-model="opusCtrl.opus.bannerUrl"/><br/>
                    <span class="small">Recommended size is at least 1000px wide by 100px high.</span>
                </div>

                <div class="form-group">
                    <label>Logo</label>
                    <input type="text" class="form-control" name="logoUrl" ng-model="opusCtrl.opus.logoUrl"/><br/>
                    <span class="small">Recommended maximum width is 275px. There is no height limit.</span>
                </div>

                <div class="form-group">
                    <label>Thumbnail</label>
                    <input type="text" class="form-control" name="thumbnailUrl" ng-model="opusCtrl.opus.thumbnailUrl"/><br/>
                    <span class="small">Recommended size 160px by 100px.</span>
                </div>

                <div class="form-group">
                    <label>Short Name</label>
                    <input type="text" class="form-control" name="shortName" ng-model="opusCtrl.opus.shortName" ng-change="opusCtrl.showShortNameTip()"/> <br/>
                    <span class="small">The URL for your collection will be http://${request.serverName}/opus/{{opusCtrl.opus.shortName ? opusCtrl.opus.shortName : opusCtrl.opus.uuid | lowercase}}</span>
                    <div class="well small" ng-show="opusCtrl.shortNameTipVisible">
                        <i class="fa fa-info-circle color--medium-blue margin-bottom-1"></i>
                        <p/>
                        NOTE: Changing the Short Name of your collection will change the URL used to access it. This will affect any existing bookmarks, and may affect some of the collection statistics.
                        <p/>
                        When changing the Short Name, save the Branding section and leave the Edit Configuration page, then return - this will ensure that your browser is using the new URL.
                    </div>
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
                    <label for="copyright">Copyright statement</label>

                    <div class="input-group">
                        <span class="input-group-addon">&copy;</span>
                        <input id="copyright" type="text" class="form-control" name="copyright" ng-model="opusCtrl.opus.copyrightText"/> <br/>
                    </div>
                    <span class="small">e.g. &copy; {{opusCtrl.opus.title}}, 2015. To be displayed at the bottom of each profile page</span>
                </div>

                <div class="form-group">
                    <label for="footerText">Footer text</label>

                    <textarea id="footerText" ng-model="opusCtrl.opus.footerText" rows="4"
                              class="form-control" ng-maxlength="300" maxlength="300"></textarea>
                    <span class="small">To be displayed in the page footer</span>
                </div>
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