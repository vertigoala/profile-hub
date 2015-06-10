<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <title>${profile.scientificName}</title>

    <link rel="stylesheet" href="${resource(dir: '/thirdparty/bootstrap/css/bootstrap-3.1.1.min.css', absolute: true,)}"
          type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/nsl.css', absolute: true)}" type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/profiles.css', absolute: true)}" type="text/css"/>
    <link rel="stylesheet" href="${resource(dir: '/css/pdf.css', absolute: true)}" type="text/css"/>
</head>

<body>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span12">
            <div class="pull-left small">${opus.title}</div>
            <div class="pull-right small">This document was produced on ${new Date().format("dd/MM/yyyy")}.</div>
        </div>
    </div>
</div>
<hr/>
</body>
</html>
