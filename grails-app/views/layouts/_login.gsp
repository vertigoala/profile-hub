<div class="span6">
    <div class="pull-right menu-bar">

        <g:if test="${params.currentUser}">
            <ul>
                <li class="dropdown">
                    <a class="dropdown-toggle"
                       id="loginOutDropdown"
                       role="button"
                       data-toggle="dropdown"
                       href="#">Welcome ${params.currentUser} <span class="fa fa-angle-double-down"></span></b></a>
                    <ul id="menu1"
                        class="dropdown-menu"
                        role="menu"
                        aria-labelledby="loginOutDropdown">
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
            </ul>

        </g:if>
        <g:else>
            <a href="${grailsApplication.config.security.cas.loginUrl}?service=${grailsApplication.config.serverURL}${request.forwardURI}">Login</a>
        </g:else>
    </div>
</div>