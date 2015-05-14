<div ng-controller="TaxonController as taxonCtrl" ng-init="taxonCtrl.init('${edit}')" ng-cloak>
    <div id="browse_taxonomy" class="bs-docs-example ng-cloak"
         data-content="Taxonomy from {{taxonCtrl.speciesProfile.taxonConcept.infoSourceName}}" ng-show="taxonCtrl.classifications.length > 0" ng-cloak>
        <ul>
            <li ng-repeat="classification in taxonCtrl.classifications">
                <a href="${request.contextPath}/opus/{{taxonCtrl.opusId}}/profile/{{classification.profileUuid}}"
                   ng-if="classification.profileUuid" target="_self">{{classification.rank | capitalize}}: {{classification.scientificName}}</a>
                <span ng-if="!classification.profileUuid">{{classification.rank | capitalize}}: {{classification.scientificName}}</span>
            </li>
        </ul>
    </div>
</div>