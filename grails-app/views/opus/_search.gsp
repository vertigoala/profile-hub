<div ng-controller="SearchController as searchCtrl" ng-cloak>

    <div class="col-xs-12 col-sm-12 col-md-12">
        <h3 class="heading-medium">Search for profile(s)</h3>

        <div class="input-group">
            <div class="input-group-btn">
                <button type="button" class="btn btn-default dropdown-toggle btn-lg search-type-control" data-toggle="dropdown"
                        aria-haspopup="true" aria-expanded="false">
                    {{ searchCtrl.searchOptions.nameOnly ? 'with name' : 'with text' }} <span class="caret"></span>
                </button>
                <ul class="dropdown-menu">
                    <li><a ng-click="searchCtrl.setSearchOption('name')">with name</a></li>
                    <li><a ng-click="searchCtrl.setSearchOption('text')">with text</a></li>
                </ul>
            </div>
            <input id="searchTerm"
                   ng-model="searchCtrl.searchTerm"
                   ng-enter="searchCtrl.search()"
                   name="searchTerm"
                   class="input-lg form-control ignore-save-warning"
                   autocomplete="on"
                   type="text">
            <span class="input-group-btn">
                <button class="btn btn-primary btn-lg" type="button" ng-click="searchCtrl.search()">Search</button>
                <button class="btn btn-default btn-lg" type="button" ng-click="searchCtrl.clearSearch()" title="Clear search"><span class="fa fa-trash"></span></button>
            </span>
        </div>
        <button class="btn btn-link toggle-link ignore-save-warning" ng-model="searchCtrl.showOptions" btn-checkbox>Options</button>
        <div ng-show="searchCtrl.showOptions" class="well" ng-hide="searchCtrl.searchOptions.nameOnly">
            <div class="checkbox inline-block padding-right-1">
                <label for="matchAll" class="inline-label">
                    <input id="matchAll" type="checkbox" name="matchAll" class="ignore-save-warning"
                           ng-model="searchCtrl.searchOptions.matchAll" ng-false-value="false">
                    Must contain all terms
                </label>
            </div>
            <div class="checkbox inline-block">
                <label for="includeArchivedProfiles" class="inline-label">
                    <input id="includeArchivedProfiles" type="checkbox" name="includeArchivedProfiles" class="ignore-save-warning"
                           ng-model="searchCtrl.searchOptions.includeArchived" ng-false-value="false">
                    Include archived profiles
                </label>
            </div>
        </div>
    </div>

    <div ng-show="searchCtrl.searchResults.items.length == 0">
        <div class="col-xs-12 col-sm-12 col-md-12">
            No results containing your search terms were found.
        </div>
    </div>

    <div ng-show="searchCtrl.searchResults.items.length > 0">
        <div class="col-xs-12 col-sm-12 col-md-12">
            Showing {{ searchCtrl.searchOptions.offset + 1 }} to {{ searchCtrl.searchOptions.offset + searchCtrl.searchResults.items.length }} of {{ searchCtrl.searchResults.total }} results, sorted by relevance.
        </div>

        <div class="col-md-12 padding-top-1" ng-repeat="profile in searchCtrl.searchResults.items" ng-cloak>
            <div class="col-md-2 col-sm-12 col-xs-12">
                <a href="${request.contextPath}/opus/{{ profile.opusShortName ? profile.opusShortName : profile.opusId }}/profile/{{ profile.archivedDate ? profile.uuid : profile.scientificName | enc }}"
                   target="_self">
                    <div class="imgConSmall" in-view="searchCtrl.loadImageForProfile(profile.uuid)" in-view-options="{ debounce: 100 }">
                        <div ng-show="profile.image.url">
                            <img ng-src="{{profile.image.url}}"
                                 ng-if="profile.image.url && profile.image.type.name == 'OPEN'"
                                 class="thumbnail"/>
                            <img ng-src="${request.contextPath}{{profile.image.url}}"
                                 ng-if="profile.image.url && profile.image.type.name != 'OPEN'"
                                 class="thumbnail"/>
                        </div>
                        <img src="${request.contextPath}/images/not-available.png"
                             ng-hide="profile.image.url || profile.image.status == 'checking' || profile.image.status == 'not-checked'" class="thumbnail"
                             alt="There is no image for this profile"/>
                        <div class="fa fa-spinner fa-spin" ng-show="profile.image.status != 'checked'"></div>
                    </div>
                </a>
            </div>

            <div ng-class="searchCtrl.opusId ? 'col-md-10 col-sm-12 col-xs-12' : 'col-md-8 col-sm-12 col-xs-12'">
                <h4 class="inline-block"><a href="${request.contextPath}/opus/{{ profile.opusShortName ? profile.opusShortName : profile.opusId }}/profile/{{ profile.archivedDate ? profile.uuid : profile.scientificName | enc }}"
                       target="_self">{{ profile.scientificName }}</a></h4>
                <div class="inline-block padding-left-1" ng-show="profile.rank">({{profile.rank | capitalize}})</div>

                <div class="font-xsmall" ng-show="profile.otherNames"><h5><span ng-repeat="name in profile.otherNames">{{ name.text | capitalize }}<span ng-show="!$last">, </span></span></h5></div>

                <div class="font-xsmall" ng-show="profile.description"><span ng-repeat="description in profile.description">{{ description.text | words: 100 }}<span ng-show="!$last">, </span></span></div>
            </div>

            <div class="col-md-2 col-sm-12 col-xs-12" ng-show="!searchCtrl.opusId">
                {{profile.opusName}}
            </div>

            <div class="col-md-12">
                <hr/>
            </div>

        </div>
        <pagination total-items="searchCtrl.searchResults.total"
                    ng-change="searchCtrl.search(searchCtrl.pageSize, (searchCtrl.page - 1) * searchCtrl.pageSize)"
                    ng-model="searchCtrl.page" max-size="10" class="pagination-sm"
                    items-per-page="searchCtrl.pageSize"
                    previous-text="Prev" boundary-links="true"
                    ng-show="searchCtrl.searchResults.total > searchCtrl.pageSize"></pagination>
    </div>
</div>
