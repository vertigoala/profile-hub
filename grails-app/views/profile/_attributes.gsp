<div ng-controller="AttributeEditor as attrCtrl" ng-init="attrCtrl.init('${edit}')">
    <div class="row-fluid">
        <div class="span4" ng-cloak>
            <button ng-show="!attrCtrl.readonly" ng-click="attrCtrl.addAttribute()" class="btn btn-info"><i
                    class="icon icon-plus icon-white"></i>Add attribute
            </button>
        </div>

        <div class="span8">
            <div class="small pull-right" style="padding-top: 10px"
                 ng-show="(attrCtrl.readonly && attrCtrl.opus.showLinkedOpusAttributes) || (!attrCtrl.readonly && attrCtrl.opus.allowCopyFromLinkedOpus)">
                Show information from supporting collections:
                <div class="btn-group">
                    <label class="btn btn-mini" ng-class="attrCtrl.showSupportingData ? 'btn-success' : ''"
                           ng-model="attrCtrl.showSupportingData" btn-radio="true">On</label>
                    <label class="btn btn-mini" ng-class="attrCtrl.showSupportingData ? '' : 'btn-danger'"
                           ng-model="attrCtrl.showSupportingData" btn-radio="false">Off</label>
                </div>
            </div>
        </div>
    </div>

    <!-- edit screen -->
    <div ng-repeat="attribute in attrCtrl.attributes" ng-form="AttributeForm" ng-show="!attrCtrl.readonly" ng-cloak>
        <ng-include src="'showEditableAttribute.html'" ng-if="!attribute.source"></ng-include>
        <div class="bs-docs-example" data-content="{{ attribute.title }}" ng-if="attrCtrl.showAttribute(attribute)">
            <ng-include src="'showReadOnlyAttribute.html'"></ng-include>
        </div>
    </div>

    <!-- view screen -->
    <div ng-repeat="title in attrCtrl.attributeTitles" ng-show="attrCtrl.readonly" ng-cloak>
        <div class="bs-docs-example" data-content="{{ title.name }}" ng-if="attrCtrl.showTitleGroup(title.name)">
            <div ng-repeat="attribute in attrCtrl.attributes | groupAttributes:title.name">
                <div ng-if="attrCtrl.showAttribute(attribute)">
                    <ng-include src="'showReadOnlyAttribute.html'"></ng-include>
                </div>
                <hr ng-if="!$last"/>
            </div>
        </div>
    </div>
</div>

<!-- template for the read-only view of a single attribute -->
<script type="text/ng-template" id="showReadOnlyAttribute.html">

<blockquote style="border-left:none;">
    <p class="display-text">
        <div ta-bind ng-model="attribute.text"></div>
    </p>
    <div ng-if="attrCtrl.opus.allowFineGrainedAttribution">
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
    </div>
</blockquote>

<div class="row-fluid" ng-show="attribute.source || (attrCtrl.readonly && !attribute.source && attrCtrl.opus.showLinkedOpusAttributes && $last) || attribute.original">
    <span class="span12">
        <span class="pull-right">
            <span class="small pull-right" ng-show="attribute.source">
                Source: <a
                    href="${request.contextPath}/opus/{{attribute.source.opusShortName ? attribute.source.opusShortName : attribute.source.opusId}}/profile/{{attribute.source.profileId}}"
                    target="_self">{{attribute.source.opusTitle}}</a>
            </span>
            <span class="small" ng-if="attribute.original">
                Originally copied from: <a
                    href="${request.contextPath}/opus/{{attribute.original.profile.opus.shortName ? attribute.original.profile.opus.shortName : attribute.original.profile.opus.uuid}}/profile/{{attribute.original.profile.uuid}}"
                    target="_self">{{attribute.original.profile.opus.title}}</a>
                <br/>
            </span>
            <span class="small"
                  ng-show="attrCtrl.readonly && !attribute.source && attrCtrl.opus.showLinkedOpusAttributes && $last">
                <button class="btn btn-link"
                   ng-click="attrCtrl.viewInOtherCollections(attribute.title)">Show {{attribute.title}} in other collections</button>
            </span>
        </span>
    </span>
</div>

<div class="row-fluid" ng-show="attrCtrl.opus.allowCopyFromLinkedOpus && !attrCtrl.readonly">
    <span class="span12">
        <span class="pull-right">
            <button class="btn btn-info"
                    ng-click="attrCtrl.copyAttribute($index, AttributeForm)">Copy to this profile</button>
        </span>
    </span>
</div>
</script>

<!-- Template for the editable view of a single attribute -->
<script type="text/ng-template" id="showEditableAttribute.html">

<div class="well attribute-edit" id="browse_attributes_edit">
    <input type="text"
           autocomplete="off"
           required
           typeahead="attributeTitle for attributeTitle in attrCtrl.allowedVocabulary | filter: $viewValue"
           class="form-control attribute-header-input" ng-model="attribute.title" name="title"
           value="title" placeholder="Title..."/>
    <alert ng-show="attribute.title && !attrCtrl.isValid(attribute.title)"
           type="danger">You must select a value from the list of approved titles.</alert>

    <div text-angular text-angular-name="attribute" ng-model="attribute.text" ta-toolbar="{{richTextToolbarFull}}"></div>

    <label for="significantEdit" class="inline-label" ng-show="attribute.uuid && attrCtrl.opus.allowFineGrainedAttribution">
        <input id="significantEdit" type="checkbox" name="significantEdit" ng-model="attribute.significantEdit"
               ng-false-value="false">
        This is a significant edit
    </input>
    </label>

    <div class="row-fluid">
        <span class="span4">
            <button class="btn btn-link" ng-click="attrCtrl.showAudit($index)"
                    ng-hide="!attribute.uuid || attribute.auditShowing">
                <span class="fa fa-angle-double-down"></span> Show history
            </button>
            <button class="btn btn-link" ng-click="attrCtrl.hideAudit($index)"
                    ng-show="attribute.uuid && attribute.auditShowing">
                <span class="fa fa-angle-double-up"></span> Hide history
            </button>
        </span>
        <span class="span8">
            <span class="pull-right">
                <button class="btn btn-primary" ng-click="attrCtrl.saveAttribute($index, AttributeForm)"
                        ng-disabled="!attrCtrl.isValid(attribute.title) || !attribute.text">
                    <span ng-show="!attrCtrl.saving" id="saved"><span
                            ng-show="AttributeForm.$dirty || !attribute.uuid">*</span> Save</span>
                    <span ng-show="attrCtrl.saving" id="saving">Saving....</span>
                </button>
                <button class="btn btn-danger" ng-click="attrCtrl.deleteAttribute($index)">Delete</button>
            </span>
        </span>
    </div>

    <div ng-show="attribute.auditShowing" class="attribute-audit" class="row-fluid">
        <span class="span4"
              ng-show="!attribute.audit || attribute.audit.length == 0">There have been no changes to this attribute.</span>

        <div class="audit-history" style="margin-top:20px;" ng-show="attribute.audit.length > 0">
            <table class="table table-striped">
                <thead>
                <td>Content</td>
                <td>Editor</td>
                <td>Date</td>
                <td></td>
                </thead>
                <tbody>
                <tr ng-repeat="auditItem in attribute.audit">
                    <td>
                        <b>{{ auditItem.object.title }}</b>
                        <br/>
                        <div data-ng-bind-html="auditItem.diff" ng-if="auditItem.diff"></div>
                        <div data-ng-bind-html="auditItem.object.plainText" ng-if="!auditItem.diff"></div>
                    </td>
                    <td>{{ auditItem.userDisplayName }}</td>
                    <td>{{ auditItem.date }}</td>
                    <td><button class="btn btn-mini" title="Revert to this version"
                                ng-click="attrCtrl.revertAttribute($parent.$index, $index, AttributeForm)">Revert</button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </div>

    <div class="row-fluid vertical-pad" ng-show="attribute.original">
        <span class="span12">
            <span class="pull-right">
                <span class="blockquote small">
                    Originally copied from: <a
                        href="${request.contextPath}/opus/{{attrCtrl.opusId}}/profile/{{attribute.original.profile.uuid}}"
                        target="_self">{{attribute.original.profile.opus.title}}</a>
                </span>
            </span>
        </span>
    </div>
</div>

</script>

<!-- Template for the "Show in other collections" popup -->
<script type="text/ng-template" id="supportingCollections.html">
<div class="modal-header">
    <h3 class="modal-title">Usages in other collections</h3>
</div>

<div class="modal-body">
    <alert type="info"
           ng-show="!attrModalCtrl.supporting || attrModalCtrl.supporting.length == 0">This attribute is not used in the supporting collection(s).</alert>

    <div class="bs-docs-example" ng-repeat="attribute in attrModalCtrl.supporting"
         data-content="{{ attribute.opusTitle }}" ng-show="attrModalCtrl.supporting.length > 0">
        <blockquote style="border-left:none;">
            <p class="display-text">
            <div ta-bind ng-model="attribute.text"></div></p>
        </blockquote>
    </div>
</div>

<div class="modal-footer">
    <button class="btn btn-primary" ng-click="attrModalCtrl.close()">Close</button>
</div>
</script>

