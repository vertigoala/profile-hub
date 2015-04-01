<div ng-controller="ImagesController as imageCtrl" ng-init="imageCtrl.init('${edit}')" ng-cloak>
    <div class="bs-docs-example" id="browse_images" data-content="Images" ng-show="imageCtrl.images.length > 0">
        <div ng-repeat="image in imageCtrl.images" class="imgCon">
            <a href="${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}{{image.uuid}}"
               target="_self" ng-if="image.largeImageUrl">
                <img ng-src="{{image.largeImageUrl}}" ng-if="image.largeImageUrl"/>
            </a>

            <div class="meta">{{ image.dataResourceName }}</div>
        </div>
    </div>
</div>