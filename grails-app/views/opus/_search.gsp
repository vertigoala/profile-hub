<div ng-controller="SearchController as searchCtrl" ng-cloak>

    <div class="col-xs-12 col-sm-12 col-md-12">
        <h3 class="heading-medium">Search for a profile</h3>

        <div class="input-group">
            <span class="input-group-btn">
                <button type="button" class="btn btn-default dropdown-toggle btn-lg" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                    {{ searchCtrl.searchOptions.nameOnly ? 'by name' : 'containing' }} <span class="caret"></span>
                </button>
                <ul class="dropdown-menu">
                    <li><a href="#" ng-click="searchCtrl.setSearchOption('name')">by name</a></li>
                    <li><a href="#" ng-click="searchCtrl.setSearchOption('text')">containing</a></li>
                </ul>
            </span>
            <input id="searchTerm"
                   ng-model="searchCtrl.searchTerm"
                   ng-enter="searchCtrl.search()"
                   name="searchTerm"
                   class="input-lg form-control ignore-save-warning"
                   autocomplete="on"
                   placeholder="e.g. Acacia abbatiana"
                   type="text">
            <span class="input-group-btn">
                <button class="btn btn-primary btn-lg" type="button" ng-click="searchCtrl.search()">Search</button>
            </span>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <a href="" class="btn btn-link btn-sm fa" ng-click="searchCtrl.toggleSearchOptions()"
               ng-class="searchCtrl.showSearchOptions ? 'fa-angle-double-up' : 'fa-angle-double-down'">&nbsp;
                Search options
            </a>
        </div>

        <div class="col-md-12" ng-show="searchCtrl.showSearchOptions">
            <label for="nameSearch">Name search</label>
            <input id="nameSearch" type="checkbox" class="ignore-save-warning" ng-model="searchCtrl.searchOptions.nameOnly">
        </div>
    </div>

    <div  ng-show="searchCtrl.profiles.length > 0">
        <div class="col-xs-12 col-sm-12 col-md-12">
            Showing {{ searchCtrl.profiles.length }} of {{ searchCtrl.totalResults }} results, sorted by relevance.
        </div>
        <div class="col-xs-12 col-sm-12 col-md-12">
            <div class="table-responsive">
                <table class="table table-striped">
                    <tr>
                        <th>Rank</th>
                        <th>Taxon</th>
                        <th ng-if="!searchCtrl.opusId">Collection</th>
                        <th>Relevance</th>
                    </tr>
                    <tr ng-repeat="profile in searchCtrl.profiles">
                        <td><a href="${request.contextPath}/opus/{{ profile.opusShortName ? profile.opusShortName : profile.opusId }}/profile/{{ profile.scientificName }}"
                               target="_self">{{profile.rank | capitalize | default:'Unknown'}}:</a></td>
                        <td><a href="${request.contextPath}/opus/{{ profile.opusShortName ? profile.opusShortName : profile.opusId }}/profile/{{ profile.scientificName }}"
                               target="_self" class="scientific-name">{{profile.scientificName}}</a></td>
                        <td ng-if="!searchCtrl.opusId">{{profile.opusName}}</td>
                        <td><percent-display percent="searchCtrl.formatScore(profile.score)" side="40" colors="#AEFFFC #0087BE"></percent-display></td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
