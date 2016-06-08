<html>
<head>
    <parameter name="returnUrlPath" value="${grailsApplication.config.grails.serverURL}"/>
    <r:require modules="knockout,attachDocuments"/>
</head>
<body>
    <h3>List</h3>
    <g:render template="/resource/attachDocument"/>
    %{--<g:render template="/resource/listDocuments"/>--}%
</body>

<r:script disposition="head">
    console.log('Loaded list.gsp');
</r:script>

</html>

