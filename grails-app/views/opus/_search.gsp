<div ng-controller="SearchController as searchCtrl" ng-cloak>
    <!-- Row search -->
    <div class="col-xs-12 col-sm-12 col-md-12">
        <h3 class="heading-medium">Search for a profile</h3>

        <div class="input-group">
            <input id="searchTerm"
                   ng-change="searchCtrl.search()"
                   ng-model="searchCtrl.searchTerm"
                   name="searchTerm"
                   class="input-lg form-control ignore-save-warning"
                   autocomplete="off"
                   placeholder="e.g. Acacia abbatiana"
                   type="text">
            <span class="input-group-btn">
                <button class="btn btn-primary btn-lg" type="button">Search</button>
            </span>
        </div>
    </div>

    <div class="col-xs-12 col-sm-12 col-md-12">
    <div class="table-responsive">
        <table class="table table-striped" ng-show="searchCtrl.profiles.length > 0">
            <tr>
                <th>Rank</th>
                <th>Taxon</th>
                <th ng-if="!searchCtrl.opusId">Collection</th>
            </tr>
            <tr ng-repeat="profile in searchCtrl.profiles">
                <td><a href="${request.contextPath}/opus/{{ profile.opus.shortName ? profile.opus.shortName : profile.opus.uuid }}/profile/{{ profile.scientificName }}"
                       target="_self">{{profile.rank | capitalize | default:'Unknown'}}:</a></td>
                <td><a href="${request.contextPath}/opus/{{ profile.opus.shortName ? profile.opus.shortName : profile.opus.uuid }}/profile/{{ profile.scientificName }}"
                       target="_self" class="scientific-name">{{profile.scientificName}}</a></td>
                <td ng-if="!searchCtrl.opusId">{{profile.opus.title}}</td>
            </tr>
        </table>
    </div>
    </div>

    <div ng-show="searchCtrl.profiles.length == 0 && searchCtrl.searchTerm">
        <p>No matching results</p>
    </div>
</div>
