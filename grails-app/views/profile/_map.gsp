<div class="col-md-6" ng-controller="ImagesController as imageCtrl"
     ng-init="imageCtrl.init('${edit}')" ng-show="imageCtrl.primaryImage" ng-cloak>
    <div id="primaryImage" class="col-md-12">
        <div class="primary-image">
            <a href="${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}{{imageCtrl.primaryImage.occurrenceId}}"
               target="_self" ng-if="imageCtrl.primaryImage.largeImageUrl">
                <img ng-src="{{imageCtrl.primaryImage.largeImageUrl}}" ng-if="imageCtrl.primaryImage.largeImageUrl"/>
            </a>

            <p class="font-xxsmall"><strong>{{ imageCtrl.primaryImage.dataResourceName }}</strong></p>
        </div>
    </div>
    <div class="col-md-12" ng-show="imageCtrl.primaryImage">
        <a target="_self" href=""
           du-smooth-scroll="{{profileCtrl.readonly() ? 'view_' : 'edit_'}}images">View other images</a>
    </div>
</div>

<div class="col-md-6">
    <div ng-controller="MapController as mapCtrl"
         ng-init="mapCtrl.init('${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.wms.path}', '${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.info.path}')"
         class="col-md-12" ng-cloak>

        <leaflet style="height: 300px; width: 450px; float: right; margin-top:10px;" center="mapCtrl.center"
                 layers="mapCtrl.layers"
                 event-broadcast="mapCtrl.events"></leaflet>
    </div>

    <div class="col-md-12">
        <a class="pull-right padding-top-1"
           href="{{profileCtrl.opus.biocacheUrl}}/occurrences/search?q={{mapCtrl.constructQuery()}}">View in {{profileCtrl.opus.biocacheName}}</a>
    </div>
</div>
