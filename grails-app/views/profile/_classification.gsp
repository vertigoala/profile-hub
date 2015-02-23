<div ng-controller="ClassificationsController" ng-init="init('${edit}')" class="bs-docs-example" id="browse_taxonomy"
     data-content="Taxonomy from {{speciesProfile.taxonConcept.infoSourceName}}">
    <ul>
        <li ng-repeat="classification in classifications">
            <a href="{{classification.profileId}}" ng-if="classification.profileId">{{classification.rank | capitalize}}: {{classification.scientificName}}</a>
            <span ng-if="!classification.profileId">{{classification.rank | capitalize}}: {{classification.scientificName}}</span>
        </li>
    </ul>
</div>