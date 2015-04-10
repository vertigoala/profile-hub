<div ng-controller="GlossaryController as glossaryCtrl" id="glossary" class="well" ng-cloak>
    <h3>Glossary</h3>

    <p>
        Upload a CSV file containing your glossary. NOTE: This will completely replace any existing glossary items. To add, edit or delete individual items, go to the glossary page.
    </p>

    <p>
        The CSV must have columns "term","description".
    </p>

    <form ng-submit="glossaryCtrl.upload()" role="form">
        <label for="file" class="inline-label">Upload .csv file:</label>

        <div class="row-fluid">
            <div class="fileupload fileupload-new pull-left" data-provides="fileupload">
                <div class="input-append">
                    <div class="uneditable-input span3">
                        <i class="icon-file fileupload-exists"></i>
                        <span class="fileupload-preview"></span>
                    </div>
                    <span class="btn btn-file">
                        <span class="fileupload-new">Select file</span>
                        <span class="fileupload-exists">Change</span>
                        <input type="file" name="csvFile" id="file"
                               onchange="angular.element(this).scope().glossaryCtrl.setFileToUpload(this)" required/>
                    </span>
                    <a href="#" class="btn fileupload-exists" data-dismiss="fileupload">Remove</a>
                    <button class="btn btn-primary">Upload</button>
                </div>
            </div>
        </div>
    </form>
</div>