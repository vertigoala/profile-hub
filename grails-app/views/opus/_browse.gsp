<h2>Browse</h2>

<div class="row-fluid" ng-controller="SearchController as searchCtrl" ng-init="searchCtrl.getTaxonLevels()" ng-cloak>
    <div class="span3">
        <accordion close-others="true">
            <accordion-group ng-repeat="taxon in searchCtrl.orderedTaxonLevels | orderBy:order">
                <accordion-heading>
                    <a ng-href="#" ng-click="searchCtrl.searchByTaxonLevel(taxon.key)">{{taxon.name}} ({{searchCtrl.taxonLevels[taxon.key]}})</a>
                </accordion-heading>

                <div ng-repeat="(name, count) in searchCtrl.taxonResults[taxon.key]">
                    <a ng-href=""
                       ng-click="searchCtrl.searchByTaxon(taxon.key, name, count)">{{name | capitalize}} ({{count}})</a>
                    <a ng-href=""
                       ng-if="$index >= searchCtrl.MAX_FACET_ITEMS - 1 && $last && $index < searchCtrl.taxonLevels[taxon.key] - 1"
                       ng-click="searchCtrl.searchByTaxonLevel(taxon.key, $index)"><br/>...</a>
                </div>
            </accordion-group>
        </accordion>
    </div>

    <div class="span9" ng-cloak>
        <h3>{{searchCtrl.selectedTaxon.name | capitalize}}</h3>
        <table class="table table-striped" ng-show="searchCtrl.profiles.length > 0">
            <tr>
                <th>Taxon</th>
            </tr>
            <tr ng-repeat="profile in searchCtrl.profiles">
                <td><a href="${request.contextPath}/opus/{{ searchCtrl.opusId }}/profile/{{ profile.scientificName }}"
                       target="_self">{{profile.scientificName}}</a>
                </td>
            </tr>
        </table>
        <pagination total-items="searchCtrl.selectedTaxon.count"
                    ng-change="searchCtrl.searchByTaxon(searchCtrl.selectedTaxon.level, searchCtrl.selectedTaxon.name, searchCtrl.selectedTaxon.count, (searchCtrl.page - 1) * searchCtrl.pageSize)"
                    ng-model="searchCtrl.page" max-size="10" class="pagination-sm" items-per-page="searchCtrl.pageSize"
                    previous-text="Prev" boundary-links="true" ng-show="searchCtrl.selectedTaxon.count > searchCtrl.pageSize"></pagination>
    </div>
</div>
