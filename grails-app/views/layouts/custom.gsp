<!DOCTYPE html>
<html lang="en" ng-app="profileEditor">
<head>
    <base href="${request.contextPath}/">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="app.version" content="${g.meta(name: 'app.version')}"/>
    <meta name="app.build" content="${g.meta(name: 'app.build')}"/>
    <meta name="description" content="Atlas of Living Australia"/>
    <meta name="author" content="Atlas of Living Australia">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="http://www.ala.org.au/wp-content/themes/ala2011/images/favicon.ico" rel="shortcut icon"
          type="image/x-icon"/>
    <title><g:layoutTitle/></title>
    <r:require modules="profiles"/>
    <r:layoutResources/>
    <g:layoutHead/>
    <style type="text/css">
    #banner-image {
        background-image: url(${bannerUrl ?: grailsApplication.config.images.service.url + '/store/7/4/4/e/a08a52f2-7bbe-40d9-8f1a-fe8acb28e447/original'});
    }
    </style>
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body id="${pageProperty(name: 'body.id')}" onload="${pageProperty(name: 'body.onload')}">

<g:if test="${application.getAttribute("alaAdminMessage")}">
    <div class="padding-top-1" ng-cloak>
        <alert type="danger admin-message">
            <span class="admin-message-text">${application.getAttribute("alaAdminMessage")}</span> &nbsp;&nbsp;(${application.getAttribute("alaAdminMessageTimestamp")})
        </alert>
    </div>
</g:if>

<!-- Navbar -->
<nav class="navbar navbar-default" id="primary-nav">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse"
                    data-target="#bs-example-navbar-collapse-1">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <span class="navbar-brand">${pageTitle}</span>

        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <g:if test="${glossaryUrl}">
                    <li><a href="${glossaryUrl}" target="_blank">Glossary</a></li>
                </g:if>
                <g:if test="${aboutPageUrl}">
                    <li><a href="${aboutPageUrl}" target="_blank">About&nbsp;&nbsp;</a></li>
                </g:if>
            </ul>

            <small>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="${request.contextPath}/">Profile collections</a></li>
                    <g:render template="../layouts/login"/>
                </ul>
            </small>
        </div>
    </div>
</nav>

<div class="jumbotron" id="banner-image"></div>

<div class="container" id="main-content">
    <h1 class="hidden">Welcome to the eFlora website</h1>

    <g:layoutBody/>

    <div class="row"></div>
    <footer class="main-footer">
        <div class="container margin-bottom-2">
            <div class="main-footer-border"></div>

            <div class="row">
                <div class="col-xs-12 col-sm-4 col-lg-3 margin-bottom-2 site-logo" id="site-logo">
                    <img class="img-responsive customizable-logo-img"
                         src="${logoUrl ?: 'http://root.ala.org.au/bdrs-core/files/download.htm?className=au.com.gaiaresources.bdrs.model.theme.Theme&id=217&fileName=processed/images/bdrs/atlasoflivingaust.png'}"
                         alt="${logoAlt ?: 'logo'}"/>
                </div>

                <div class="col-xs-12 col-sm-8 col-lg-9">

                    <div class="col-md-12 col-lg-6">
                        <p class="lead">
                            <g:if test="${footerText}">
                                ${raw(footerText)}
                            </g:if>
                        </p>

                        <p class="lead">
                            ALA: sharing biodiversity knowledge to shape our future.
                        </p>
                    </div>

                    <div class="col-sm-6 col-md-6 col-lg-3">
                        <ul class="link-list">
                            <li class="heading">Site navigation</li>
                            <li><a href="${request.contextPath}/">Collections</a></li>
                            <g:if test="${glossaryUrl}">
                                <li><a href="${glossaryUrl}" target="_blank">Glossary</a></li>
                            </g:if>
                        </ul>
                    </div>

                    <div class="col-sm-6 col-md-6 col-lg-3">
                        <ul class="link-list">
                            <li class="heading">Site information</li>
                            <g:if test="${aboutPageUrl}">
                                <li><a href="${aboutPageUrl}" target="_blank">About Us</a></li>
                            </g:if>
                            <g:else>
                                <li><a href="http://www.ala.org.au/about-the-atlas/contact-us/"
                                       target="_blank">Get in touch</a></li>
                            </g:else>
                        </ul>
                    </div>

                    <div class="col-xs-12 col-sm-7 col-md-6">
                        <g:if test="${contact}">
                            <ul class="social list-inline">
                                <g:if test="${contact.facebook}">
                                    <li><a class="soc-facebook" href="${contact.facebook}"
                                           title="Facebook" target="_blank"><i class="fa fa-facebook"></i></a></li>
                                </g:if>
                                <g:if test="${contact.twitter}">
                                    <li><a class="soc-twitter" href="${contact.twitter}" title="Twitter"
                                           target="_blank"><i class="fa fa-twitter"></i></a></li>
                                </g:if>
                                <g:if test="${contact.email}">
                                    <li><a class="soc-envelope" href="${contact.email.contains('@') ? 'mailto:' + contact.email : contact.email}"
                                           title="Email" target="_blank"><i class="fa fa-envelope"></i></a></li>
                                </g:if>
                            </ul>
                        </g:if>
                    </div>
                </div>
            </div>
        </div>
    </footer>
</div>


<script type="text/javascript">
    var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
    document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<r:script>
    var pageTracker = _gat._getTracker("UA-4355440-1");
    pageTracker._initData();
    pageTracker._trackPageview();

    // show warning if using IE6
    if ($.browser && $.browser.msie && $.browser.version.slice(0, 1) == '6') {
        $('#header').prepend($('<div style="text-align:center;color:red;">WARNING: This page is not compatible with IE6.' +
        ' Many functions will still work but layout and image transparency will be disrupted.</div>'));
    }
</r:script>

<r:script>
    angular.module('app.config', []).constant('config', {
        contextPath: '${request.contextPath}',
        readonly: ${!edit},
        currentUser: '${params.currentUser}',
        profileServiceUrl: '${grailsApplication.config.profile.service.url}',
        keybaseProjectUrl: '${grailsApplication.config.keybase.project.lookup}',
        nslNameUrl: '${grailsApplication.config.nsl.name.url.prefix}',
        isOpusReviewer: '${params.isOpusReviewer}'
     });
</r:script>

<!-- JS resources-->
<r:layoutResources/>

</body>
<script type='text/javascript'>
    (function (d, t) {
        var bh = d.createElement(t), s = d.getElementsByTagName(t)[0];
        bh.type = 'text/javascript';
        bh.src = '//www.bugherd.com/sidebarv2.js?apikey=kqamg3xuhww6j6zrpthdmw';
        s.parentNode.insertBefore(bh, s);
    })(document, 'script');
</script>
</html>
