<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Welcome to Grails</title>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
</head>

<body>

<h1>Profiles</h1>

<p class="lead">
   A list of all the profile collections in the system.
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