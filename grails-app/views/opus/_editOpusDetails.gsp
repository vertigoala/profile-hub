<div class="panel panel-default" ng-form="OpusForm" ng-cloak>
    <div class="panel-heading">
        <a name="overview">
            <h4>Site overview</h4>
        </a>
    </div>

    <div class="panel-body">

        <div ng-show="!opusCtrl.opus.uuid">
            <div class="col-sm-12">
                <div class="form-group">
                    <label for="opusName">Name</label>
                    <input type="text"
                           id="opusName"
                           class="form-control"
                           name="opusName"
                           ng-model="opusCtrl.opus.dataResource"
                           typeahead-editable="false"
                           typeahead-on-select="opusCtrl.opusResourceChanged($item, $model, $label)"
                           typeahead="source as source.name for source in opusCtrl.dataResourceList | filter:$viewValue | limitTo:10"/>
                </div>
                <alert type="danger"
                       ng-show="!opusCtrl.opus.dataResource">You must select a value from the list.</alert>
            </div>

        </div>

        <div class="col-sm-12">
            <h4 class="heading-underlined">Description</h4>

            <p>
                {{opusCtrl.dataResource.pubDescription | default:'No description available.'}}
            </p>

            <h4 class="heading-underlined">Rights</h4>

            <p>
                {{opusCtrl.dataResource.rights | default:'No rights statement available.'}}
            </p>

            <h4 class="heading-underlined">Citation</h4>

            <p>
                {{opusCtrl.dataResource.citation | default:'No citation statement available.'}}
            </p>
        </div>
    </div>

    <div class="panel-footer">
        <div class="row">
            <div class="col-md-12">
                <g:if test="${params.isOpusAdmin}">
                    <a href="${grailsApplication.config.collectory.base.url}dataResource/show/{{opusCtrl.opus.dataResourceUid}}"
                       class="btn btn-primary pull-right" ng-show="opusCtrl.opus.uuid" target="_blank">Edit details</a>
                </g:if>

                <button class="btn btn-primary pull-right" ng-click="opusCtrl.saveOpus(OpusForm)"
                        ng-show="!opusCtrl.opus.uuid"
                        ng-disabled="!opusCtrl.opus.dataResource">
                    <span ng-show="!opusCtrl.saving" id="saved"><span ng-show="OpusForm.$dirty">*</span> Save</span>
                    <span ng-show="opusCtrl.saving" id="saving">Saving....</span>
                </button>
            </div>
        </div>
    </div>
</div>