<g:if test="${speciesProfile && speciesProfile.taxonName}">
    <div class="bs-docs-example" id="browse_names" data-content="Nomenclature">
        <ul style="list-style: none; margin-left:0px;">
            <li>
                <blockquote style="border-left:none;">
                    <p>${speciesProfile.taxonName.nameComplete} ${speciesProfile.taxonName.authorship}</p>
                </blockquote>
            </li>
            <g:each in="${speciesProfile.synonyms}" var="synonym">
                <li>
                    <blockquote style="border-left:none;">
                        <p>${synonym.nameString}</p>
                        <cite>- ${synonym.referencedIn ?: 'Reference not available'}</cite>
                    </blockquote>
                </li>
            </g:each>
        </ul>
    </div>
</g:if>