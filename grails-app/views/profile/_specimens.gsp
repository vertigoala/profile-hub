<div ng-controller="SpecimenController as specCtrl" ng-cloak ng-form="SpecimenForm" >
    <div class="bs-docs-example ng-cloak"
         data-content="Specimens" ng-show="specCtrl.specimens.length > 0 || !specCtrl.readonly()"
         ng-cloak>
        <p class="lead" ng-if="!specCtrl.readonly()">
            Add links to specimen pages. Links should be of the form:
            <br/><b>http://biocache.ala.org.au/occurrences/e0fd3aca-7b21-44de-abe4-6b392cd32aae</b>
        </p>

        <div class="row-fluid" ng-repeat="specimen in specCtrl.specimens">
            <div ng-if="!specCtrl.readonly() && !specimen.saved">
                <label>URL:</label>
                <input type="text" class="input-xxlarge" ng-model="specimen.url"
                       ng-change="specCtrl.lookupSpecimenDetails($index, null, specimen.url)"/><br/>
                <alert type="danger" ng-if="specimen.url && !specimen.id">Invalid URL. The URL must of the form specified above.</alert>
            </div>
            <div ng-if="specimen.institutionName">Institution Name: <a href="${grailsApplication.config.collectory.base.url}/public/show/{{specimen.institutionUid}}"target="_blank">{{specimen.institutionName}}</a></div>
            <div ng-if="specimen.collectionName">Collection: <a href="${grailsApplication.config.collectory.base.url}/public/show/{{specimen.collectionUid}}"target="_blank">{{specimen.collectionName}}</a></div>
            <div ng-if="specimen.catalogNumber">Catalog Number: {{specimen.catalogNumber}}</div>
            <div ng-if="specimen.id"><a href="${grailsApplication.config.biocache.base.url}/occurrences/{{specimen.id}}" target="_blank">View specimen record</a></div>

            <div class="row-fluid">
            <div ng-if="!specCtrl.readonly()" class="pull-right">
                <button class="btn btn-danger"
                        ng-click="specCtrl.deleteSpecimen($index, SpecimenForm)">Delete</button>
            </div>
            </div>
            <hr ng-if="!$last"/>
        </div>

        <div class="row-fluid" ng-if="!specCtrl.readonly()">
            <hr/>
            <button class="btn btn-info" ng-click="specCtrl.addSpecimen(SpecimenForm)"><i
                    class="icon icon-plus icon-white"></i>Add Specimen</button>
            <button class="btn btn-primary" ng-click="specCtrl.save(SpecimenForm)" ng-disabled="!specCtrl.isValid()"><span
                    ng-show="SpecimenForm.$dirty">*</span> Save</button>
        </div>
    </div>
</div>