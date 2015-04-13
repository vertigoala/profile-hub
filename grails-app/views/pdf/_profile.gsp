<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <title>${profile.scientificName}</title>

    <r:require module="profiles"/>
</head>

<body>

<div class="container-fluid">
    <div class="row-fluid">
        <h1>${profile.scientificName}</h1>
    </div>

    <g:each in="${profile.attributes}" var="attribute">
        <div>
            <h2>${attribute.title}</h2>
            <blockquote>
                <p class="display-text">${raw(attribute.text)}</p>
                <small>
                    Contributed by
                    <cite title="Contributors to this text">
                        ${attribute.creators.join(', ')}
                    </cite>
                </small>
                <g:if test="${attribute.editors}">
                    <div>
                        <small>
                            Edited by
                            <cite title="Editors to this text">
                                ${attribute.editors.join(', ')}
                            </cite>
                        </small>
                    </div>
                </g:if>
            </blockquote>
        </div>
    </g:each>

    <h2>Occurrences</h2>
    <img src="${mapImageUrl}"/>

    <g:if test="${classifications}">
        <div>
            <h2>Taxonomy from ${speciesProfile?.taxonConcept?.infoSourceName}</h2>
            <ul>
                <g:each in="${classifications}" var="classification">
                    <li>${classification.rank.capitalize()}: ${classification.scientificName}</li>
                </g:each>
            </ul>
        </div>
    </g:if>


    <g:if test="${speciesProfile?.taxonName}">
        <div>
            <h2>Nomenclature</h2>
            <ul>
                <li>
                    <blockquote>
                        <p>${speciesProfile.taxonName.nameComplete} ${speciesProfile.taxonName.authorship}</p>
                    </blockquote>
                </li>
                <g:each in="${speciesProfile.synonyms}" var="synonym">
                    <g:if test="${synonym.nameString?.trim()}">
                        <li>
                            <blockquote>
                                <p>${synonym.nameString}</p>
                                <g:if test="${synonym.referencedIn?.trim()}">
                                    <cite>- ${synonym.referencedIn}</cite>
                                </g:if>
                            </blockquote>
                        </li>
                    </g:if>
                </g:each>
            </ul>
        </div>
    </g:if>
</div>
</body>

</html>