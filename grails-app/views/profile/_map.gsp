<g:if test="${displayMap}">
    <div class="col-md-6 col-sm-12" ng-controller="MapController as mapCtrl"
         ng-cloak
         ng-init="mapCtrl.init('${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.wms.path}', '${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.info.path}')">
        <div class="row">
            <div class="col-md-12">
                <div class="thumbnail pull-left">
                    <a name="{{profileCtrl.readonly() ? 'view_' : 'edit_'}}map"></a>

                    <m:map id="occurrenceMap" height="300px" width="450px"/>
                  </div>
            </div>
        </div>

        <div class="row">
            <div class="col-md-12">
                <a href="{{profileCtrl.opus.biocacheUrl}}/occurrences/search?{{mapCtrl.constructQuery()}}"
                   ng-if="profileCtrl.opus.biocacheUrl" target="_blank"
                   class="padding-left-1 margin-top-1 inline-block">View in {{profileCtrl.opus.biocacheName}}</a>
            </div>
        </div>
    </div>
</g:if>