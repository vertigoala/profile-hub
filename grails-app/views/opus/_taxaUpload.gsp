<g:if test="${opus?.enableTaxaUpload}">
    <div class="well" ng-controller="TaxaController">
        <h3>Upload taxa</h3>
        <p>Click below to upload your own list of taxa in CSV format. A profile page will be created for each scientific name uploaded.<br/>
            This list can in include recognised scientific names and/or operational taxonomic unit (OTUs).
            Recognised names will be linked the Australian National Checklists.
        </p>
        <br/>
        <div>
            <span class="btn btn-default btn-file">
                Choose file
                <input type="file" id="taxaUploadFile" name="taxaUploadFile" ngf-select="" ng-model="taxaFile"/>
            </span>
            <span class="font-xsmall">{{ taxaFile[0].name }}</span>
        </div>
        <br/>
        <button class="btn" ng-click="taxaUpload()"><i class="icon-upload"></i> Upload taxa</button>
    </div>
</g:if>