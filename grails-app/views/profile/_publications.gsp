<div ng-controller="PublicationController as pubCtrl" class="bs-docs-example"
     ng-cloak id="browse_lists" data-content="Publications"
     ng-show="pubCtrl.publications.length > 0 || !pubCtrl.readonly()"
     ng-form="PubForm">

    <g:if test="${params.isOpusEditor}">
        <button ng-show="!pubCtrl.readonly() && !pubCtrl.newPublication" ng-click="pubCtrl.addPublication(PubForm)"
                class="btn btn-info"><i
                class="icon icon-plus icon-white"></i>Add Publication
        </button>

        <div ng-show="!pubCtrl.readonly() && pubCtrl.newPublication">
            <form ng-submit="pubCtrl.savePublication(PubForm)" role="form">
                <label for="pubTitle">Title</label>
                <input id="pubTitle" type="text" class="field span12" ng-model="pubCtrl.newPublication.title" ng-required="true"/>
                <br/>
                <label for="pubDate">Publication Date</label>
                <input id="pubDate" type="date" ng-model="pubCtrl.newPublication.publicationDate" ng-required="true" />
                <br/>
                <label for="pubAuthors">Author(s)</label>
                <textarea id="pubAuthors" class="field span12" ng-model="pubCtrl.newPublication.authors" ng-required="true"></textarea>
                <br/>
                <label for="pubDescription">Description</label>
                <textarea id="pubDescription" class="field span12" ng-model="pubCtrl.newPublication.description" ng-required="true"></textarea>
                <br/>
                <label for="pubDOI">DOI</label>
                <input id="pubDOI" type="text" class="input-large" ng-model="pubCtrl.newPublication.doi"/>
                <br/>
                <label for="file">Reviewed copy</label>
                <input id="file" type="file" class="input-large" onchange="angular.element(this).scope().pubCtrl.uploadFile(this)" accept="application/pdf" required/>
                <br/>

                <button ng-show="!pubCtrl.readonly() && pubCtrl.newPublication"
                        class="btn btn-primary" ng-disabled="PubForm.$invalid"><span
                        ng-show="PubForm.$dirty">*</span> Save</button>
                <button ng-show="!pubCtrl.readonly() && pubCtrl.newPublication" ng-click="pubCtrl.cancelNewPublication(PubForm)"
                        class="btn btn-warning">Cancel</button>
                <hr/>
            </form>
        </div>
    </g:if>

    <div ng-repeat="publication in pubCtrl.publications">
        <div class="row-fluid">
            <div class="span12">
                <h4 ng-show="publication.title != ''">
                    Title: {{publication.title}}
                </h4>
                <div ng-show="publication.publicationDate != ''">
                    Publication Date: {{publication.publicationDate | date:"dd/MM/yyyy"}} (uploaded on {{publication.uploadDate | date:"dd/MM/yyyy"}})
                </div>
                <div ng-show="publication.authors != ''">
                    Authors: {{publication.authors}}
                </div>
                <div ng-show="publication.description != ''">
                    Description: {{publication.description}}
                </div>
                <div ng-show="publication.doi">
                    DOI: {{publication.doi}}
                </div>
            </div>
            <div class="row-fluid">
                <div class="pull-right">
                    <a ng-href="${grailsApplication.config.profile.service.url}/opus/{{pubCtrl.opusId}}/profile/{{pubCtrl.profileId}}/publication/{{publication.uuid}}/file" target="_blank" class="btn btn-link fa fa-download">&nbsp;&nbsp;Download</a>
                    <g:if test="${params.isOpusEditor}">
                        <button ng-click="pubCtrl.deletePublication($index)" class="btn btn-danger" ng-show="!pubCtrl.readonly()">Delete</button>
                    </g:if>
                </div>
            </div>
        </div>
        <hr ng-if="!$last"/>
    </div>
</div>