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
        </div>
    </div>

    <div class="panel-body">
        <div class="row">
            <div class="col-sm-12">
                How does this look like?
                <div id="edit-documents" class="pill-pane">
                    <div class="row-fluid">
                        <div class="span10">
                            <g:render template="/resource/list"
                                      model="[documentResourceAdmin:true]"/>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>
</body>
</html>