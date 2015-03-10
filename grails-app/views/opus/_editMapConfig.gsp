<div id="opusInfo" class="well" ng-form="MapForm">
    <h3>Map configuration</h3>
    <p>
        <label>Attribution:</label>
        <input type="text" class="input-xxlarge" name="mapAttribution" ng-model="opusCtrl.opus.mapAttribution"/>
    </p>
    <p>
        <label>Biocache instance to link to:</label>
        <input type="text" class="input-xxlarge" name="biocacheUrl" ng-model="opusCtrl.opus.biocacheUrl"/>
    </p>
    <p>
        <label>Biocache instance name:</label>
        <input type="text" class="input-xxlarge" name="biocacheName" ng-model="opusCtrl.opus.biocacheName"/>
    </p>
    <p>
        <label>Base layer URL:</label>
        <input type="text" class="input-xxlarge" name="mapBaseLayer" ng-model="opusCtrl.opus.mapBaseLayer"/>
    </p>
    <p>
        <label>Point color:</label>
        <input colorpicker type="text" class="input-medium" colorpicker-close-on-select ng-model="opusCtrl.opus.mapPointColour"/>
        <span class="square" ng-style="{background: opusCtrl.opus.mapPointColour}"/>
    </p>
    <p>
        <label>Default map centre & zoom:</label>
        <span>Default latitude: </span><input type="text" class="input-medium" name="mapDefaultLatitude" ng-model="opusCtrl.opus.mapDefaultLatitude"/>
        <span>Default longitude: </span><input type="text" class="input-medium" name="mapDefaultLongitude" ng-model="opusCtrl.opus.mapDefaultLongitude"/>
        <span>Zoom level: </span><input type="text" class="input-medium" name="mapZoom" ng-model="opusCtrl.opus.mapZoom"/>
    </p>
    <button class="btn" ng-click="opusCtrl.saveOpus(MapForm)">
        <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="MapForm.$dirty">*</span> Save</span>
        <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
    </button>
</div>