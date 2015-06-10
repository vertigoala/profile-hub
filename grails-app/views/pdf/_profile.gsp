<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <link rel="stylesheet" href="${resource(dir: '/thirdparty/bootstrap/css/bootstrap-3.1.1.min.css', absolute: true,)}"
          type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/nsl.css', absolute: true)}" type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/profiles.css', absolute: true)}" type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/pdf.css', absolute: true)}" type="text/css"/>
    <style type="text/css">
    .customizable-banner {
        background-image: url(${opus.bannerUrl ?: grailsApplication.config.images.service.url + '/store/7/4/4/e/a08a52f2-7bbe-40d9-8f1a-fe8acb28e447/original'});
    }
    </style>
</head>

<body>
<div class="container-fluid">
    <div class="customizable-banner">
        <div class="row-fluid span12">
            <div class="span6">
                <div class="customizable-logo pull-left">
                    <img class="customizable-logo-img"
                         src="${opus.logoUrl ?: grailsApplication.config.ala.base.url + '/wp-content/themes/ala2011/images/logo.png'}"/>
                </div>
            </div>

        </div>
    </div>

    <div class="row-fluid">
        <h1><span class="scientific-name">${profile.scientificName}</span> <span
                class="inline-sub-heading">${profile.nameAuthor}</span></h1>
    </div>

    <div>
        <i>${profile.authorship?.find { it.category == 'Author' }?.text ?: ""}</i>
    </div>

    <g:if test="${options.attributes}">
        <g:each in="${profile.attributes.sort { it.order }}" var="attribute">
            <div>
                <h4>${attribute.title}</h4>
                <blockquote>
                    <p class="display-text">${raw(attribute.text)}</p>
                    <g:if test="${opus.allowFineGrainedAttribution}">
                        <span class="small">
                            Contributed by
                            <cite title="Contributors to this text">
                                ${attribute.creators.join(', ')}
                            </cite>
                        </span>
                        <g:if test="${attribute.editors}">
                            <span class="small">
                                Edited by
                                <cite title="Editors to this text">
                                    ${attribute.editors.join(', ')}
                                </cite>
                            </span>
                        </g:if>
                    </g:if>
                </blockquote>
            </div>
        </g:each>
    </g:if>

    <g:if test="${options.map}">
        <h4>Occurrences</h4>
        <img src="${profile.mapImageUrl}"/>
    </g:if>

    <g:if test="${options.conservation && profile.speciesProfile?.conservationStatuses}">
        <h4>Conservation Status</h4>
        <ul>
            <g:each in="${profile.speciesProfile.conservationStatuses.sort({ it.region })}" var="status">
                <li><span class="status ${status.colour}">${status.regionAbbrev ?: 'IUCN'}</span> ${status.rawStatus}
                </li>
            </g:each>
        </ul>
    </g:if>

    <g:if test="${options.taxonomy && profile.classifications}">
        <div>
            <h4>Taxonomy from ${profile.speciesProfile?.taxonConcept?.infoSourceName}</h4>
            <ul>
                <g:each in="${profile.classifications}" var="classification">
                    <li>${classification.rank.capitalize()}: ${classification.scientificName}</li>
                </g:each>
            </ul>
        </div>
    </g:if>

    <g:if test="${options.nomenclature && profile.nslNameIdentifier && profile.nomenclatureHtml}">
        <h4>Nomenclature</h4>
        ${raw(profile.nomenclatureHtml)}
    </g:if>

    <g:if test="${options.links && profile.links}">
        <h4>Links</h4>
        <ul>
            <g:each in="${profile.links}" var="link">
                <li><a href="${link.url}">${link.title}</a><span>&nbsp;-&nbsp;</span>${link.description}</li>
            </g:each>
        </ul>
    </g:if>

    <g:if test="${options.bhllinks && profile.bhl}">
        <h4>Biodiversity Heritage Library References</h4>
        <g:each in="${profile.bhl}" var="link">
            <p>
                <b>Title:</b> ${link.title}<br/>
                <b>Description:</b> ${link.description}<br/>
                <b>BHL Title:</b> ${link.fullTitle}<br/>
                <b>Edition:</b> ${link.edition}<br/>
                <b>Publisher:</b> ${link.publisher}<br/>
                <b>DOI:</b> ${link.doi}<br/>
            </p>
        </g:each>
    </g:if>

    <g:if test="${options.specimens && profile.specimens}">
        <h4>Specimens</h4>
        <g:each in="${profile.specimens}" var="spec">
            <p>
                <b>Institution Name:</b> ${spec.institutionName}<br/>
                <b>Collection:</b> ${spec.collectionName}<br/>
                <b>Catalog Number:</b> ${spec.catalogNumber}<br/>
            </p>
        </g:each>
    </g:if>

    <g:if test="${options.images && profile.images}">
        <h4>Images</h4>
        <div class="row">
            <g:each in="${profile.images}" var="image">
                <g:if test="${!image.excluded}">
                    <span class="col-lg-3">
                        <img class="profile-image" src="${image.largeImageUrl}"/>
                    </span>
                </g:if>
            </g:each>
        </div>
    </g:if>

    <g:if test="${options.bibliography && profile.bibliography}">
        <h4>Bibliography</h4>
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

    <g:if test="${options.children}">
        <ul>
            <g:each in="${profile.children}" var="child">
                <li>${child.scientificName}</li>
            </g:each>
        </ul>
    </g:if>
</div>

<r:layoutResources/>
</body>

</html>