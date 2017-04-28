
<form method="get" action="opus/${params.opusId}/search" class="margin-bottom-1">
    <input type="submit" class="btn btn-default btn-lg btn-block" value="Search"  tooltip="${opus.opusLayoutConfig.helpTextSearch}" tooltip-placement="right" tooltip-append-to-body="true"/>
</form>

<form method="get" action="opus/${params.opusId}/browse" class="margin-bottom-1">
    <input type="submit" class="btn btn-default btn-lg btn-block" value="Browse"  tooltip="${opus.opusLayoutConfig.helpTextBrowse}" tooltip-placement="right" tooltip-append-to-body="true"/>
</form>

<g:if test="${opus.keybaseProjectId != null}">
<form method="get" action="opus/${params.opusId}/identify" class="margin-bottom-1">
    <input type="submit" class="btn btn-default btn-lg btn-block" value="Identify"  tooltip="${opus.opusLayoutConfig.helpTextIdentify}" tooltip-placement="right" tooltip-append-to-body="true"/>
</form>
</g:if>

<form method="get" action="opus/${params.opusId}/documents">
    <input type="submit" class="btn btn-default btn-lg btn-block" value="Context"  tooltip="${opus.opusLayoutConfig.helpTextDocuments}" tooltip-placement="right" tooltip-append-to-body="true"/>
</form>