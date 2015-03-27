<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Error</title>
    <r:require module="profiles"/>
</head>

<body>

<div class="container-fluid">
    <div class="row-fluid">
        <div class="span6">
            <ol class="breadcrumb" role="navigation">
                <li><i class="fa fa-arrow-left"></i><span class="divider"/><a href="${request.contextPath}/" target="_self">Home</a></li>
            </ol>
        </div>
    </div>

    <div class="row-fluid">
        <div class="alert alert-danger">An unexpected error has occurred while processing your request.</div>
    </div>
</div>
</body>
</html>
