<div ng-controller="AttributeEditor" ng-init="init('${edit}')" class="ng-cloak" ng-cloak>
    <div class="ng-show" ng-show="!readonly">
        <button ng-click="addAttribute()" class="btn"><i class="icon icon-plus"></i>Add attribute</button>
        <button ng-click="addImage()" class="btn"><i class="icon icon-plus"></i>Add Image</button>
    </div>

    <div ng-show="messages.length">
        <alert ng-repeat="message in messages" type="{{message.type}}">{{message.msg}}</alert>
    </div>

    <div ng-repeat="attribute in attributes" ng-form="AttributeForm">
        <div class="well attribute-edit" id="browse_attributes_edit" ng-show="!readonly">
            <g:textField
                    typeahead="attributeTitle.name for attributeTitle in attributeTitles | filter: $viewValue"
                    class="form-control attribute-header-input" ng-model="attribute.title" name="title"
                    value="title" placeholder="Title..."/>
            <g:textArea class="field span12" rows="4" ng-model="attribute.text" name="text"
                        placeholder="Description..."/>

            <div class="row-fluid">
                <span class="span4">
                    <button class="btn" ng-click="showAudit($index)">Show history</button><br/>
                </span>
                <span class="span8">
                    <span class="info">{{ attribute.status }}</span>
                    <button class="btn btn-danger pull-right" ng-click="deleteAttribute($index)">Delete</button>
                    &nbsp;
                    <button class="btn btn pull-right" ng-click="saveAttribute($index, AttributeForm)">
                        <span ng-show="!attribute.saving" id="saved"><span
                                ng-show="AttributeForm.$dirty">*</span> Save</span>
                        <span ng-show="attribute.saving" id="saving">Saving....</span>
                    </button>
                </span>
            </div>

            <div ng-show="attribute.uuid != ''" class="attribute-audit">
                <div class="audit-history" style="margin-top:20px;">
                    <table class="table table-striped">
                        <thead ng-show="attribute.audit !== undefined && attribute.audit.length > 0">
                        <tr>Content</tr>
                        <tr>Editor</tr>
                        <tr>Date</tr>
                        <tr></tr>
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

        <div class="bs-docs-example" id="browse_attributes" data-content="{{ attribute.title }}" ng-show="readonly">
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