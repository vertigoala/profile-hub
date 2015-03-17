<h2>Quick search</h2>

<div ng-controller="SearchController as searchCtrl">
    <div class="input-append">
        <input ng-change="searchCtrl.search()" ng-model="searchCtrl.searchTerm" name="searchTerm" class="input-xxlarge form-control"
               id="searchTerm" type="text" ng-model="searchCtrl.scientificName"/>
        <button class="btn" type="button">Search</button>
    </div>

    <table class="table table-striped" ng-show="searchCtrl.profiles.length > 0">
        <tr>
            <th>Taxon</th>
        </tr>
        <tr ng-repeat="profile in searchCtrl.profiles">
            <td><a href="${createLink(mapping: 'viewProfile')}/{{ profile.profileId }}"
                   target="_self">{{profile.scientificName}}</a>
            </td>
        </tr>
    </table>

    <div ng-show="searchCtrl.profiles.length == 0">
        <p>No matching results</p>
    </div>
</div>