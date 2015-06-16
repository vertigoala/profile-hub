<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <link rel="stylesheet" href="${resource(dir: '/thirdparty/bootstrap/css/bootstrap3.3.4.min.css', absolute: true)}"
          type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/nsl.css', absolute: true)}" type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/theme.css', absolute: true)}" type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/profiles.css', absolute: true)}" type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/pdf.css', absolute: true)}" type="text/css"/>
    <script src="${resource(dir: '/js/pdf.js', absolute: true)}" type="text/javascript"></script>

</head>

<body>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <g:if test="${opus.copyrightText}">
                <div class="center"><span class="small-text">${raw(opus.copyrightText)}</span></div>
            </g:if>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="pull-right"><span class="page"></span></div>
        </div>
    </div>
</div>
</body>
</html>
