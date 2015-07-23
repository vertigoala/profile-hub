<div class="panel panel-default" ng-controller="PublicationController as pubCtrl"
     ng-cloak id="browse_lists"
     ng-show="pubCtrl.publications.length > 0 || !pubCtrl.readonly()"
     ng-form="PubForm">
    <a name="{{pubCtrl.readonly() ? 'view_' : 'edit_'}}publications"></a>

    <div class="panel-heading">
        <div class="row">
            <div class="col-sm-12">
                <h4 class="section-panel-heading">Versions</h4>
            </div>
        </div>
    </div>

    <div class="panel-body">
        <div class="row section-no-para">
            <div class="col-sm-12" ng-repeat="pub in pubCtrl.publications">
                <publication data="pub" opus-id="pubCtrl.opusId" profile-id="pubCtrl.profileId">
                </publication>
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
