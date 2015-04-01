<div class="well" style="margin-top:20px;">

    <g:if test="${params.isOpusAdmin}">
        <a href="${request.contextPath}/opus/{{opusCtrl.opusId}}/update" target="_self" class="btn btn-warning pull-right" ng-hide="!config.readonly"><i class="icon-edit icon-white"></i> Edit</a>
    </g:if>

    <div class="row-fluid">
        <h3>Statistics</h3>
        <div ng-cloak>
            <div class="row-fluid">
            <div class="span6">
                Profiles in collection
            </div>
            <div class="span3">
                {{opusCtrl.opus.profileCount}}
            </div>
            </div>
            <div class="row-fluid">
            <div class="span6">
                Editors
            </div>
            <div class="span3">
                {{opusCtrl.opus.editors.length}}
            </div>
                </div>
        </div>
    </div>

    <div class="row-fluid" ng-show="opusCtrl.opus.imageSources.length > 0">
        <h3>Image sources</h3>
        <ul ng-cloak>
            <li ng-repeat="imageSource in opusCtrl.opus.imageSources">
                <a href="${grailsApplication.config.collectory.base.url}/public/show/{{imageSource}}" target="_self">{{opusCtrl.dataResources[imageSource] | default:'Loading...'}}</a>
            </li>
        </ul>
    </div>

    <div class="row-fluid" ng-show="opusCtrl.opus.recordSources.length > 0">
        <h3>Specimen sources</h3>
        <ul ng-cloak>
            <li ng-repeat="recordSource in opusCtrl.opus.recordSources">
                <a href="${grailsApplication.config.collectory.base.url}/public/show/{{recordSource}}" target="_self">{{opusCtrl.dataResources[recordSource] | default:'Loading...'}}</a>
            </li>
        </ul>
    </div>

    <div class="row-fluid" ng-show="opusCtrl.opus.supportingOpuses.length > 0">
        <h3>Supporting collections</h3>
        <ul ng-cloak>
            <li ng-repeat="opus in opusCtrl.opus.supportingOpuses">
                <a href="${request.contextPath}/opus/{{opus.uuid}}" target="_self">{{opus.title | default:'Loading...'}}</a>
            </li>
        </ul>
    </div>

    <hr/>
    <g:if test="${params.isOpusEditor}">
        <div ng-controller="ProfileController as profileCtrl">
            <button class="btn btn-info vertical-pad" ng-click="profileCtrl.createProfile(opusCtrl.opusId)">Add new profile</button>
        </div>
    </g:if>
</div>

<script type="text/ng-template" id="createProfile.html">
<div class="modal-header">
    <h3 class="modal-title">Create new profile</h3>
</div>

<div class="modal-body">
    <alert type="danger" class="error" ng-show="createProfileCtrl.error">{{createProfileCtrl.error}}</alert>
    <label for="scientificName" class="inline-label">Scientific Name</label>
    <input id="scientificName" type="text" ng-model="createProfileCtrl.scientificName" class="input-xlarge" required ng-enter="createProfileCtrl.ok()"/>
</div>

<div class="modal-footer">
    <button class="btn btn-primary" ng-click="createProfileCtrl.ok()" ng-disabled="!createProfileCtrl.scientificName">OK</button>
    <button class="btn btn-warning" ng-click="createProfileCtrl.cancel()">Cancel</button>
</div>
</script>