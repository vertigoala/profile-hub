<!DOCTYPE html>
<html lang="en">
<g:render template="/layouts/custom-top" />

<div class="container ${request.forwardURI?.endsWith("update")?'public':''}" id="main-content">
    <g:layoutBody/>
</div>
<g:render template="/layouts/custom-bottom" />
</html>