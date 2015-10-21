<div class="panel panel-default" ng-controller="AboutController as aboutCtrl" ng-form="AboutForm" ng-cloak>
    <div class="panel-heading">
        <a name="about">
            <h4 class="section-panel-heading">About page content</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-sm-12">
                <p>
                    Enter the formatted content that you wish to be included in the 'about' page for your collection.
                </p>

                <label for="aboutHtml" class="screen-reader-label">About text</label>
                <textarea id="aboutHtml" ng-model="aboutCtrl.aboutHtml" name="attribute" ckeditor="richTextFullToolbar"></textarea>

                <div class="small">(Maximum of 5000 characters)</div>
            </div>
            <div class="col-sm-12 padding-top-1">
                <p>
                    Enter the <strong>formatted citation</strong> that you wish to be included in the 'about' page for your collection and the generated pdf files.
                </p>

                <label for="citationHtml" class="screen-reader-label">Citation text</label>
                <textarea id="citationHtml" ng-model="aboutCtrl.citationHtml" name="attribute" ckeditor="richTextFullToolbar"></textarea>

                <div class="small">(Maximum of 500 characters)</div>
            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <button class="btn btn-primary pull-right" ng-click="aboutCtrl.saveAboutHtml(AboutForm)"
                        ng-disabled="AboutForm.$invalid">
                    <span ng-show="AboutForm.$dirty">*</span> Save
                </button>
            </div>
        </div>
    </div>
</div>