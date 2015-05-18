<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <title>${profile.scientificName}</title>

    <link rel="stylesheet" href="${resource(dir: '/thirdparty/bootstrap/css/bootstrap-3.1.1.min.css', absolute: true, )}" type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/nsl.css', absolute: true)}" type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/profiles.css', absolute: true)}" type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/pdf.css', absolute: true)}" type="text/css"/>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <h1>${profile.scientificName}</h1>
    </div>

    <div>
        <i>${profile.authorship?.find { it.category == 'Author' }?.text ?: ""}</i>
    </div>

    <g:if test="${options.attributes}">
        <g:each in="${profile.attributes.sort { it.order }}" var="attribute">
            <div>
                <h2>${attribute.title}</h2>
                <blockquote>
                    <p class="display-text">${raw(attribute.text)}</p>
                    <g:if test="${opus.allowFineGrainedAttribution}">
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
                    </g:if>
                </blockquote>
            </div>
        </g:each>
    </g:if>

    <g:if test="${options.map}">
        <h2>Occurrences</h2>
        <img src="${mapImageUrl}"/>
    </g:if>

    <g:if test="${options.conservation && speciesProfile?.conservationStatuses}">
        <h2>Conservation Status</h2>
        <ul>
            <g:each in="${speciesProfile.conservationStatuses.sort({it.region})}" var="status">
                <li><span class="status ${status.colour}">${status.regionAbbrev ?: 'IUCN'}</span> ${status.rawStatus}</li>
            </g:each>
        </ul>
    </g:if>

    <g:if test="${options.taxonomy && classifications}">
        <div>
            <h2>Taxonomy from ${speciesProfile?.taxonConcept?.infoSourceName}</h2>
            <ul>
                <g:each in="${classifications}" var="classification">
                    <li>${classification.rank.capitalize()}: ${classification.scientificName}</li>
                </g:each>
            </ul>
        </div>
    </g:if>

    <g:if test="${options.nomenclature && profile.nslNameIdentifier && nomenclatureHtml}">
        <h2>Nomenclature</h2>
        ${raw(nomenclatureHtml)}
    </g:if>

    <g:if test="${options.links && profile.links}">
        <h2>Links</h2>
        <ul>
        <g:each in="${profile.links}" var="link">
            <li><a href="${link.url}">${link.title}</a><span>&nbsp;-&nbsp;</span>${link.description}</li>
        </g:each>
        </ul>
    </g:if>

    <g:if test="${options.bhllinks && profile.bhl}">
        <h2>Biodiversity Heritage Library References</h2>
        <g:each in="${profile.bhl}" var="link">
            <p>
            <b>Title: </b> ${link.title}<br/>
            <b>Description: </b> ${link.description}<br/>
            <b>BHL Title: </b> ${link.fullTitle}<br/>
            <b>Edition: </b> ${link.edition}<br/>
            <b>Publisher: </b> ${link.publisher}<br/>
            <b>DOI: </b> ${link.doi}<br/>
            </p>
        </g:each>
    </g:if>

    <g:if test="${options.specimens && specimens}">
        <h2>Specimens</h2>
        <g:each in="${specimens}" var="spec">
            <p>
                <b>Institution Name: </b> ${spec.institutionName}<br/>
                <b>Collection: </b> ${spec.collectionName}<br/>
                <b>Catalog Number: </b> ${spec.catalogNumber}<br/>
            </p>
        </g:each>
    </g:if>

    <g:if test="${options.images && images}">
        <h2>Images</h2>
        <g:each in="${images}" var="image">
            <g:if test="${!image.excluded}">
                <img src="${image.largeImageUrl}"/>

                <div class="meta">${image.dataResourceName}</div>
            </g:if>
        </g:each>
    </g:if>

    <g:if test="${options.bibliography && profile.bibliography}">
        <h2>Bibliography</h2>
        <ul>
            <g:each in="${profile.bibliography}" var="bib">
                <li>${raw(bib.text)}</li>
            </g:each>
        </ul>
    </g:if>

    <g:each in="${profile.authorship?.findAll { it.category != 'Author' }}" var="authorship">
        <div>
            <h3>${authorship.category}</h3>

            <p>${authorship.text}</p>
        </div>
    </g:each>
</div>

<r:layoutResources/>
</body>

</html>