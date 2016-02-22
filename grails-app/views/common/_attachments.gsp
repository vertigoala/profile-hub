<div class="padding-top-1" ng-controller="AttachmentController as attachmentCtrl" ng-cloak>
    <div class="padding-top-1 padding-bottom-1" ng-repeat="attachment in attachmentCtrl.attachments">
        <div class="col-md-3">
            <a ng-href="{{attachment.downloadUrl}}" target="_blank"><span class="fa fa-file-pdf-o padding-right-1"></span>{{ attachment.title }}</a>
        </div>

        <div class="col-md-6">
            {{ attachment.description }}
            <div class="citation" ng-if="attachment.creator || attachment.rightsHolder">
                <span ng-if="attachment.creator">{{ attachment.creator }}<span
                        ng-if="attachment.createdDate">, {{ attachment.createdDate | date: 'dd/MM/yyyy' }}</span>
                </span>
                <span ng-if="attachment.rightsHolder">(&copy; {{ attachment.rightsHolder }})</span>

            </div>
        </div>
        <div class="col-md-3">
            <div class="pull-right">
                <a ng-href="{{attachment.downloadUrl}}" target="_blank"><span
                        class="fa fa-download color--green">&nbsp;Download</span></a>
                <span class="padding-left-1"><img src="" ng-src="{{attachment.licenceIcon}}" alt="{{attachment.licence}}" title="{{attachment.licence}}"></span>
            </div>
            <g:if test="${profile ? (edit ? params.isOpusEditor : false) : params.isOpusAdmin}">
                <div class="pull-right padding-top-1">
                    <a href="" ng-click="attachmentCtrl.editAttachment(attachment)" class="padding-left-1"><span
                            class="fa fa-edit">&nbsp;Edit</span></a>
                    <a href="" ng-click="attachmentCtrl.deleteAttachment(attachment.uuid)" class="padding-left-1"><span
                        class="fa fa-trash-o color--red">&nbsp;Delete</span></a>
                </div>
            </g:if>
        </div>
        <hr class="col-md-12"/>
    </div>

    <g:if test="${profile ? (edit ? params.isOpusEditor : false) : params.isOpusAdmin}">
        <div class="row padding-top-1">
            <div class="col-md-12">
                <button class="btn btn-default pull-right" ng-click="attachmentCtrl.uploadAttachment()"><i
                        class="fa fa-plus"></i>&nbsp;Add document</button>
            </div>
        </div>
    </g:if>

    <script type="text/ng-template" id="attachmentUpload.html">
    <div ng-form="UploadForm">
        <div class="modal-header">
            <h4 class="modal-title">Upload document</h4>
        </div>

        <div class="modal-body">
            <alert class="alert-danger" ng-show="attachmentUploadCtrl.error">{{ attachmentUploadCtrl.error }}</alert>

            <div class="form-horizontal">
                <div class="form-group">
                    <label for="file" class="col-sm-3 control-label">Document (pdf) *</label>

                    <div class="col-sm-9" ng-show="!attachmentUploadCtrl.metadata.uuid">
                        <input id="file" type="file" ngf-select="" ng-model="attachmentUploadCtrl.files" name="file"
                               accept="application/pdf" required="{{!attachmentUploadCtrl.metadata.uuid}}" ng-required="!attachmentUploadCtrl.metadata.uuid">
                    </div>
                    <div class="col-sm-9 margin-top-1" ng-show="attachmentUploadCtrl.metadata.uuid">
                        <span>{{ attachmentUploadCtrl.metadata.filename }}</span>
                    </div>
                </div>
            </div>

            <div class="form-horizontal">
                <div class="form-group">
                    <label for="title" class="col-sm-3 control-label">Title *</label>

                    <div class="col-sm-9">
                        <input id="title" type="text" ng-model="attachmentUploadCtrl.metadata.title"
                               class="form-control"
                               required ng-required="true"/>
                    </div>
                </div>
            </div>

            <div class="form-horizontal">
                <div class="form-group">
                    <label for="createdDate" class="col-sm-3 control-label">Date created</label>

                    <div class="col-sm-9">
                        <fallback-date-picker field-id="createdDate"
                                              ng-model="attachmentUploadCtrl.metadata.createdDate"></fallback-date-picker>
                    </div>
                </div>
            </div>

            <div class="form-horizontal">
                <div class="form-group">
                    <label for="description" class="col-sm-3 control-label">Description</label>

                    <div class="col-sm-9">
                        <textarea id="description" ng-model="attachmentUploadCtrl.metadata.description"
                                  class="form-control" rows="3" maxlength="500" ng-maxlength="500"></textarea>
                    </div>
                </div>
            </div>

            <div class="form-horizontal">
                <div class="form-group">
                    <label for="owner" class="col-sm-3 control-label" required>Author</label>

                    <div class="col-sm-9">
                        <input id="owner" type="text" ng-model="attachmentUploadCtrl.metadata.creator"
                               class="form-control"/>
                    </div>
                </div>
            </div>

            <div class="form-horizontal">
                <div class="form-group">
                    <label for="rights" class="col-sm-3 control-label" required>Rights</label>

                    <div class="col-sm-9">
                        <input id="rights" type="text" ng-model="attachmentUploadCtrl.metadata.rights"
                               class="form-control"/>
                    </div>
                </div>
            </div>

            <div class="form-horizontal">
                <div class="form-group">
                    <label for="rightsHolder" class="col-sm-3 control-label" required>Rights Holder</label>

                    <div class="col-sm-9">
                        <input id="rightsHolder" type="text" ng-model="attachmentUploadCtrl.metadata.rightsHolder"
                               class="form-control"/>
                    </div>
                </div>
            </div>

            <div class="form-horizontal">
                <div class="form-group">
                    <label for="licence" class="col-sm-3 control-label" required>Licence *</label>

                    <div class="col-sm-9">
                        <select id="licence"
                                ng-options="licence.name for licence in attachmentUploadCtrl.licences | orderBy:'name'"
                                ng-model="attachmentUploadCtrl.metadata.licence" class="form-control"
                                ng-required="true">
                        </select>
                    </div>
                </div>
            </div>

            <div class="col-md-12">
                <p class="small pull-right">
                    Fields marked with an asterisk (*) are mandatory.
                </p>
            </div>
        </div>

        <div class="modal-footer">
            <button class="btn btn-primary" ng-click="attachmentUploadCtrl.ok()"
                    ng-disabled="UploadForm.$invalid">OK</button>
            <button class="btn btn-default" ng-click="attachmentUploadCtrl.cancel()">Cancel</button>
        </div>
    </div>
    </script>
</div>
