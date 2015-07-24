<div class="panel panel-default" ng-controller="TaxonController as taxonCtrl" ng-init="taxonCtrl.init('${edit}')"
     ng-cloak ng-show="taxonCtrl.classifications.length > 0">
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
                <ul>
                    <li ng-repeat="classification in taxonCtrl.classifications">
                        <span ng-if="classification.profileUuid">
                            <a href="${request.contextPath}/opus/{{taxonCtrl.opusId}}/profile/{{classification.profileName}}"
                               ng-if="classification.profileUuid"
                               target="_self">{{classification.rank | capitalize}}: {{classification.scientificName}}</a>
                        </span>
                        <span ng-if="!classification.profileUuid">{{classification.rank | capitalize}}: {{classification.scientificName}}</span>
                        &nbsp;&nbsp;<a href="" class="fa fa-list-ul"
                                       title="Show members of {{classification.rank}} {{classification.scientificName}}"
                                       ng-click="taxonCtrl.showChildren(classification.rank, classification.scientificName)"></a>
                    </li>
                </ul>
            </div>
        </div>

        <div class="row" ng-if="taxonCtrl.infraspecificTaxa.length > 0">
            <div class="col-sm-12 margin-bottom-1">
                <hr/>
                <h4 class="section-heading">Subspecies</h4>
            </div>

            <div class="col-sm-12" ng-if="taxonCtrl.infraspecificTaxa.length > 0">

                <ul>
                    <li ng-repeat="taxa in taxonCtrl.infraspecificTaxa">
                        <a href="${request.contextPath}/opus/{{taxonCtrl.opusId}}/profile/{{taxa.scientificName}}"
                           ng-if="taxa.profileId" target="_self">{{taxa.scientificName}}</a>
                    </li>
                </ul>
            </div>
        </div>
    </div>

</div>


<!-- template for the popup displayed when Export as PDF is selected -->
<script type="text/ng-template" id="showTaxonChildren.html">
<div class="modal-header">
    <h4 class="modal-title">{{taxonChildrenCtrl.taxon.level | capitalize}}: {{taxonChildrenCtrl.taxon.scientificName | capitalize}}</h4>
</div>

<div class="modal-body">
    <table class="table table-striped" ng-show="taxonChildrenCtrl.profiles.length > 0">
        <tr>
            <th>Rank</th>
            <th>Taxon</th>
        </tr>
        <tr ng-repeat="profile in taxonChildrenCtrl.profiles">
            <td>{{profile.rank | capitalize}}</td>
            <td><a href="${request.contextPath}/opus/{{ taxonChildrenCtrl.opusId }}/profile/{{ profile.scientificName }}"
                   target="_self">{{profile.scientificName}}</a>
            </td>
        </tr>
    </table>
    <pagination total-items="taxonChildrenCtrl.taxon.count"
                ng-change="taxonChildrenCtrl.loadChildren((taxonChildrenCtrl.page - 1) * taxonChildrenCtrl.pageSize)"
                ng-model="taxonChildrenCtrl.page" max-size="10" class="pagination-sm"
                items-per-page="taxonChildrenCtrl.pageSize"
                previous-text="Prev" boundary-links="true"
                ng-show="taxonChildrenCtrl.taxon.count > taxonChildrenCtrl.pageSize"></pagination>

    <span ng-show="taxonChildrenCtrl.profiles.length == 0">There are no child taxa for {{taxonChildrenCtrl.taxon.scientificName | capitalize}}</span>
</div>

<div class="modal-footer">
    <button class="btn btn-default" ng-click="taxonChildrenCtrl.cancel()">Close</button>
</div>
</script>