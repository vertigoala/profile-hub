    <parameter name="returnUrlPath" value="${grailsApplication.config.grails.serverURL}"/>
    <r:require modules="knockout,attachDocuments"/>

<h2>Resources</h2>
<g:render template="/resource/listDocuments"/>
<g:render template="/resource/attachDocument"/>
<r:layoutResources/>

