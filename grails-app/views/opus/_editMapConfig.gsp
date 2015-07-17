<div class="panel panel-default" ng-form="MapForm" ng-cloak>
    <div class="panel-heading">
        <a name="map">
            <h4>Map configuration</h4>
        </a>
    </div>

    <div class="panel-body">
        <div class="col-sm-12">
            <div class="form-group">
                <label for="attribution">Attribution</label>
                <input id="attribution" type="text" class="form-control" name="mapAttribution"
                       ng-model="opusCtrl.opus.mapAttribution">
            </div>

            <div class="form-group">
                <label for="biocacheUrl">Biocache instance to link to</label>
                <input id="biocacheUrl" type="text" class="form-control" name="biocacheUrl"
                       ng-model="opusCtrl.opus.biocacheUrl">
            </div>

            <div class="form-group">
                <label for="biocacheName">Biocache instance name</label>
                <input id="biocacheName" type="text" class="form-control" name="biocacheName"
                       ng-model="opusCtrl.opus.biocacheName">
            </div>

            <div class="form-group">
                <label for="mapBaseLayer">Base layer URL</label>
                <input id="mapBaseLayer" type="text" class="form-control" name="mapBaseLayer"
                       ng-model="opusCtrl.opus.mapBaseLayer">
            </div>

            <div class="form-group">
                <label for="pointColour">Point color</label>

                <div class="input-group">
                    <input id="pointColour" type="text" class="form-control" colorpicker colorpicker-close-on-select
                           ng-model="opusCtrl.opus.mapPointColour">
                    <span class="input-group-addon" ng-style="{background: opusCtrl.opus.mapPointColour}"></span>
                </div>
            </div>

            <h4 class="heading-underlined">Default map centre &amp; zoom</h4>

            <div class="form-inline">
                <div class="form-group">
                    <label class="control-label" for="mapDefaultLatitude">Default latitude</label>
                    <input id="mapDefaultLatitude" type="text" class="form-control" maxlength="4" size="5"
                           name="mapDefaultLatitude" ng-model="opusCtrl.opus.mapDefaultLatitude">
                </div>

                <div class="form-group">
                    <label class="control-label" for="mapDefaultLongitude">Default longitude</label>
                    <input id="mapDefaultLongitude" type="text" class="form-control" maxlength="4" size="5"
                           name="mapDefaultLongitude" ng-model="opusCtrl.opus.mapDefaultLongitude">
                </div>

                <div class="form-group">
                    <label class="control-label" for="mapZoom">Zoom level</label>
                    <input id="mapZoom" type="text" class="form-control" maxlength="4" size="1" name="mapZoom"
                           ng-model="opusCtrl.opus.mapZoom">
                </div>
            </div>

            <h4 class="heading-underlined">Ranks to exclude from occurrence maps</h4>
            <div class="form-inline">
                <div class="form-group">
                    <div class="checkbox col-lg-2 col-md-2 col-sm-12" ng-repeat="(key, rank) in opusCtrl.ranks">
                        <label>
                            <input type="checkbox" checklist-model="opusCtrl.opus.excludeRanksFromMap" checklist-value="rank">&nbsp; {{rank | capitalize}}
                        </label>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <button class="btn btn-primary pull-right" ng-click="opusCtrl.saveOpus(MapForm)">
                    <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="MapForm.$dirty">*</span> Save</span>
                    <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>
</div>
