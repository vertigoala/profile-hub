<div ng-controller="ImagesController" ng-init="init('${edit}')">
    <div class="bs-docs-example" id="browse_images" data-content="Images" ng-show="images.length > 0">
        <div ng-repeat="image in images" class="imgCon">
            <a href="${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}{{image.uuid}}"
               target="_self" ng-if="image.largeImageUrl">
                <img ng-src="{{image.largeImageUrl}}" ng-if="image.largeImageUrl"/>
            </a>

            <div class="meta">{{ image.dataResourceName }}</div>
        </div>
    </div>

    %{--<div class="image-carousel">--}%
        %{--<carousel interval="imagesSlideShowInterval">--}%
            %{--<slide ng-repeat="slide in slides" active="slide.active">--}%
                %{--<img ng-src="{{slide.image}}" style="margin:auto;">--}%

                %{--<div class="carousel-caption">--}%
                    %{--<h4>Image {{$index}}</h4>--}%

                    %{--<p>{{slide.text}}</p>--}%
                %{--</div>--}%
            %{--</slide>--}%
        %{--</carousel>--}%
    %{--</div>--}%
</div>