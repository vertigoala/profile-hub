<div class="padding-top-1" ng-controller="SearchController as searchCtrl" ng-init="searchCtrl.getTaxonLevels()" ng-cloak>
    <div class="col-lg-3 col-md-4 col-xs-12">
        <!-- Side menu -->
        <div class="side-menu">
            <nav class="navbar navbar-default" role="navigation">
                <!-- Brand and toggle get grouped for better mobile display -->
                <div class="navbar-header">
                    <div class="brand-wrapper">

                        <!-- Brand -->
                        <div class="brand-name-wrapper">
                            <span class="navbar-brand">Quick search</span>
                        </div>

                        <!-- Search body -->
                        <div id="search" class="panel">
                            <div class="panel-body">
                                <form class="navbar-form" role="search">
                                    <div class="form-group">
                                        <input type="text" class="form-control" placeholder="e.g. Acacia binervata"
                                               ng-change="searchCtrl.search()"
                                               name="searchTerm"
                                               autocomplete="off"
                                               ng-model="searchCtrl.searchTerm"></div>
                                    <button type="submit" class="btn btn-default ">
                                        <span class="fa fa-search"></span>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </nav>
            <nav class="navbar navbar-default" role="navigation">
                <!-- Brand and toggle get grouped for better mobile display -->
                <div class="navbar-header">
                    <div class="brand-wrapper">
                        <div class="brand-name-wrapper">
                            <span class="navbar-brand">Browse by category</span>
                        </div>
                    </div>
                </div>

                <accordion close-others="true">
                    <accordion-group ng-repeat="taxon in searchCtrl.orderedTaxonLevels | orderBy:order">
                        <accordion-heading>
                            <i class="fa fa-circle-thin"></i>
                        &emsp;<a ng-href="#"
                                 ng-click="searchCtrl.searchByTaxonLevel(taxon.key)">{{taxon.name}} ({{searchCtrl.taxonLevels[taxon.key]}})</a>
                            <span class="caret"></span>
                        </accordion-heading>

                        <div ng-repeat="(name, count) in searchCtrl.taxonResults[taxon.key]" class="accordion-item">
                            <a ng-href=""
                               ng-click="searchCtrl.searchByTaxon(taxon.key, name, count)">{{name | capitalize}} ({{count}})</a>
                            <a ng-href=""
                               ng-if="$index >= searchCtrl.MAX_FACET_ITEMS - 1 && $last && $index < searchCtrl.taxonLevels[taxon.key] - 1"
                               ng-click="searchCtrl.searchByTaxonLevel(taxon.key, $index)"><p>View more...</p>
                            </a>
                        </div>
                    </accordion-group>
                </accordion>
            </nav>
        </div>
    </div>


    <div class="col-lg-9 col-md-8 col-xs-12" ng-cloak>
        <div ng-show="!searchCtrl.selectedTaxon.name && !searchCtrl.searchTerm">
            <p>
                Search or browse by category using the navigation bar to the left.
            </p>
            <p ng-if="opusCtrl.opus.keybaseKeyId">
                Alternatively, you can select the Identify tab and search for a profile using the dichotomous key player.
            </p>
        </div>
        <h4 class="heading-underlined"
            ng-show="searchCtrl.selectedTaxon.name">{{searchCtrl.selectedTaxon.level | capitalize}}: {{searchCtrl.selectedTaxon.name | capitalize}} <small>({{searchCtrl.selectedTaxon.count}} entries)</small>
        </h4>
        <h4 class="heading-underlined"
            ng-show="searchCtrl.searchTerm && !searchCtrl.selectedTaxon.name">Results for "{{searchCtrl.searchTerm}}*"</small>
        </h4>

        <div class="table-responsive">
            <table class="table table-striped" ng-show="searchCtrl.profiles.length > 0">
                <tr ng-repeat="profile in searchCtrl.profiles">
                    <td>{{profile.rank | capitalize | default:'Unknown'}}:</td>
                    <td>
                        <a href="${request.contextPath}/opus/{{ searchCtrl.opusId }}/profile/{{ profile.scientificName }}"
                           target="_self" class="scientific-name">{{profile.scientificName}}</a>
                    </td>
                </tr>
            </table>
            <pagination total-items="searchCtrl.selectedTaxon.count"
                        ng-change="searchCtrl.searchByTaxon(searchCtrl.selectedTaxon.level, searchCtrl.selectedTaxon.name, searchCtrl.selectedTaxon.count, (searchCtrl.page - 1) * searchCtrl.pageSize)"
                        ng-model="searchCtrl.page" max-size="10" class="pagination-sm"
                        items-per-page="searchCtrl.pageSize"
                        previous-text="Prev" boundary-links="true"
                        ng-show="searchCtrl.selectedTaxon.count > searchCtrl.pageSize"></pagination>
        </div>
    </div>
</div>