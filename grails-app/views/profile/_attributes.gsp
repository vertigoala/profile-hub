<div ng-controller="AttributeEditor as attrCtrl" ng-init="attrCtrl.init('${edit}')" class="ng-cloak" ng-cloak>
    <div class="ng-show" ng-show="!attrCtrl.readonly">
        <button ng-click="attrCtrl.addAttribute()" class="btn"><i class="icon icon-plus"></i>Add attribute</button>
    </div>

    <div ng-repeat="attribute in attrCtrl.attributes" ng-form="AttributeForm">
        <div class="well attribute-edit" id="browse_attributes_edit" ng-show="!attrCtrl.readonly">
            <input type="text"
                   typeahead="attributeTitle.name for attributeTitle in attrCtrl.attributeTitles | filter: $viewValue"
                   class="form-control attribute-header-input" ng-model="attribute.title" name="title"
                   value="title" placeholder="Title..."/>
            <textarea class="field span12" rows="4" ng-model="attribute.text" name="text"
                      placeholder="Description..."></textarea>

            <div class="row-fluid">
                <span class="span4">
                    <button class="btn btn-link" ng-click="attrCtrl.showAudit($index)" ng-hide="attribute.auditShowing">
                        <span class="fa fa-angle-double-down"></span> Show history
                    </button>
                    <button class="btn btn-link" ng-click="attrCtrl.hideAudit($index)" ng-show="attribute.auditShowing">
                        <span class="fa fa-angle-double-up"></span> Hide history
                    </button>
                </span>
                <span class="span8">
                    <span class="info">{{ attribute.status }}</span>
                    <button class="btn btn-danger pull-right"
                            ng-click="attrCtrl.deleteAttribute($index)">Delete</button>
                    &nbsp;
                    <button class="btn btn pull-right" ng-click="attrCtrl.saveAttribute($index, AttributeForm)">
                        <span ng-show="!attrCtrl.saving" id="saved"><span
                                ng-show="AttributeForm.$dirty">*</span> Save</span>
                        <span ng-show="attrCtrl.saving" id="saving">Saving....</span>
                    </button>
                </span>
            </div>

            <div ng-show="attribute.auditShowing" class="attribute-audit" class="row-fluid">
                <span class="span4" ng-show="!attribute.audit || attribute.audit.length == 0">There have been no changes to this attribute.</span>

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
        </div>

        <div class="bs-docs-example" id="browse_attributes" data-content="{{ attribute.title }}"
             ng-show="attrCtrl.readonly">
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
</div>