<div class="well">
    <h3>Approved Specimen/Observation sources</h3>
    <p>Configure the record sources to be included in your profile pages. This will set what data is used on maps.
    These are data resources accessible via Atlas API's.
    </p>
    <ul>
        <g:each in="${opus.recordSources}" var="recordSource">
            <li style="padding:5px;">
                <a href="http://collections.ala.org.au/public/show/${recordSource}">${dataResources[recordSource]}</a>
                &nbsp;<button class="btn btn-mini btn-danger" title="Remove this resource"><i class="icon-minus icon-white"></i></button>
            </li>
        </g:each>
    </ul>
    <a href="javascript:alert('Not implemented yet!');" class="btn"><i class="" onclick="alert('Not implemented yet!');"></i> Save</a>
</div>