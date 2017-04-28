<g:if test="${opus.opusLayoutConfig?.images && opus.opusLayoutConfig.images?.size()}">
    <div>
        <div>
            <uib-carousel interval="opusCtrl.opus.opusLayoutConfig.duration" no-wrap="false">
                <uib-slide ng-repeat="image in opusCtrl.opus.opusLayoutConfig.images">
                    <img ng-src="{{image.imageUrl}}" style="margin:auto;">
                    <div class="carousel-caption" ng-cloak>
                        <p>{{image.credit}}</p>
                    </div>
                </uib-slide>
            </uib-carousel>
        </div>
    </div>
</g:if>