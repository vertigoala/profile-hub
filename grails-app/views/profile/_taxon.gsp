<div ng-controller="TaxonController" ng-init="init('${edit}')">
    <div id="browse_taxonomy" class="bs-docs-example ng-cloak"
         data-content="Taxonomy from {{speciesProfile.taxonConcept.infoSourceName}}" ng-show="classifications" ng-cloak>
        <ul>
            <li ng-repeat="classification in classifications">
                <a href="{{classification.profileId}}"
                   ng-if="classification.profileId">{{classification.rank | capitalize}}: {{classification.scientificName}}</a>
                <span ng-if="!classification.profileId">{{classification.rank | capitalize}}: {{classification.scientificName}}</span>
            </li>
        </ul>
    </div>

    <div class="bs-docs-example ng-cloak" id="browse_names" data-content="Nomenclature" ng-cloak
         ng-show="speciesProfile and speciesProfile.taxonName">
        <ul style="list-style: none; margin-left:0px;">
            <li>
                <blockquote style="border-left:none;">
                    <p>{{speciesProfile.taxonName.nameComplete}} {{speciesProfile.taxonName.authorship}}</p>
                </blockquote>
            </li>
            <li ng-repeat="synonym in speciesProfile.synonyms">
                <blockquote style="border-left:none;">
                    <p>{{synonym.nameString}}</p>
                    <cite ng-if="synonym.referencedIn">- {{synonym.referencedIn}}</cite>
                    <cite ng-if="!synonym.referencedIn">- Reference not available</cite>
                </blockquote>
            </li>
        </ul>
    </div>
</div>