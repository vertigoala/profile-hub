<div ng-controller="MapController"
     ng-init="init('${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.wms.path}', '${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.info.path}')"
     class="panel">

        <leaflet style="height: 400px; margin-top:10px;" center="center" layers="layers" event-broadcast="events"></leaflet>

        <a class="btn"
           href="${opus.biocacheUrl}/occurrences/search?q={{constructQuery()}}" >View in ${opus.biocacheName}</a>

</div>

<div id="firstImage" ng-show="firstImage" ng-controller="ImagesController" ng-init="init('${edit}')">
    <div class="imgConXXX">
        <a href="${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}{{firstImage.uuid}}"
           target="_self" ng-if="firstImage.largeImageUrl">
            <img ng-src="{{firstImage.largeImageUrl}}" ng-if="firstImage.largeImageUrl"/>
        </a>

        <div class="meta">{{ firstImage.dataResourceName }}</div>
    </div>
</div>

