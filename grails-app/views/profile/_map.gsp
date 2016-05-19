<g:if test="${displayMap}">
    <div ng-controller="MapController as mapCtrl"
         ng-cloak ng-form="MapForm" ng-class="mapCtrl.editingMap ? 'col-md-12' : 'col-md-6 col-sm-12'"
         >
        <navigation-anchor name="map" title="Map" on-display="mapCtrl.init()"></navigation-anchor>

        <div class="row">
            <g:if test="${edit}">
                <div class="col-md-12">
                    <div ng-show="!mapCtrl.editingMap">
                        <div class="thumbnail pull-left">
                            <m:map id="occurrenceMap" height="300px" width="450px"/>
                        </div>
                    </div>

                    <div ng-show="mapCtrl.editingMap">
                        <m:occurrenceMap id="editOccurrenceMap"/>

                        <div class="row">
                            <div class="col-md-12 padding-top-1">
                                <save-button ng-click="mapCtrl.saveMapConfiguration(MapForm)"
                                             disabled="!MapForm.$dirty"
                                             dirty="MapForm.$dirty"
                                             form="MapForm">
                                </save-button>
                                <button class="btn btn-default"
                                        ng-click="mapCtrl.toggleEditingMap()">Cancel edit</button>
                                <button class="btn btn-danger"
                                        ng-click="mapCtrl.resetToDefaultMapConfig()">Reset to default</button>
                                <button class="btn btn-warning"
                                        ng-click="mapCtrl.undoAllMapChanges()">Undo unsaved changes</button>
                            </div>
                        </div>
                    </div>
                </div>
            </g:if>
            <g:else>
                <div class="col-md-12">
                    <div class="thumbnail pull-left">
                        <m:map id="occurrenceMap" height="300px" width="450px"/>
                    </div>
                </div>
            </g:else>
        </div>

        <div class="row" ng-show="!mapCtrl.editingMap && mapCtrl.profile">
            <div class="col-md-12">
                <div>
                    <a href="" ng-show="mapCtrl.hasEditorCustomisations()" ng-click="mapCtrl.toggleEditorCustomisations()" target="_blank"
                       class="margin-top-1 inline-block">{{ mapCtrl.showingEditorView ? 'Show' : 'Hide' }} non-vouchered occurrences</a>
                    <span class="padding-right-1 padding-left-1" ng-show="mapCtrl.hasEditorCustomisations()">|</span>
                    <a href="${grailsApplication.config.biocache.base.url}/occurrences/search?{{mapCtrl.profile.occurrenceQuery}}#tab_mapView"
                       ng-if="profileCtrl.opus.mapConfig.biocacheUrl" target="_blank"
                       class="margin-top-1 inline-block">Explore in the Atlas of Living Australia</a>
                </div>
                <g:if test="${edit}">
                    <button class="btn btn-default btn-sm margin-top-1"
                            ng-click="mapCtrl.toggleEditingMap()">Edit map configuration</button>
                </g:if>
            </div>
        </div>

        <div class="padding-bottom-2" ng-show="mapCtrl.editingMap"></div>
    </div>
</g:if>


