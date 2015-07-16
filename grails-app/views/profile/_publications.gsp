<div class="panel panel-default" ng-controller="PublicationController as pubCtrl"
     ng-cloak id="browse_lists"
     ng-show="pubCtrl.publications.length > 0 || !pubCtrl.readonly()"
     ng-form="PubForm">
    <a name="{{pubCtrl.readonly() ? 'view_' : 'edit_'}}publications"></a>

    <div class="panel-body">
        <div class="row">
            <div class="col-sm-2"><strong>Versions</strong></div>

            <div class="col-sm-10">
                <div class="col-sm-12" ng-repeat="pub in pubCtrl.publications">
                    <publication ng-class="col-sm-10" title="pub.title" publication-date="pub.publicationDate"
                                 authors="pub.authors" uuid="pub.uuid" doi="pub.doi" version="pub.version"
                                 profile-id="pubCtrl.profileId" opus-id="pubCtrl.opusId"
                                 context="'${grailsApplication.config.profile.service.url}'">

                    </publication>
                    <hr ng-if="!$last"/>
                </div>
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
