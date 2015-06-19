<g:if test="${params.currentUser}">
<li class="dropdown font-xsmall">
    <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
        <i class="fa fa-cog fa-spin"></i>&ensp;User settings
        <span class="caret"></span>
    </a>
    <ul class="dropdown-menu" role="menu">
        <li role="presentation">
            <a role="menuitem"
               tabindex="-1"
               href="${grailsApplication.config.casServerName}/userdetails/myprofile">My Profile</a>
        </li>
        <g:if test="${params.isALAAdmin}">
            <li role="presentation">
                <a role="menuitem"
                   tabindex="-1"
                   href="${request.contextPath}/admin">Admin</a>
            </li>
        </g:if>
        <li role="presentation">
            <a role="menuitem"
               tabindex="-1"
               target="_self"
               href="${request.contextPath}/logout/logout?casUrl=${grailsApplication.config.security.cas.logoutUrl}&appUrl=${grailsApplication.config.serverURL}${request.forwardURI}">Logout</a>
        </li>
    </ul>
</li>
</g:if>
<g:else>
    <li><a href="${grailsApplication.config.security.cas.loginUrl}?service=${grailsApplication.config.serverURL}${request.forwardURI}">Login</a></li>
</g:else>