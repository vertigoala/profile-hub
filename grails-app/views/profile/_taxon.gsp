<div ng-controller="TaxonController as taxonCtrl" ng-init="taxonCtrl.init('${edit}')">
    <div id="browse_taxonomy" class="bs-docs-example ng-cloak"
         data-content="Taxonomy from {{taxonCtrl.speciesProfile.taxonConcept.infoSourceName}}" ng-show="taxonCtrl.classifications.length > 0" ng-cloak>
        <ul>
            <li ng-repeat="classification in taxonCtrl.classifications">
                <a href="{{classification.profileUuid}}"
                   ng-if="classification.profileUuid" target="_self">{{classification.rank | capitalize}}: {{classification.scientificName}}</a>
                <span ng-if="!classification.profileUuid">{{classification.rank | capitalize}}: {{classification.scientificName}}</span>
            </li>
        </ul>
    </div>

    <div class="bs-docs-example ng-cloak" id="browse_names" data-content="Nomenclature" ng-cloak
         ng-show="taxonCtrl.speciesProfile && taxonCtrl.speciesProfile.taxonName">
        <ul style="list-style: none; margin-left:0px;">
            <li>
                <blockquote style="border-left:none;">
                    <p>{{taxonCtrl.speciesProfile.taxonName.nameComplete}} {{taxonCtrl.speciesProfile.taxonName.authorship}}</p>
                </blockquote>
            </li>
            <li ng-repeat="synonym in taxonCtrl.speciesProfile.synonyms">
                <blockquote style="border-left:none;">
                    <p>{{synonym.nameString}}</p>
                    <cite ng-if="synonym.referencedIn">- {{synonym.referencedIn | default:"Reference not available"}}</cite>
                </blockquote>
            </li>
        </ul>
    </div>
</div>