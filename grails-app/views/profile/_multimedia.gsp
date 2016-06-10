<div class="panel panel-default" ng-controller="MultimediaController as mmCtrl"
     ng-init="mmCtrl.init('${model}', '${edit}')" >
    <div ng-show="mmCtrl.documents.length > 0 || !mmCtrl.readonly" ng-cloak>
        <navigation-anchor anchor-name="view_multimedia" title="Multimedia"
                           condition="mmCtrl.documents.length > 0 && mmCtrl.readonly" ></navigation-anchor>
        <navigation-anchor anchor-name="edit_multimedia" title="Multimedia"
                           condition="!mmCtrl.readonly" ></navigation-anchor>
        <div class="panel-heading">
            <div class="row">
                <div class="col-sm-12">
                    <h4 class="section-panel-heading">Multimedia</h4>
                </div>
            </div>
        </div>

        <div class="panel-body">
            <div class="row">
                <div class="col-sm-12">
                    <div id="edit-documents" class="pill-pane">
                        <div class="row-fluid">
                            <div class="span10">
                                <g:render plugin="document-preview-plugin" template="/resource/attachDocument"/>
                                <g:render plugin="document-preview-plugin" template="/resource/listDocuments" model="[documentResourceAdmin: edit]"/>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>