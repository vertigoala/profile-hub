<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${profile.scientificName} | ${profile.opusName}</title>
    <link rel="stylesheet" href="http://leafletjs.com/dist/leaflet.css"/>
    <script src="http://leafletjs.com/dist/leaflet.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.3/angular.min.js" type="text/javascript"></script>
    <script src="//angular-ui.github.io/bootstrap/ui-bootstrap-tpls-0.12.0.js"></script>

    <r:require module="profiles"/>
    <script>
        <g:render template="/scriptInit"/>
    </script>
</head>

<body onload="initialiseUrls()">

<div id="container" ng-app="profileEditor">
    <div class="pull-right" style="margin-top:20px;">

        <g:if test="${!edit}">
            <g:link class="btn btn" mapping="editProfile" params="[profileId: profile.uuid]"><i
                    class="icon-edit"></i>&nbsp;Edit</g:link>
        </g:if>
        <g:else>
            <button class="btn"
                    onclick="javascript:alert('Not implemented - through to users edits')">Logged in: ${currentUser}</button>
            <g:link class="btn" mapping="viewProfile" params="[profileId: profile.uuid]">Public view</g:link>
        </g:else>
        <a class="btn btn" href="${grailsApplication.config.profile.service.url}/profile/${profile.uuid}">JSON</a>
    </div>

    <h1>${profile.scientificName ?: 'empty'}</h1>

    <div class="row-fluid">

        <div class="span8">
            <div ng-controller="AttributeEditor"
                 ng-init="init('${opus.encodeAsJSON()}', '${profile.encodeAsJSON()}', '${edit}')">
                <div class="ng-show" ng-show="!readonly">
                    <button ng-click="addAttribute()" class="btn"><i class="icon icon-plus"></i>Add attribute</button>
                    <button ng-click="addImage()" class="btn"><i class="icon icon-plus"></i>Add Image</button>
                </div>

                <div ng-repeat="attribute in attributes" ng-form="AttributeForm">
                    <div class="well attribute-edit" id="browse_attributes_edit" ng-show="!readonly">
                        <g:textField
                                typeahead="attributeTitle.name for attributeTitle in attributeTitles | filter:  $viewValue"
                                class="form-control attribute-header-input" ng-model="attribute.title" name="title"
                                value="title"/>
                        <g:textArea class="field span12" rows="4" ng-model="attribute.text" name="text"/>

                        <div class="row-fluid">
                            <span class="span4">
                                <button class="btn" ng-click="showAudit($index)">Show history</button><br/>
                            </span>
                            <span class="span8">
                                <span class="info">{{ attribute.status }}</span>
                                <button class="btn btn-danger pull-right"
                                        ng-click="deleteAttribute($index)">Delete</button>
                                &nbsp;
                                <button class="btn btn pull-right" ng-click="saveAttribute($index, AttributeForm)">
                                    <span ng-show="!isSaving($index)" id="saved"><span
                                            ng-show="AttributeForm.$dirty">*</span> Save</span>
                                    <span ng-show="isSaving($index)" id="saving">Saving....</span>
                                </button>
                            </span>
                        </div>

                        <div ng-show="attribute.uuid != ''" class="attribute-audit">
                            <div class="audit-history" style="margin-top:20px;">
                                <table class="table table-striped">
                                    <thead ng-show="attribute.audit !== undefined && attribute.audit.length > 0">
                                    <th>Content</th>
                                    <th>Editor</th>
                                    <th>Date</th>
                                    <th></th>
                                    </thead>
                                    <tbody>
                                    <tr ng-repeat="auditItem in attribute.audit">
                                        <td>
                                            <b>{{ auditItem.object.title }}</b>
                                            <br/>
                                            {{ auditItem.object.text }}
                                        </td>
                                        <td>{{ auditItem.userDisplayName }}</td>
                                        <td>{{ auditItem.date }}</td>
                                        <td><button class="btn btn-mini" title="Revert to this version"
                                                    ng-click="revertAttribute($parent.$index, $index)">Revert</button>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="bs-docs-example" id="browse_attributes" data-content="{{ attribute.title }}"
                         ng-show="readonly">
                        <blockquote style="border-left:none;">
                            <p>{{ attribute.text }}</p>
                            <small>
                                Contributed by
                                <cite title="Contributors to this text">
                                    {{ attribute.creators.join(', ') }}
                                </cite>
                            </small>
                            <span ng-show="attribute.editors.length > 0">
                                <small>
                                    Edited by
                                    <cite title="Editors to this text">
                                        {{ attribute.editors.join(', ') }}
                                    </cite>
                                </small>
                            </span>
                        </blockquote>
                    </div>
                </div>

                <g:if test="${profile.links && !edit}">
                    <div class="bs-docs-example" id="browse_links" data-content="Links">
                        <ul>
                            <g:each in="${profile.links}" var="link">
                                <li><a href="${link.url}">${link.title}</a>${link.description ? ' - ' + link.description : ''}
                                </li>
                            </g:each>
                        </ul>
                        <g:if test="${edit}">
                            <a class="btn" href="javascript:alert('not implemented yet')"><i
                                    class="icon icon-plus"></i> Add link</a>
                        </g:if>
                    </div>
                </g:if>
                <g:elseif test="${edit}">
                    <div ng-controller="LinksEditor" class="bs-docs-example" id="browse_links" data-content="Links">
                        <div style="margin-bottom: 10px;">
                            <button class="btn" ng-click="saveLinks()">Save changes</button>
                            <button class="btn" ng-click="addLink()"><i class="icon icon-plus"></i> Add new link
                            </button>
                        </div>
                        <table class="table table-striped">
                            <tr ng-repeat="link in links">
                                <td>
                                    <label>URL</label>
                                    <input type="text" class="input-xxlarge" value="{{link.url}}"/><br/>
                                    <label>Title</label>
                                    <input type="text" class="input-xxlarge" value="{{link.title}}"/><br/>
                                    <label>Description</label>
                                    <textarea rows="3" class="input-xxlarge">{{link.description}}</textarea>
                                </td>
                                <td><button class="btn" ng-click="deleteLink($index)"><i
                                        class="icon icon-minus"></i> Remove</button></td>
                            </tr>
                        </table>
                    </div>
                </g:elseif>

                <g:if test="${edit}">
                    <div ng-controller="BHLLinksEditor" class="bs-docs-example" id="browse_bhllinks"
                         data-content="Biodiversity Heritage Library references">

                        <p class="lead">
                            Add links to the biodiversity heritage library. Links should be of the form:
                            <b>http://biodiversitylibrary.org/page/29003916</b>
                        </p>

                        <div style="margin-bottom: 10px;">
                            <button class="btn" ng-click="saveLinks()">Save changes</button>
                            <button class="btn" ng-click="addLink()"><i class="icon icon-plus"></i> Add new reference
                            </button>
                        </div>

                        <table class="table table-striped">
                            <tr ng-repeat="link in bhl">
                                <td>
                                    <input type="hidden" name="link.uuid" value="{{link.uuid}}"/>
                                    <label>URL</label>
                                    <input type="text" class="input-xxlarge" ng-model="link.url" value="{{link.url}}"
                                           ng-change="updateThumbnail($index)"/><br/>
                                    <label>Title</label>
                                    <input type="text" class="input-xxlarge" ng-model="link.title"
                                           value="{{link.title}}"/><br/>
                                    <label>Description</label>
                                    <textarea rows="3" class="input-xxlarge"
                                              ng-model="link.description">{{link.description}}</textarea>
                                    <br/>
                                    <cite ng-show="hasThumbnail($index)">
                                        <span><b>BHL metadata</b></span><br/>
                                        <span>
                                            Title: {{link.fullTitle}}
                                        </span>
                                        <br/>
                                        <span>
                                            Edition: {{link.edition}}
                                        </span>
                                        <br/>
                                        <span>
                                            Publisher: {{link.publisherName}}
                                        </span>
                                        <br/>
                                        <span>
                                            DOI: <a href="http://dx.doi.org/{{link.bhlDoi}}">{{link.doi}}</a>
                                        </span>
                                    </cite>
                                </td>
                                <td>
                                    <button class="btn" ng-click="deleteLink($index)"><i
                                            class="icon icon-minus"></i> Remove</button>
                                    <br/>

                                    <div ng-show="hasThumbnail($index)">
                                        <a href="{{link.url}}" target="_blank">
                                            <img
                                                    style="margin-top:20px; max-height:200px;"
                                                    ng-src="{{link.thumbnail}}"
                                                    alt="{{link.title}}"
                                                    class="img-rounded"/>
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>
                </g:if>
                <g:elseif test="${profile.bhl}">
                    <div ng-controller="BHLLinksEditor" class="bs-docs-example" id="browse_bhllinks"
                         data-content="Biodiversity Heritage Library references">
                        <table class="table">
                            <tr ng-repeat="link in bhl">
                                <td>
                                    <h4 ng-show="link.title != ''" style="margin-bottom: 0; padding-bottom:0;">
                                        Title: {{link.title}}
                                    </h4>
                                    <span ng-show="link.description != ''">
                                        Description: {{link.description}}
                                    </span>
                                    <cite ng-show="hasThumbnail($index)">
                                        <span>
                                            BHL title: {{link.fullTitle}}
                                        </span>
                                        <br/>
                                        <span>
                                            Edition: {{link.edition}}
                                        </span>
                                        <br/>
                                        <span>
                                            Publisher: {{link.publisherName}}
                                        </span>
                                        <br/>
                                        <span>
                                            DOI: <a href="http://dx.doi.org/{{link.bhlDoi}}">{{link.doi}}</a>
                                        </span>
                                    </cite>
                                </td>
                                <td>
                                    <div ng-show="hasThumbnail($index)">
                                        <a href="{{link.url}}" target="_blank">
                                            <img ng-model="link.thumbnail" src="{{link.thumbnail}}"
                                                 style="max-height:150px;" alt="{{link.title}}" class="img-rounded"/>
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>
                </g:elseif>

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

                <div class="bs-docs-example hide" id="browse_images" data-content="Images">
                </div>
            </div>
        </div>

        <div class="span4">
            <div id="map" style="height: 400px; margin-top:10px;"></div>
            <a class="btn"
               href="${opus.biocacheUrl}/occurrences/search?q=${occurrenceQuery}">View in ${opus.biocacheName}</a>

            <div id="firstImage" class="hide" style="margin-top:15px;"></div>

            <div class="bs-docs-example hide" id="browse_lists" data-content="Conservation & sensitivity lists">
                <ul></ul>
            </div>
        </div>

        <r:script>
            <g:render template="/scriptInit"/>

            <g:applyCodec encodeAs="none">
                $(function() {
                    profiles.addTaxonMap('${opus.encodeAsJSON()}', '${profile.encodeAsJSON()}', '${occurrenceQuery}');
                    profiles.addImages('${imagesQuery}');
                    profiles.addLists('${profile.encodeAsJSON()}');
                });
            </g:applyCodec>
        </r:script>

</body>

</html>