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

    <script>
        // http://madalgo.au.dk/~jakobt/wkhtmltoxdoc/wkhtmltopdf-0.9.9-doc.html
        function pageNumbers() {
            var vars = {};
            var x = document.location.search.substring(1).split('&');
            for (var i in x) {
                var z = x[i].split('=', 2);
                vars[z[0]] = unescape(z[1]);
            }
            var x = ['frompage', 'topage', 'page', 'webpage', 'section', 'subsection', 'subsubsection'];
            for (var i in x) {
                var y = document.getElementsByClassName(x[i]);
                for (var j = 0; j < y.length; ++j) y[j].textContent = vars[x[i]];
            }
        }
    </script>
</head>

<body onload="pageNumbers()">
<div class="container-fluid">
    <div class="row-fluid small">
        <div class="span12">
            <hr/>
            <g:if test="${opus.copyrightText}">
                <div class="center">${raw(opus.copyrightText)}</div>
            </g:if>
            <div class="pull-right"><span class="page"></span></div>
        </div>
    </div>
</div>
</body>
</html>
