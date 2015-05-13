<div id="opusKeyConfig" class="well" ng-form="KeyForm" ng-cloak>
    <h3>Keys configuration</h3>
    <p>
        <label>Select the <a href="${grailsApplication.config.keybase.web.url}">Keybase</a> Project for this collection:</label>
        <select ng-options="project.project_name for project in opusCtrl.keybaseProjects | orderBy:'project_name'" ng-model="opusCtrl.selectedKeybaseProject" class="input-xxlarge">
            <option value="">--- Select one ---</option>
        </select>
    </p>

    <button class="btn btn-primary" ng-click="opusCtrl.saveOpus(KeyForm)">
        <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="KeyForm.$dirty">*</span> Save</span>
        <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
    </button>
</div>