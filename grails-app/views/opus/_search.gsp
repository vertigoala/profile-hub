<div ng-controller="SearchController as searchCtrl" ng-cloak>

    <div class="col-xs-12 col-sm-12 col-md-12">
        <h3 class="heading-medium">Search for a profile</h3>

        <div class="input-group">
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
    </div>

    <div ng-show="searchCtrl.searchResults.items.length == 0">
        <div class="col-xs-12 col-sm-12 col-md-12">
            No results containing your search terms were found.
        </div>
    </div>

    <div ng-show="searchCtrl.searchResults.items.length > 0">
        <div class="col-xs-12 col-sm-12 col-md-12">
            Showing 1 to {{ searchCtrl.searchResults.items.length }} of {{ searchCtrl.searchResults.total }} results, sorted by relevance.
        </div>

        <div class="col-md-12 padding-top-1" ng-repeat="profile in searchCtrl.searchResults.items" ng-cloak>
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
                <h4 class="inline-block"><a href="${request.contextPath}/opus/{{ profile.opusShortName ? profile.opusShortName : profile.opusId }}/profile/{{ profile.scientificName | enc }}"
                       target="_self">{{ profile.scientificName }}</a></h4>
                <div class="inline-block padding-left-1" ng-show="profile.rank">({{profile.rank | capitalize}})</div>

                <div class="font-xsmall" ng-show="profile.rank"><span ng-repeat="name in profile.otherNames">{{ name.text | capitalize }}<span ng-show="!$last">, </span></span></div>
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
