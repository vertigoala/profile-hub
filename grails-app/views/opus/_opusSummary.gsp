<div class="well" style="margin-top:20px;">

    <g:link class="btn pull-right" mapping="editOpus" params="[opusId: opus.uuid]" target="_self">
        <span class="icon-edit"></span>&nbsp; Edit
    </g:link>

    <div ng-show="opusCtrl.opus.imageSources.length > 0">
        <h3>Image sources</h3>
        <ul>
            <li ng-repeat="imageSource in opusCtrl.opus.imageSources">
                <a href="${grailsApplication.config.collectory.base.url}/public/show/{{imageSource}}" target="_self">{{opusCtrl.dataResources[imageSource] | default:'Loading...'}}</a>
            </li>
        </ul>
    </div>

    <div ng-show="opusCtrl.opus.recordSources.length > 0">
        <h3>Specimen sources</h3>
        <ul>
            <li ng-repeat="recordSource in opusCtrl.opus.recordSources">
                <a href="${grailsApplication.config.collectory.base.url}/public/show/{{recordSource}}" target="_self">{{opusCtrl.dataResources[recordSource] | default:'Loading...'}}</a>
            </li>
        </ul>
    </div>

    <div ng-show="opusCtrl.opus.supportingOpuses.length > 0">
        <h3>Supporting collections</h3>
        <ul>
            <li ng-repeat="opus in opusCtrl.opus.supportingOpuses">
                <a href="${request.contextPath}/opus/{{opus.uuid}}" target="_self">{{opus.title | default:'Loading...'}}</a>
            </li>
        </ul>
    </div>
</div>