<div class="col-md-7">
    <div id="primaryImage" class="col-md-12 padding-bottom-2" ng-show="imageCtrl.primaryImage && !profileCtrl.showMap" ng-controller="ImagesController as imageCtrl"
         ng-init="imageCtrl.init('${edit}')" ng-cloak>
        <div class="primary-image">
            <a href="${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}{{imageCtrl.primaryImage.occurrenceId}}"
               target="_self" ng-if="imageCtrl.primaryImage.largeImageUrl">
                <img ng-src="{{imageCtrl.primaryImage.largeImageUrl}}" ng-if="imageCtrl.primaryImage.largeImageUrl"/>
            </a>

            <p class="font-xxsmall"><strong>{{ imageCtrl.primaryImage.dataResourceName }}</strong></p>
        </div>
    </div>

    <div ng-controller="MapController as mapCtrl"
         ng-init="mapCtrl.init('${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.wms.path}', '${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.info.path}')"
         class="col-md-12 padding-bottom-1" ng-cloak ng-show="profileCtrl.showMap">

        <leaflet style="height: 400px; margin-top:10px;" center="mapCtrl.center" layers="mapCtrl.layers"
                 event-broadcast="mapCtrl.events"></leaflet>

        <a class="btn btn-default pull-right"
           href="{{profileCtrl.opus.biocacheUrl}}/occurrences/search?q={{mapCtrl.constructQuery()}}">View in {{profileCtrl.opus.biocacheName}}</a>

    </div>

    <div class="btn-group">
        <label class="btn btn-default" ng-class="profileCtrl.showMap ? '' : 'disabled'"
               ng-model="profileCtrl.showMap" btn-radio="false"><i class="fa fa-picture-o"></i> <span
                class="hidden-sm hidden-xs">Show image</span></label>
        <label class="btn btn-default" ng-class="profileCtrl.showMap ? 'disabled' : ''"
               ng-model="profileCtrl.showMap" btn-radio="true"><i class="fa fa-map-marker"></i> <span
                class="hidden-sm hidden-xs">Show map</span></label>
    </div>

</div>
