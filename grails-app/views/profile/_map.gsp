<div id="map" style="height: 400px; margin-top:10px;"></div>
<a class="btn"
   href="${opus.biocacheUrl}/occurrences/search?q=${occurrenceQuery}">View in ${opus.biocacheName}</a>

<div id="firstImage" ng-show="firstImage" ng-controller="ImagesController" ng-init="init('${edit}')">
    <div class="imgConXXX">
        <a href="${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}{{firstImage.uuid}}" target="_self">
            <img src="{{firstImage.largeImageUrl}}"/>
        </a>
        <div class="meta">{{ firstImage.dataResourceName }}</div>
    </div>
</div>

<div class="bs-docs-example hide" id="browse_lists" data-content="Conservation & sensitivity lists">
    <ul></ul>
</div>