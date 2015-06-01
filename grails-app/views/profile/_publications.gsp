<div ng-controller="PublicationController as pubCtrl" class="bs-docs-example"
     ng-cloak id="browse_lists" data-content="Versions"
     ng-show="pubCtrl.publications.length > 0 || !pubCtrl.readonly()"
     ng-form="PubForm">

    <g:if test="${params.isOpusEditor}">
        <button ng-show="!pubCtrl.readonly() && !pubCtrl.newPublication" ng-click="pubCtrl.savePublication()"
                class="btn btn-info"><i
                class="icon icon-plus icon-white"></i> Create snapshot version
        </button>
    </g:if>

    <div ng-repeat="publication in pubCtrl.publications">
        <div class="row-fluid">
            <div class="span12">
                <h4 ng-show="publication.title != ''">
                    Title: {{publication.title}}
                </h4>
                <div ng-show="publication.publicationDate != ''">
                    Publication Date: {{publication.publicationDate | date:"dd/MM/yyyy HH:mm"}}
                </div>
                <div ng-show="publication.authors != ''">
                    Authors: {{publication.authors}}
                </div>
                <div ng-show="publication.doi">
                    Unique ID: {{publication.uuid}}
                </div>
                <a ng-href="${grailsApplication.config.profile.service.url}/opus/{{pubCtrl.opusId}}/profile/{{pubCtrl.profileId}}/publication/{{publication.uuid}}/file" target="_blank"><span class="fa fa-download">  Download</span></a>
            </div>
        </div>
        <hr ng-if="!$last"/>
    </div>
</div>