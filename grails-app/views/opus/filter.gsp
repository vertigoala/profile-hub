<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile filter | Atlas of Living Australia</title>

</head>

<body>
<div>

<g:include controller="opus" action="opusSummaryPanel" params="[opusId: params.opusId]"/>
<auth:ifNotLoggedIn>
    <div class="alert alert-warning">
        <i class="fa fa-2x fa-exclamation-triangle"></i> You are not logged in.
        Any filter set will not persist beyond this browser session.
    </div>
</auth:ifNotLoggedIn>
<g:form uri="/opus/${params.opusId}/florulaList" method="POST">
    <div class="form-group">
        <label for="florulaListId">Filter</label>
        <g:select class="form-control" name="florulaListId" from="${lists}" value="${florulaListId}" optionKey="dataResourceUid" optionValue="listName" noSelection="[(null): '-- No filter --']" aria-describedby="helpBlock" />
        <span id="helpBlock" class="help-block">This collection will be filtered by the current list (for you only).  Filtering <strong>only</strong> allows profiles within the collection that have an a matched name in the list to be returned in search, browse results, and as keys; and shown as a profile page.  If a filter is currently active on the current collection an indicator will be present near the filter item in the nav bar.</span>
    </div>
    <button type="submit" class="btn btn-default">Submit</button>
</g:form>
</div>
</body>

</html>