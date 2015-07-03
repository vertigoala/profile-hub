<div class="row">
    <div class="col-lg-5 col-md-6 col-sm-12" ng-controller="MapController as mapCtrl"
         ng-init="mapCtrl.init('${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.wms.path}', '${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.info.path}')">
        <div class="col-md-12" ng-cloak>
            <leaflet style="height: 300px; width: 450px;" center="mapCtrl.center" class="thumbnail"
                     layers="mapCtrl.layers"
                     event-broadcast="mapCtrl.events"></leaflet>
        </div>

        <div class="col-md-12">
            <a href="{{profileCtrl.opus.biocacheUrl}}/occurrences/search?{{mapCtrl.constructQuery()}}" ng-if="profileCtrl.opus.biocacheUrl" target="_blank">View in {{profileCtrl.opus.biocacheName}}</a>
        </div>
    </div>

    <div class="col-lg-7 col-md-6 col-sm-12" ng-controller="ImagesController as imageCtrl"
         ng-init="imageCtrl.init('${edit}')" ng-show="imageCtrl.primaryImage" ng-cloak>
        <div id="primaryImage" class="col-md-12">
            <div class="primary-image col-md-12">
                <a href="${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}{{imageCtrl.primaryImage.occurrenceId}}"
                   target="_blank" ng-if="imageCtrl.primaryImage.largeImageUrl">
                    <img ng-src="{{imageCtrl.primaryImage.largeImageUrl}}" class="thumbnail"
                         ng-if="imageCtrl.primaryImage.largeImageUrl"/>
                </a>

            </div>

            <div class="col-md-12">
                <strong class="font-xxsmall">{{ imageCtrl.primaryImage.dataResourceName }}</strong>
            </div>

            <div class="col-md-12" ng-show="imageCtrl.primaryImage">
                <a target="_self" href=""
                   du-smooth-scroll="{{profileCtrl.readonly() ? 'view_' : 'edit_'}}images">View all images</a>
            </div>
        </div>
    </div>
</div>