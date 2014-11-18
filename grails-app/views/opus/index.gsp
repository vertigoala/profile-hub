<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <meta name="logoUrl" content="${logoUrl}"/>
    <title>Profile collections | Atlas of Living Australia</title>
</head>

<body>

<h1>Profile collections</h1>

<p class="lead">
   A list of all the profile collections in this system.
</p>

<div class="row-fluid">
<ul>
<g:each in="${opui}" var="opus">
    <li>
        <g:link mapping="viewOpus" params="[uuid:opus.uuid]">${opus.title}</g:link>
    </li>
</g:each>
</ul>
</div>

</body>

</html>