<div id="opusInfo" class="well">
    <h4>Description</h4>
    <p>
        {{opusCtrl.dataResource.pubDescription | default:'No description available.'}}
    </p>
    <h4>Rights</h4>
    <p>
        {{opusCtrl.dataResource.rights | default:'No rights statement available.'}}
    </p>
    <h4>Citation</h4>
    <p>
        {{opusCtrl.dataResource.citation | default:'No citation statement available.'}}
    </p>
</div>