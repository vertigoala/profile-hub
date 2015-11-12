<div ng-controller="SearchController as searchCtrl" ng-cloak>

    <div class="col-xs-12 col-sm-12 col-md-12">
        <h3 class="heading-medium">Search for a profile</h3>

        <div class="input-group">
            <span class="input-group-btn">
                <button type="button" class="btn btn-default dropdown-toggle btn-lg" data-toggle="dropdown"
                        aria-haspopup="true" aria-expanded="false">
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
            <input id="nameSearch" type="checkbox" class="ignore-save-warning"
                   ng-model="searchCtrl.searchOptions.nameOnly">
        </div>
    </div>

    <div ng-show="searchCtrl.profiles.length > 0">
        <div class="col-xs-12 col-sm-12 col-md-12">
            Showing {{ searchCtrl.profiles.length }} of {{ searchCtrl.totalResults }} results, sorted by relevance.
        </div>

        <div class="col-md-12 padding-top-1" ng-repeat="profile in searchCtrl.profiles" ng-cloak>
            <div class="col-md-2 col-sm-6 col-xs-12">
                <a href="${request.contextPath}/opus/{{ profile.opusShortName ? profile.opusShortName : profile.opusId }}/profile/{{ profile.scientificName | enc }}"
                   target="_self">
                    <div class="imgConSmall" in-view="searchCtrl.loadImageForProfile(profile.uuid)">
                        <div ng-show="profile.image.url">
                            <img ng-src="{{profile.image.url}}"
                                 ng-if="profile.image.url && profile.image.type.name == 'OPEN'"
                                 class="thumbnail"/>
                            <img ng-src="${request.contextPath}{{profile.image.url}}"
                                 ng-if="profile.image.url && profile.image.type.name != 'OPEN'"
                                 class="thumbnail"/>
                        </div>
                        <img src="${request.contextPath}/images/not-available.png"
                             ng-hide="profile.image.url || profile.image.status == 'checking'" class="thumbnail"
                             alt="There is no image for this profile"/>
                        <div class="fa fa-spinner fa-spin" ng-show="profile.image.status == 'checking'"></div>
                    </div>
                </a>
            </div>

            <div ng-class="searchCtrl.opusId ? 'col-md-10 col-sm-6 col-xs-12' : 'col-md-8 col-sm-6 col-xs-12'">
                <h4><a href="${request.contextPath}/opus/{{ profile.opusShortName ? profile.opusShortName : profile.opusId }}/profile/{{ profile.scientificName | enc }}"
                       target="_self">{{ profile.scientificName }}</a></h4>

                <div class="small" ng-show="profile.rank">{{profile.rank | capitalize}}</div>
            </div>

            <div class="col-md-2 col-sm-6 col-xs-12" ng-show="!searchCtrl.opusId">
                {{profile.opusName}}
            </div>

            <div class="col-md-12">
                <hr/>
            </div>
        </div>

    </div>
</div>
