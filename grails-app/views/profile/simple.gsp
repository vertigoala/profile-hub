<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Dependencies Ok Title</title>
    <r:require modules="knockout,attachDocuments"/>
</head>

<body>
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
                            %{--<g:render template="/resource/list"--}%
                                      %{--model="[useExistingModel: true,editable:true, admin:true, filterBy: 'all', ignore: '', imageUrl:resource(dir:'/images/filetypes'),containerId:'adminDocumentList']"/>--}%
                            <g:render template="/resource/attachDocument"/>

                            <g:render template="/resource/listDocuments"
                                      model="[useExistingModel: true,editable:true, admin:true, filterBy: 'all', ignore: '', imageUrl:resource(dir:'/images/filetypes'),containerId:'adminDocumentList']"/>



                            %{--<g:render template="/shared/editDocuments"--}%
                            %{--model="[useExistingModel: true,editable:true, filterBy: 'all', ignore: '', imageUrl:resource(dir:'/images/filetypes'),containerId:'adminDocumentList']"/>--}%
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
</body>