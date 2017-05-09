<%@ page import="grails.util.Environment" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <base href="${request.contextPath}/">
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Expires" content="-1">
    <meta name="app.version" content="${g.meta(name: 'app.version')}"/>
    <meta name="app.build" content="${g.meta(name: 'app.build')}"/>
    <meta name="description" content="Atlas of Living Australia"/>
    <meta name="author" content="Atlas of Living Australia">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="http://www.ala.org.au/wp-content/themes/ala2011/images/favicon.ico" rel="shortcut icon"
          type="image/x-icon"/>
    <title><g:layoutTitle/></title>
    <script>
        var CKEDITOR_BASEPATH = '${request.contextPath}/assets/ckeditor/';
    </script>
    <g:layoutHead/>
    <asset:stylesheet href="application.css" />
    <asset:javascript src="head.js" />
    <style type="text/css">
    #banner-image {
        background-image: url(${bannerUrl ?: grailsApplication.config.images.service.url + '/store/7/4/4/e/a08a52f2-7bbe-40d9-8f1a-fe8acb28e447/original'});
        min-height: ${bannerHeight ?: 100}px;
    }
    </style>
    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    %{-- Google Analytics --}%
    <script>
        window.ga=window.ga||function(){(ga.q=ga.q||[]).push(arguments)};ga.l=+new Date;
        ga('create', '${grailsApplication.config.googleAnalyticsId}', 'auto');
        ga('send', 'pageview');
    </script>
    <script async src='//www.google-analytics.com/analytics.js'></script>
    %{--End Google Analytics--}%
</head>

<body id="${pageProperty(name: 'body.id')}" onload="${pageProperty(name: 'body.onload')}" ng-app="profileEditor">

<ala:systemMessage/>

<div ng-controller="CustomAlertController" id="timeoutAlert" class="genericAlert">
    <alert ng-repeat="alert in alerts" type="{{alert.type}}" close="closeAlert($index)" ng-cloak>{{alert.msg}}</alert>
</div>

<div ng-controller="StayOnAlertController" id="stayOnAlert" class="genericAlert">
    <alert ng-repeat="alert in alerts" type="{{alert.type}}" close="closeAlert($index)" ng-cloak>{{alert.msg}}</alert>
</div>


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
            <g:if test="${opus}">
                <span class="navbar-brand">
                    <a href="${request.contextPath}/opus/${opus.shortName ?: opus.uuid}" title="${opus.title}">${opus.title}</a>
                </span>
            </g:if>
            <g:else>
                <span class="navbar-brand">${pageTitle}</span>
            </g:else>
        </div>

        <!-- Collect the nav links, forms, and other content for toggling -->
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <g:if test="${searchUrl}">
                    <li class="${pageName=='search'?'active':''}"><a href="${searchUrl}">Search</a></li>
                </g:if>
                <g:if test="${browseUrl}">
                    <li class="${pageName=='browse'?'active':''}"><a href="${browseUrl}">Browse</a></li>
                </g:if>
                <g:if test="${identifyUrl}">
                    <li class="${pageName=='identify'?'active':''}"><a href="${identifyUrl}">Identify</a></li>
                </g:if>
                <g:if test="${documentsUrl}">
                    <li class="${pageName=='documents'?'active':''}"><a href="${documentsUrl}">Context</a></li>
                </g:if>
                <g:if test="${glossaryUrl}">
                    <li class="${pageName=='glossary'?'active':''}"><a href="${glossaryUrl}" target="_blank">Glossary</a></li>
                </g:if>
                <g:if test="${aboutPageUrl && reportsUrl}">
                    <li class="dropdown font-xsmall ${pageName=='about'?'active':''}">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false">
                            More
                            <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu" role="menu">
                            <li class="${pageName=='about'?'active':''}"><a href="${aboutPageUrl}">About&nbsp;&nbsp;</a></li>
                            <li class="${pageName=='reports'?'active':''}"><a href="${reportsUrl}">Reports</a></li>
                        </ul>
                    </li>
                </g:if>
                <g:else>
                    <li class="${pageName=='about'?'active':''}"><a href="${aboutPageUrl}">About&nbsp;&nbsp;</a></li>
                </g:else>
            </ul>

            <small>
                <ul class="nav navbar-nav navbar-right">
                    <li><delegated-search></delegated-search></li>
                    <li><a href="${request.contextPath}/">Profile collections</a></li>
                    <g:render template="/layouts/login"/>
                    <li><p:help help-id="main"/></li>
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
                    <g:each in="${logos?:[[logoUrl:asset.assetPath(src: "atlasoflivingaust.png")]]}" var="logo">
                        <div class="row margin-bottom-1">
                            <div class="col-xs-12 col-sm-12 col-lg-12">
                                <g:if test="${logo.hyperlink}">
                                    <a href="${logo.hyperlink}" target="_blank">
                                        <img class="img-responsive customizable-logo-img"
                                             src="${logo.logoUrl}"
                                             alt="logo"/>
                                    </a>
                                </g:if>
                                <g:else>
                                    <img class="img-responsive customizable-logo-img"
                                         src="${logo.logoUrl}"
                                         alt="logo"/>
                                </g:else>
                            </div>
                        </div>
                    </g:each>
                </div>

                <div class="col-xs-12 col-sm-8 col-lg-9">

                    <div class="col-md-12 col-lg-6">
                        <g:if test="${footerText}">
                        <p class="lead">
                                ${raw(footerText)}
                        </p>
                        </g:if>

                        <p class="lead">
                            ALA: sharing biodiversity knowledge to shape our future.
                        </p>

                        <g:if test="${opus.brandingConfig.shortLicense}">
                            <p>
                                ${raw(opus.brandingConfig.shortLicense)}
                            </p>
                        </g:if>

                        <g:if test="${opus}">
                            <g:render template="../opus/issn" model="${[issn: opus.brandingConfig.issn]}"></g:render>
                        </g:if>
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
                        <g:if test="${aboutPageUrl}">
                            <ul class="link-list">
                                <li class="heading">Site information</li>
                                    <li><a href="${aboutPageUrl}" target="_blank">About Us</a></li>
                            </ul>
                        </g:if>
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

<asset:script type="text/javascript">
    // show warning if using IE6
    if ($.browser && $.browser.msie && $.browser.version.slice(0, 1) == '6') {
        $('#header').prepend($('<div style="text-align:center;color:red;">WARNING: This page is not compatible with IE6.' +
        ' Many functions will still work but layout and image transparency will be disrupted.</div>'));
    }
</asset:script>

<asset:script type="text/javascript">
    angular.module('app.config', []).constant('config', {
        contextPath: '${request.contextPath}',
        edit: ${!!edit},
        readonly: ${!edit},
        currentUser: '${params.currentUser}',
        currentUserId: '${params.currentUserId}',
        profileServiceUrl: '${grailsApplication.config.profile.service.url}',
        keybaseProjectUrl: '${grailsApplication.config.keybase.project.lookup}',
        imageServiceUrl: '${grailsApplication.config.images.service.url}',
        bieServiceUrl: '${grailsApplication.config.bie.base.url}',
        biocacheServiceUrl: '${opus && opus.usePrivateRecordData ? "${request.contextPath}${request.contextPath.endsWith("/") ? '' : '/'}opus/${opus.uuid}" : grailsApplication.config.biocache.base.url}',
        biocacheRecordUrl: '${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.occurrence.record.path}',
        nslNameUrl: '${grailsApplication.config.nsl.name.url.prefix}',
        isOpusReviewer: ${!!params.isOpusReviewer},
        isOpusAuthor: ${!!params.isOpusAuthor},
        isOpusEditor: ${!!params.isOpusEditor},
        listServiceUrl: '${grailsApplication.config.lists.base.url}',
        masterListType: '${grailsApplication.config.lists.masterlist.type ?: 'PROFILE' }',
        nslServiceUrlPrefix: '${grailsApplication.config.nsl.service.url.prefix}',
        nslNameUrlPrefix: '${grailsApplication.config.nsl.name.url.prefix}',
        nslServiceApniConceptSuffix: '${grailsApplication.config.nsl.service.apni.concept.suffix}',
        features: {publications: '${grailsApplication.config.feature.publications}',
                   imageUpload:'${grailsApplication.config.feature.feature.imageUpload}'},
        map: {mapId: '${grailsApplication.config.map.id}',
              accessKey: '${grailsApplication.config.map.access.key}'},
        mainCssFile: '${assetPath(src: "profiles.css")}',
        bootstrapCssFile: '${assetPath(src: "/bootstrap/css/bootstrap3.3.4.min.css")}',
        imageLoadErrorUrl: '${assetPath(src: "not-available.png")}',
        development: ${Environment.current == Environment.DEVELOPMENT}
     });
</asset:script>

<!-- JS resources-->
%{--<r:layoutResources/>--}%
<asset:javascript src="application.js" />
<asset:deferredScripts />

</body>
<script type='text/javascript'>
    <g:if test="${!excludeBugherd && !grailsApplication.config.bugherd.disabled}">
        (function (d, t) {
            var bh = d.createElement(t), s = d.getElementsByTagName(t)[0];
            bh.type = 'text/javascript';
            bh.src = '//www.bugherd.com/sidebarv2.js?apikey=kqamg3xuhww6j6zrpthdmw';
            s.parentNode.insertBefore(bh, s);
        })(document, 'script');
    </g:if>

    // This unsaved changes code relies on AngularJS adding the ng-dirty flag to fields as they are modified.
    $(window).bind('beforeunload', function() {
        var dirty = false;

        $(":not(form).ng-dirty").each(function (index, field) {
            var $field = $(field);

            if (!$field.hasClass("ignore-save-warning") && (!$field.is("div") || ($field.is("div") && $field.hasClass("dirty-check-container"))) && !$field.is("ul") && !$field.closest(".dualmultiselect")) {
                $field.addClass("show-dirty");
                dirty = true;
            }

            // handle CKE Text editors: the input field is a hidden textarea, followed by a number of divs and an iframe
            // with the rendered content. We need to highlight the nested div with the class 'cke_contents'
            if (!$field.hasClass("ignore-save-warning") && $field.is("textarea") && $field.next().hasClass("cke")) {
                $field.next().find(".cke_contents").addClass("show-dirty");
                dirty = true;
            }

            if (!$field.hasClass("ignore-save-warning") && ($field.attr("type") == "checkbox" || $field.attr("type") == "radio")) {
                $field.parent().addClass("show-dirty");
                dirty = true;
            }
        });

        if (dirty) {
            return "You have unsaved changes. These changes will be lost if you navigate away from this page."
        }
    })
</script>
</html>
