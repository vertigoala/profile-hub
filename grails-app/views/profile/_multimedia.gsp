<div class="panel panel-default">
    <div class="panel-heading">
        <div class="row">
            <div class="col-sm-12">
                <h4 class="section-panel-heading">Multimedia</h4>
            </div>
            <h1>${grailsApplication.config.layout}</h1>
        </div>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-sm-12">
                How does this look like?
                <div id="edit-documents" class="pill-pane">
                    <div class="row-fluid">
                        <div class="span10">
                            <g:render template="/resource/list"/>

                            %{--<g:render template="/shared/editDocuments"--}%
                            %{--model="[useExistingModel: true,editable:true, filterBy: 'all', ignore: '', imageUrl:resource(dir:'/images/filetypes'),containerId:'adminDocumentList']"/>--}%
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

%{--<r:script disposition="head">--}%
    %{--console.log ('Loading MM');--}%
%{--</r:script>--}%
%{--<r:layoutResources/>--}%
