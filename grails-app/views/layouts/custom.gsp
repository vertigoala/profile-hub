<!DOCTYPE html>
<html lang="en">
<head>
    <base href="${request.contextPath}/">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="app.version" content="${g.meta(name: 'app.version')}"/>
    <meta name="app.build" content="${g.meta(name: 'app.build')}"/>
    <meta name="description" content="Atlas of Living Australia"/>
    <meta name="author" content="Atlas of Living Australia">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="http://www.ala.org.au/wp-content/themes/ala2011/images/favicon.ico" rel="shortcut icon"
          type="image/x-icon"/>
    <title><g:layoutTitle/></title>
    <r:require modules="profiles, bootstrap"/>
    <r:layoutResources/>
    <g:layoutHead/>
    <style type="text/css">
        .customizable-banner {
            background-image: url(${bannerUrl ?: grailsApplication.config.images.service.url + '/store/7/4/4/e/a08a52f2-7bbe-40d9-8f1a-fe8acb28e447/original'});
        }
    </style>
</head>

<body id="${pageProperty(name: 'body.id')}" onload="${pageProperty(name: 'body.onload')}">
<g:set var="fluidLayout"
       value="${pageProperty(name: 'meta.fluidLayout') ?: grailsApplication.config.skin?.fluidLayout}"/>

<div class="customizable-banner">
    <div class="row-fluid span12">
        <div class="span6">
            <div class="customizable-logo pull-left">
                <img class="customizable-logo-img" src="${logoUrl ?: grailsApplication.config.ala.base.url + '/wp-content/themes/ala2011/images/logo.png'}" alt="${logoAlt ?: 'logo'}"/>
            </div>
        </div>

    </div>

    <div class="row-fluid">
        <div class="span12 customizable-subbanner">
            <div class="span6 pull-left customizable-subbanner-title">
                ${pageTitle}
            </div>
            <div class="span6">
                <g:if test="${glossaryUrl}">
                    <a href="${glossaryUrl}" class="pull-right white" target="_blank">Glossary</a>
                </g:if>
            </div>

        </div>
    </div>
</div>



<div class="vertical-pad"/>
<div class="${fluidLayout ? 'container-fluid' : 'container'}" id="main-content">
    <g:layoutBody/>
</div><!--/.container-->

<div class="${fluidLayout ? 'container-fluid' : 'container'} hidden-desktop">
    <%-- Borrowed from http://marcusasplund.com/optout/ --%>
    <a class="btn btn-small toggleResponsive"><i class="icon-resize-full"></i> <span>Desktop</span> version</a>
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
        profileServiceUrl: '${grailsApplication.config.profile.service.url}'
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
