<div id="opusInfo" class="well">
    <h3>Map configuration</h3>
    <p>
        <label>Attribution:</label>
        <input type="text" class="input-xxlarge" name="mapAttribution" value="${opus.mapAttribution}"/>
    </p>
    <p>
        <label>Biocache instance to link to:</label>
        <input type="text" class="input-xxlarge" name="biocacheUrl" value="${opus.biocacheUrl}"/>
    </p>
    <p>
        <label>Biocache instance name:</label>
        <input type="text" class="input-xxlarge" name="biocacheName" value="${opus.biocacheName}"/>
    </p>
    <p>
        <label>Base layer URL:</label>
        <input type="text" class="input-xxlarge" name="mapBaseLayer" value="${opus.mapBaseLayer}"/>
    </p>
    <p>
        <label>Point color:</label>
        <input type="text" class="input-medium" name="mapPointColour" value="${opus.mapPointColour}"/>
    </p>
    <p>
        <label>Default map centre & zoom:</label>
        <span>Default latitude: </span><input type="text" class="input-medium" name="mapDefaultLatitude" value="${opus.mapDefaultLatitude}"/>
        <span>Default longitude: </span><input type="text" class="input-medium" name="mapDefaultLongitude" value="${opus.mapDefaultLongitude}"/>
        <span>Zoom level: </span><input type="text" class="input-medium" name="mapZoom" value="${opus.mapZoom}"/>
    </p>
    <a class="btn" href="javascript:alert('Not implemented yet')">Save</a>
</div>