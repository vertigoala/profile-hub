<div class="padding-top-1" ng-controller="BrowseController as browseCtrl" ng-init="browseCtrl.getTaxonLevels()" ng-cloak>
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
                                        <input type="text" class="form-control ignore-save-warning" placeholder="e.g. Acacia binervata"
                                               ng-change="browseCtrl.searchByScientificName()"
                                               name="searchTerm"
                                               autocomplete="off"
                                               ng-enter="browseCtrl.selectSingleResult()"
                                               ng-model="browseCtrl.searchTerm"></div>
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
                    <accordion-group ng-repeat="taxon in browseCtrl.orderedTaxonLevels | orderBy:order" ng-if="browseCtrl.taxonLevels[taxon.key] > 0">
                        <accordion-heading>
                            <i class="fa fa-circle-thin"></i>
                        &emsp;<a ng-href="#" title="{{ taxon.help }}"
                                 ng-click="browseCtrl.searchByTaxonLevel(taxon.key)">{{taxon.name}} ({{browseCtrl.taxonLevels[taxon.key] ? browseCtrl.taxonLevels[taxon.key] : '0'}})
                            <span ng-if="taxon.help" class="fa fa-question-circle small superscript"></span></a>
                            <span class="caret"></span>
                        </accordion-heading>

                        <div ng-repeat="(name, count) in browseCtrl.taxonResults[taxon.key]" class="accordion-item">
                            <a ng-href=""
                               ng-click="browseCtrl.searchByTaxon(taxon.key, name, count)">{{name | capitalize}} ({{count}})</a>
                            <a ng-href=""
                               ng-if="$index >= browseCtrl.MAX_FACET_ITEMS - 1 && $last && $index < browseCtrl.taxonLevels[taxon.key] - 1"
                               ng-click="browseCtrl.searchByTaxonLevel(taxon.key, $index)"><p>View more...</p>
                            </a>
                        </div>
                    </accordion-group>
                </accordion>
            </nav>
        </div>
    </div>


    <div class="col-lg-9 col-md-8 col-xs-12" ng-cloak>
        <div ng-show="!browseCtrl.selectedTaxon.name && !browseCtrl.searchTerm">
            <p>
                Search or browse by category using the navigation bar to the left.
            </p>
            <p ng-if="opusCtrl.opus.keybaseKeyId">
                Alternatively, you can select the Identify tab and search for a profile using the dichotomous key player.
            </p>
        </div>
        <h4 class="heading-underlined"
            ng-show="browseCtrl.selectedTaxon.name && browseCtrl.selectedTaxon.level != browseCtrl.selectedTaxon.name">{{browseCtrl.selectedTaxon.level | capitalize}}: {{browseCtrl.selectedTaxon.name | capitalize}} <small>({{browseCtrl.selectedTaxon.count}} entries)</small>
        </h4>
        <h4 class="heading-underlined"
            ng-show="browseCtrl.selectedTaxon.name && browseCtrl.selectedTaxon.level == browseCtrl.selectedTaxon.name">{{browseCtrl.selectedTaxon.level | capitalize}} <small>({{browseCtrl.selectedTaxon.count}} entries)</small>
        </h4>
        <h4 class="heading-underlined"
            ng-show="browseCtrl.searchTerm && !browseCtrl.selectedTaxon.name">Results for "{{browseCtrl.searchTerm}}*"</small>
        </h4>

        <div class="table-responsive">
            <table class="table table-striped" ng-show="browseCtrl.profiles.length > 0">
                <tr ng-repeat="profile in browseCtrl.profiles">
                    <td>{{profile.rank | capitalize | default:'Unknown'}}:</td>
                    <td>
                        <a href="${request.contextPath}/opus/{{ browseCtrl.opusId }}/profile/{{ profile.scientificName | enc }}"
                           target="_self" class="scientific-name">{{profile.scientificName}}</a>
                    </td>
                </tr>
            </table>
            <pagination total-items="browseCtrl.selectedTaxon.count"
                        ng-change="browseCtrl.searchByTaxon(browseCtrl.selectedTaxon.level, browseCtrl.selectedTaxon.name, browseCtrl.selectedTaxon.count, (browseCtrl.page - 1) * browseCtrl.pageSize)"
                        ng-model="browseCtrl.page" max-size="10" class="pagination-sm"
                        items-per-page="browseCtrl.pageSize"
                        previous-text="Prev" boundary-links="true"
                        ng-show="browseCtrl.selectedTaxon.count > browseCtrl.pageSize"></pagination>
        </div>
    </div>
</div>