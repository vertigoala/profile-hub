<div class="panel panel-default" ng-controller="PublicationController as pubCtrl"
     ng-cloak id="browse_lists"
     ng-show="pubCtrl.publications.length > 0 || !pubCtrl.readonly()"
     ng-form="PubForm">
    <a name="{{pubCtrl.readonly() ? 'view_' : 'edit_'}}publications"></a>
    <div class="panel-body">
        <div class="col-sm-2"><strong>Versions</strong></div>

        <div class="col-sm-10">
            <div ng-repeat="publication in pubCtrl.publications">
                <div class="row">
                    <div class="col-md-10">
                        <strong ng-show="publication.title != ''">
                            Title: {{publication.title}}
                        </strong>

                        <div ng-show="publication.publicationDate != ''">
                            <strong>Publication Date:&nbsp;</strong>{{publication.publicationDate | date:"dd/MM/yyyy HH:mm"}}
                        </div>

                        <div ng-show="publication.authors != ''">
                            <strong>Authors:&nbsp;</strong>{{publication.authors}}
                        </div>

                        <div ng-show="publication.doi">
                            <strong>Unique ID:&nbsp;</strong>{{publication.uuid}}
                        </div>
                    </div>

                    <div class="col-md-2">
                        <a ng-href="${grailsApplication.config.profile.service.url}/opus/{{pubCtrl.opusId}}/profile/{{pubCtrl.profileId}}/publication/{{publication.uuid}}/file"
                           target="_blank"><span class="fa fa-download color--green">&nbsp;Download</span></a>
                    </div>
                </div>
                <hr ng-if="!$last"/>
            </div>
        </div>
    </div>

    <div class="panel-footer" ng-if="!pubCtrl.readonly()">
        <div class="row">
            <div class="col-md-12">
                <g:if test="${params.isOpusEditor}">
                    <button ng-show="!pubCtrl.readonly() && !pubCtrl.newPublication"
                            ng-click="pubCtrl.savePublication()"
                            class="btn btn-default"><i
                            class="fa fa-plus"></i> Create snapshot version
                    </button>
                </g:if>
            </div>
        </div>
    </div>
</div>
