<div class="panel panel-default" ng-form="KeyForm" ng-cloak>
    <div class="panel-heading">
        <a name="key">
            <h4>Keys configuration</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="col-sm-12">
            <div class="form-group">
                <label>Select the <a
                        href="${grailsApplication.config.keybase.web.url}">Keybase</a> Project for this collection:
                </label>
                <select ng-options="project.project_name for project in opusCtrl.keybaseProjects | orderBy:'project_name'"
                        ng-model="opusCtrl.selectedKeybaseProject" class="form-control">
                    <option value="">--- Select one ---</option>
                </select>
            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <button class="btn btn-primary pull-right" ng-click="opusCtrl.saveOpus(KeyForm)">
                    <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="KeyForm.$dirty">*</span> Save</span>
                    <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>

</div>