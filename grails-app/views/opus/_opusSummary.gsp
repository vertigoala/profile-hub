<div class="well" style="margin-top:20px;">
    <div>
        <g:if test="${!edit}">
            <g:link class="btn pull-right" mapping="editOpus" params="[opusId: opus.uuid]" target="_self">
                <i class="icon-edit"></i>&nbsp;Edit
            </g:link>
        </g:if>
        <g:else>
            <g:link class="btn pull-right" mapping="viewOpus" params="[opusId: opus.uuid]" target="_self">Public view</g:link>
        </g:else>
    </div>

    <div ng-show="opusCtrl.opus.imageSources.length > 0">
        <h3>Image sources</h3>
        <ul>
            <li ng-repeat="imageSource in opusCtrl.opus.imageSources">
                <a href="${grailsApplication.config.collectory.base.url}/public/show/{{imageSource}}">{{opusCtrl.dataResources[imageSource]}}</a>
            </li>
        </ul>
    </div>

    <div ng-show="opusCtrl.opus.recordSources.length > 0">
        <h3>Specimen sources</h3>
        <ul>
            <li ng-repeat="recordSource in opusCtrl.opus.recordSources">
                <a href="${grailsApplication.config.collectory.base.url}/public/show/{{recordSource}}">{{opusCtrl.dataResources[recordSource]}}</a>
            </li>
        </ul>
    </div>
</div>