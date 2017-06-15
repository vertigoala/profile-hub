<div class="row padding-bottom-2">
    <div class="col-lg-8 col-md-6 col-sm-6" ng-cloak>
        <p class="lead">
            ${opus?.description ?: '{{opusCtrl.opus.description}}'}
        </p>
        <g:if test="${aboutPageUrl}">
            <p class="margin-bottom-2">
                To find more information about the ${opus?.title ?: '{{opusCtrl.opus.title}}' }, visit <a href="${aboutPageUrl}"
                                                                                     target="_blank">our About page</a>.
            </p>
        </g:if>
    </div>
</div>
