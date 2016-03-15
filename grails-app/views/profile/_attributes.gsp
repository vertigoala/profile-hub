<div ng-controller="AttributeEditor as attrCtrl" ng-init="attrCtrl.init('${edit}')">
    <div class="row">
        <div class="col-md-12 padding-bottom-1" ng-cloak>
            <button ng-show="!attrCtrl.readonly" ng-click="attrCtrl.addAttribute()" class="btn btn-default"><i
                    class="fa fa-plus"></i>&nbsp;Add attribute
            </button>

            <div class="small pull-right ignore-save-warning"
                 ng-form="ShowSupportingAttributesForm" ng-show="(!attrCtrl.readonly && (attrCtrl.opus.showLinkedOpusAttributes || attrCtrl.opus.allowCopyFromLinkedOpus))">
                Show information from supporting collections:
                <div class="btn-group">
                    <label class="btn btn-xs" ng-class="attrCtrl.showSupportingData ? 'btn-success' : 'btn-default'"
                           ng-model="attrCtrl.showSupportingData" ng-change="attrCtrl.toggleShowSupportingData(ShowSupportingAttributesForm)"
                           btn-radio="true">On</label>
                    <label class="btn btn-xs" ng-class="attrCtrl.showSupportingData ? 'btn-default' : 'btn-danger'"
                           ng-model="attrCtrl.showSupportingData" ng-change="attrCtrl.toggleShowSupportingData(ShowSupportingAttributesForm)"
                           btn-radio="false">Off</label>
                </div>
            </div>
        </div>
    </div>

    <!-- view screen -->
    <ng-include src="'showReadOnlyAttributeList.html'" ng-show="attrCtrl.readonly"></ng-include>

    <!-- edit screen -->
    <ng-include src="'showEditableAttributeList.html'" ng-show="!attrCtrl.readonly"></ng-include>
</div>

<!-- template for the editable view of an attribute list -->
<script type="text/ng-template" id="showEditableAttributeList.html">
<div ng-repeat="attribute in attrCtrl.attributes" ng-form="AttributeForm" ng-show="!attrCtrl.readonly" ng-cloak>
    <a name="edit_{{attribute.key}}"></a>
    <ng-include src="'showEditableAttribute.html'" ng-if="!attribute.fromCollection"></ng-include>

    <div ng-if="attrCtrl.showAttribute(attribute)">
        <div class="panel panel-default">
            <div class="panel-body">
                <div class="row">
                    <div class="col-sm-2"><strong>{{attribute.title}}</strong></div>

                    <div class="col-sm-10">
                        <ng-include src="'readOnlyAttributeBody.html'"></ng-include>
                    </div>
                </div>

                <div class="panel-footer" ng-show="attrCtrl.opus.allowCopyFromLinkedOpus && !attrCtrl.readonly">
                    <div class="row">
                        <span class="col-md-12">
                            <span class="pull-right">
                                <button class="btn btn-default"
                                        ng-click="attrCtrl.copyAttribute($index, AttributeForm)">Copy to this profile</button>
                            </span>
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</script>

<!-- template for the read-only view of an attribute list -->
<script type="text/ng-template" id="showReadOnlyAttributeList.html">
<div ng-repeat="title in attrCtrl.attributeTitles" ng-cloak>
    <a name="view_{{title.key}}"></a>
    <div class="panel panel-default" ng-if="attrCtrl.showTitleGroup(title.name)">
        <div class="panel-heading">
            <div class="row">
                <div class="col-sm-12">
                    <h4 class="section-panel-heading">{{ title.name }}</h4>
                </div>
             </div>
        </div>
        <div class="panel-body">
            <div class="row">
                <div class="col-sm-12" ng-if="attrCtrl.showTitleGroup(title.name)">
                    <span ng-repeat="attribute in attrCtrl.attributes | groupAttributes:title.name">
                        <ng-include src="'readOnlyAttributeBody.html'"></ng-include>
                    </span>
                </div>
            </div>
        </div>
    </div>
</div>
</script>

<!-- template for the content of a single read-only attribute, to be displayed either on the view or the edit page -->
<script type="text/ng-template" id="readOnlyAttributeBody.html">
<span ng-show="attrCtrl.showAttribute(attribute)">
    <div ng-class="(!$first && attrCtrl.readonly) ? 'padding-top-1' : ''">
        <div ng-bind-html="attribute.text | sanitizeHtml" class="display-text"></div>

        <div ng-show="attrCtrl.opus.allowFineGrainedAttribution">
            <div class="citation" ng-show="attribute.creators.length > 0">
                Contributed by {{ attribute.creators.join(', ') }}
            </div>

            <div class="citation" ng-show="attribute.editors.length > 0">
                Edited by {{ attribute.editors.join(', ') }}
            </div>
        </div>
    </div>

    <div class="annotation" ng-show="(attrCtrl.readonly && !attribute.fromCollection && attrCtrl.opus.showLinkedOpusAttributes && $last) || attribute.fromCollection || attribute.source || attribute.original">
        <div class="col-md-6" ng-if="attribute.source || (attribute.original && !attribute.source)">
            <span ng-if="attribute.source">
                Source: {{attribute.source}}
            </span>
            <span ng-if="attribute.original && !attribute.source">
                Source: {{attribute.original.profile.opus.title}}
            </span>
        </div>

        <div class="col-md-6 pull-right" ng-show="attribute.fromCollection">
            <span class="pull-right">
                <span class="pull-right">
                    From Collection: <a
                        href="${request.contextPath}/opus/{{attribute.fromCollection.opusShortName ? attribute.fromCollection.opusShortName : attribute.fromCollection.opusId}}/profile/{{attribute.fromCollection.profileId}}"
                        target="_self">{{attribute.fromCollection.opusTitle}}</a>
                </span>
            </span>
        </div>
        <span class="col-md-6 pull-right" ng-show="attrCtrl.readonly && !attribute.fromCollection && attrCtrl.opus.showLinkedOpusAttributes && $last">
            <span class="pull-right">
                <a href=""
                   ng-click="attrCtrl.viewInOtherCollections(attribute.title)">Show {{attribute.title}} in other collections</a>
            </span>
        </span>
    </div>
</span>
</script>

<!-- Template for the editable view of a single attribute -->
<script type="text/ng-template" id="showEditableAttribute.html">

<div class="panel panel-default" id="browse_attributes_edit">
    <div class="panel-body">
        <label for="attributeTitle" class="screen-reader-label">Title</label>
        <select id="attributeTitle" ng-show="attrCtrl.vocabularyStrict"
                ng-model="attribute.title" class="form-control attribute-header-input margin-bottom-1">
            <option value="">--- Select one ---</option>
            <option ng-repeat="(key, value) in attrCtrl.allowedVocabulary | orderBy:'toString()'" value="{{value}}"
                    ng-selected="attribute.title == value">{{value}}</option>
        </select>
        <input type="text"
               autocomplete="off"
               required
               typeahead="attributeTitle for attributeTitle in attrCtrl.allowedVocabulary | filter: $viewValue"
               class="form-control attribute-header-input margin-bottom-1" ng-model="attribute.title" name="title"
               value="title" placeholder="Title..."
               ng-show="!attrCtrl.vocabularyStrict"/>
        <alert ng-show="attribute.title && !attrCtrl.isValid(attribute.title)"
               type="danger">You must select a value from the list of approved titles.</alert>

        <label for="attributeContent" class="screen-reader-label">Content</label>
        <textarea id="attributeContent" ng-model="attribute.text" name="attribute" ckeditor="richTextFullToolbar" required="required"></textarea>

        <div class="row"
             ng-if="attrCtrl.opus.allowFineGrainedAttribution && (attribute.uuid || attribute.original)">
            <div class="col-md-3">
                <label for="significantEdit{{$index}}" class="inline-label">
                    <input id="significantEdit{{$index}}" type="checkbox" name="significantEdit"
                           ng-model="attribute.significantEdit"
                           ng-false-value="false">
                    This is a significant edit
                </input>
                </label>
            </div>

            <div class="col-md-9" ng-if="attribute.significantEdit">
                <div class="form-inline">
                    <div class="form-group pull-right">
                        <label for="attributeTo" class="inline-label">Attribute To:</label>
                        <input id="attributeTo" type="text"
                               autocomplete="off" size="50"
                               class="form-control" ng-model="attribute.attributeTo" name="attributeTo"
                               value="attributeTo" placeholder="{{attrCtrl.currentUser}}"/>
                    </div>
                </div>
            </div>
        </div>

        <div class="row padding-top-1">
            <div class="col-md-12">
                <div class="form-inline">
                    <div class="form-group">
                        <label for="source" class="inline-label">Source:</label>
                        <input id="source" type="text"
                               autocomplete="off" size="50"
                               class="form-control" ng-model="attribute.source" name="source"
                               value="source" placeholder="Source..."/>
                    </div>
                </div>
            </div>
        </div>

        <div class="row">
            <span class="col-md-12">
                <button class="btn btn-link" ng-click="attrCtrl.showAudit($index)"
                        ng-hide="!attribute.uuid || attribute.auditShowing">
                    <span class="fa fa-angle-double-down"></span> Show history
                </button>
                <button class="btn btn-link" ng-click="attrCtrl.hideAudit($index)"
                        ng-show="attribute.uuid && attribute.auditShowing">
                    <span class="fa fa-angle-double-up"></span> Hide history
                </button>
            </span>
        </div>

        <div ng-show="attribute.auditShowing" class="attribute-audit" class="row">
            <span class="col-md-12"
                  ng-show="!attribute.audit || attribute.audit.length == 0">There have been no changes to this attribute.</span>

            <div class="table-responsive" style="margin-top:20px;" ng-show="attribute.audit.length > 0">
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

                            <div data-ng-bind-html="auditItem.diff | sanitizeHtml" ng-if="auditItem.diff"></div>

                            <div data-ng-bind-html="auditItem.object.plainText | sanitizeHtml" ng-if="!auditItem.diff"></div>
                        </td>
                        <td>{{ auditItem.userDisplayName }}</td>
                        <td>{{ auditItem.date | date:'dd/MM/yyyy h:mm a' }}</td>
                        <td><button class="btn btn-default" title="Revert to this version"
                                    ng-click="attrCtrl.revertAttribute($parent.$index, $index, AttributeForm)">Revert</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <div class="row-fluid vertical-pad" ng-show="attribute.source">
            <span class="col-md-12">
                <span class="pull-right">
                    <span class="blockquote small">
                        Source: {{attribute.source}}
                    </span>
                </span>
            </span>
        </div>
    </div>

    <div class="panel-footer" ng-show="!attrCtrl.readonly">
        <div class="row">
            <span class="col-md-12">
                <button class="btn btn-default" ng-click="attrCtrl.deleteAttribute($index)">Delete attribute</button>
                <save-button ng-click="attrCtrl.saveAttribute($index, AttributeForm)"
                             disabled="!AttributeForm.$dirty || !attrCtrl.isValid(attribute.title) || !attribute.text"
                             dirty="AttributeForm.$dirty || !attribute.uuid"
                             form="AttributeForm">
                </save-button>
            </span>
        </div>
    </div>
</div>

</script>

<!-- Template for the "Show in other collections" popup -->
<script type="text/ng-template" id="supportingCollections.html">
<div class="modal-header">
    <h4 class="modal-title">Usages in other collections</h4>
</div>

<div class="modal-body">
    <alert type="info"
           ng-show="!attrModalCtrl.supporting || attrModalCtrl.supporting.length == 0">This attribute is not used in the supporting collection(s).</alert>

    <div class="panel panel-default" ng-repeat="attribute in attrModalCtrl.supporting"
         data-content="{{ attribute.opusTitle }}" ng-show="attrModalCtrl.supporting.length > 0">
        <div class="panel-body">
            <div class="col-sm-2"><strong>{{attribute.opusTitle}}</strong></div>

            <div class="col-sm-10">
                <div ng-bind-html="attribute.text | sanitizeHtml" class="display-text"></div>
            </div>
        </div>
    </div>
</div>

<div class="modal-footer">
    <button class="btn btn-default" ng-click="attrModalCtrl.close()">Close</button>
</div>
</script>

