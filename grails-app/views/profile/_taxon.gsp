<div class="panel panel-default" ng-cloak ng-show="profileCtrl.profile.classification.length > 0">
    <a name="{{profileCtrl.readonly() ? 'view_' : 'edit_'}}taxon"></a>

    <div class="panel-heading">
        <div class="row">
            <div class="col-sm-12">
                <h4 class="section-panel-heading">Taxonomy</h4>
            </div>
        </div>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-sm-12">
                <taxonomy data="profileCtrl.profile.classification" opus-id="profileCtrl.opusId"
                          include-rank="true" show-children="true"></taxonomy>
            </div>
        </div>
    </div>
</div>
