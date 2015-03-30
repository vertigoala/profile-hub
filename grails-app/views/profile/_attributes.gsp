<div ng-controller="AttributeEditor as attrCtrl" ng-init="attrCtrl.init('${edit}')">
    <div class="ng-show" ng-show="!attrCtrl.readonly" ng-cloak>
        <button ng-click="attrCtrl.addAttribute()" class="btn btn-info"><i class="icon icon-plus"></i>Add attribute
        </button>
    </div>

    <div ng-repeat="attribute in attrCtrl.attributes" ng-form="AttributeForm">
        <div class="well attribute-edit" id="browse_attributes_edit" ng-if="!attrCtrl.readonly && !attribute.source" ng-cloak>
            <input type="text"
                   autocomplete="off"
                   typeahead="attributeTitle for attributeTitle in attrCtrl.attributeTitles | filter: $viewValue"
                   class="form-control attribute-header-input" ng-model="attribute.title" name="title"
                   value="title" placeholder="Title..."/>
            <alert ng-show="attribute.title && !attrCtrl.isValid(attribute.title)"
                   type="danger">You must select a value from the list of approved titles.</alert>
            <textarea class="field span12" rows="4" ng-model="attribute.text" name="text"
                      placeholder="Description..."></textarea>

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
                                ng-disabled="!attrCtrl.isValid(attribute.title)">
                            <span ng-show="!attrCtrl.saving" id="saved"><span
                                    ng-show="AttributeForm.$dirty">*</span> Save</span>
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
                                {{ auditItem.object.text }}
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

            <div class="row-fluid vertical-pad" ng-show="!attrCtrl.readonly && attribute.original">
                <span class="span12">
                    <span class="pull-right">
                        <span class="blockquote small">
                            Originally copied from: <a
                                href="${request.contextPath}/opus/{{attrCtrl.opusId}}/profile/{{attribute.original.profile.uuid}}" target="_self">{{attribute.original.profile.opus.title}}</a>
                        </span>
                    </span>
                </span>
            </div>
        </div>

        <div class="bs-docs-example" id="browse_attributes" data-content="{{ attribute.title }}"
             ng-if="attribute.source || attrCtrl.readonly" ng-cloak>
            <blockquote style="border-left:none;">
                <p class="display-text">{{ attribute.text }}</p>
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

            <div class="row-fluid">
                <span class="span12">
                    <span class="pull-right">
                        <span class="small pull-right" ng-show="attribute.source">
                            Source: <a
                                href="${request.contextPath}/opus/{{attrCtrl.opusId}}/profile/{{attribute.source.profileId}}" target="_self">{{attribute.source.opusTitle}}</a>
                        </span>
                        <span class="small" ng-show="attrCtrl.readonly && !attribute.source && attrCtrl.opus.showLinkedOpusAttributes">
                           <a href="#" ng-click="attrCtrl.viewInOtherCollections($index)">Show {{attribute.title}} in other collections</a>
                        </span>
                    </span>
                </span>
            </div>
            <div class="row-fluid" ng-show="attrCtrl.opus.allowCopyFromLinkedOpus && !attrCtrl.readonly">
                <span class="span12">
                    <span class="pull-right">
                        <button class="btn btn-info" ng-click="attrCtrl.copyAttribute($index, AttributeForm)">Copy to this profile</button>
                    </span>
                </span>
            </div>
        </div>
    </div>
</div>

<script type="text/ng-template" id="supportingCollections.html">
<div class="modal-header">
    <h3 class="modal-title">Usages in other collections</h3>
</div>

<div class="modal-body">
    <alert type="info" ng-show="!attrModalCtrl.supporting || attrModalCtrl.supporting.length == 0">This attribute is not used in the supporting collection(s).</alert>

    <div class="bs-docs-example" ng-repeat="attribute in attrModalCtrl.supporting" data-content="{{ attribute.opusTitle }}" ng-show="attrModalCtrl.supporting.length > 0">
        <blockquote style="border-left:none;">
            <p class="display-text">{{ attribute.text }}</p>
        </blockquote>
    </div>
</div>

<div class="modal-footer">
    <button class="btn btn-primary" ng-click="attrModalCtrl.close()">Close</button>
</div>
</script>