<g:if test="${classification}">
    <div class="bs-docs-example" id="browse_taxonomy"
         data-content="Taxonomy from ${speciesProfile.taxonConcept.infoSourceName}">
        <ul>
            <g:each in="${classification}" var="taxon">
                <g:if test="${taxon.profileUuid}">
                    <li><g:link mapping="viewProfile"
                                params="${[uuid: taxon.profileUuid]}">${taxon.rank.capitalize()}: ${taxon.scientificName}</g:link></li>
                </g:if>
                <g:else>
                    <li>${taxon.rank.capitalize()}: ${taxon.scientificName}</li>
                </g:else>

            </g:each>
        </ul>
    </div>
</g:if>