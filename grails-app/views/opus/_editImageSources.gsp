<div class="well">
    <h3>Approved Image sources</h3>
    <p>Configure the image sources to be included in your profile pages. These are image data resources accessible via Atlas API's.</p>
    <ul>
        <g:each in="${opus.imageSources}" var="imageSource">
            <li style="padding:5px;">
                <a href="http://collections.ala.org.au/public/show/${imageSource}">${dataResources[imageSource]}</a>
                &nbsp;<button class="btn btn-mini btn-danger" title="Remove this resource"><i class="icon-minus icon-white"></i></button>
            </li>
        </g:each>
    </ul>
    <a href="javascript:alert('Not implemented yet!');" class="btn"> Save</a>
</div>