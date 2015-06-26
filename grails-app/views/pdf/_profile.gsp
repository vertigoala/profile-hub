<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <link rel="stylesheet" href="${resource(dir: '/thirdparty/bootstrap/css/bootstrap3.3.4.min.css', absolute: true,)}"
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
<div class="container">
    <div class="customizable-banner">
        <div class="row">
            <div class="col-md-6">
                <div class="customizable-logo pull-left">
                    <img class="customizable-logo-img"
                         src="${opus.logoUrl ?: grailsApplication.config.ala.base.url + '/wp-content/themes/ala2011/images/logo.png'}"/>
                </div>
            </div>

        </div>
    </div>

    <g:each in="${profiles}" var="record" status="index">
        <g:if test="${index != 0}">
            <div class="page-break" style="page-break-before: always"></div>
        </g:if>
        <div class="row">
            <h1><span class="scientific-name">${record.profile.scientificName}</span> <g:if test="${record.profile.nameAuthor}"><span
                    class="inline-sub-heading">${record.profile.nameAuthor}</span></g:if></h1>
        </div>

        <div>
            <i>${record.profile.authorship?.find { it.category == 'Author' }?.text ?: ""}</i>
        </div>

        <g:if test="${options.attributes}">
            <g:each in="${record.profile.attributes.sort { it.order }}" var="attribute">
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
            <img src="${record.profile.mapImageUrl}"/>
        </g:if>

        <g:if test="${options.conservation && record.profile.speciesProfile?.conservationStatuses}">
            <h4>Conservation Status</h4>
            <ul>
                <g:each in="${record.profile.speciesProfile.conservationStatuses.sort({ it.region })}" var="status">
                    <li><span class="status ${status.colour}">${status.regionAbbrev ?: 'IUCN'}</span> ${status.rawStatus}
                    </li>
                </g:each>
            </ul>
        </g:if>

        <g:if test="${options.taxonomy && record.profile.classifications}">
            <div>
                <h4>Taxonomy</h4>
                <ul>
                    <g:each in="${record.profile.classifications}" var="classification">
                        <li>${classification.rank.capitalize()}: ${classification.scientificName}</li>
                    </g:each>
                </ul>
            </div>
        </g:if>

        <g:if test="${options.nomenclature && record.profile.nslNameIdentifier && record.profile.nomenclatureHtml}">
            <h4>Nomenclature</h4>
            ${raw(record.profile.nomenclatureHtml)}
        </g:if>

        <g:if test="${options.links && record.profile.links}">
            <h4>Links</h4>
            <ul>
                <g:each in="${record.profile.links}" var="link">
                    <li><a href="${link.url}">${link.title}</a><span>&nbsp;-&nbsp;</span>${link.description}</li>
                </g:each>
            </ul>
        </g:if>

        <g:if test="${options.bhllinks && record.profile.bhl}">
            <h4>Biodiversity Heritage Library References</h4>
            <g:each in="${record.profile.bhl}" var="link">
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

        <g:if test="${options.specimens && record.profile.specimens}">
            <h4>Specimens</h4>
            <g:each in="${record.profile.specimens}" var="spec">
                <p>
                    <b>Institution Name:</b> ${spec.institutionName}<br/>
                    <b>Collection:</b> ${spec.collectionName}<br/>
                    <b>Catalog Number:</b> ${spec.catalogNumber}<br/>
                </p>
            </g:each>
        </g:if>

        <g:if test="${options.images && record.profile.images}">
            <h4>Images</h4>
            <div class="row">
                <g:each in="${record.profile.images}" var="image">
                    <g:if test="${!image.excluded}">
                        <span class="col-lg-3">
                            <img class="profile-image" src="${image.largeImageUrl}"/>
                        </span>
                    </g:if>
                </g:each>
            </div>
        </g:if>

        <g:if test="${options.bibliography && record.profile.bibliography}">
            <h4>Bibliography</h4>
            <ul>
                <g:each in="${record.profile.bibliography}" var="bib">
                    <li>${raw(bib.text)}</li>
                </g:each>
            </ul>
        </g:if>

        <g:each in="${record.profile.authorship?.findAll { it.category != 'Author' }}" var="authorship">
            <div>
                <h3>${authorship.category}</h3>

                <p>${authorship.text}</p>
            </div>
        </g:each>
    </g:each>

</div>

<r:layoutResources/>
</body>

</html>