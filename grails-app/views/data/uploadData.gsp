<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Upload data</title>
</head>

<body>

<div ng-controller="DataController as dataCtrl">
    <ol class="breadcrumb" ng-cloak>
        <li><a class="font-xxsmall" href="${request.contextPath}/">Profile Collections</a></li>
        <li><a class="font-xxsmall"
               href="${request.contextPath}/opus/${opusId}">${pageTitle}</a>
        </li>
        <li class="font-xxsmall active">Upload data</li>
    </ol>

    <h1 class="hidden">Welcome to the eFlora website</h1><!-- Show the H1 on each page -->

    <div class="row">
        <div class="col-md-12">
            <h1 class="heading-large">Data upload</h1>
            <p>Upload occurrence data to be used in the ${pageTitle}. Data uploaded this way will only be visible within this collection: no other applications or sites will have access to this data.</p>
            <div class="radio">
                <label for="file" class="inline-label">
                    <input id="file" type="radio" name="file" ng-value="true"
                           ng-model="dataCtrl.uploadFile" class="ignore-save-warning">
                    Upload an Excel spreadsheet or a CSV file containing your data
                </label>
            </div>
            <div class="radio">
                <label for="paste" class="inline-label">
                    <input id="paste" type="radio" name="paste" ng-value="false"
                           ng-model="dataCtrl.uploadFile" class="ignore-save-warning">
                    Type or copy & paste comma-separated data
                </label>
            </div>
        </div>
    </div>

    <div class="embedded-sandbox">
        <div class="row embedded-sandbox" ng-show="dataCtrl.uploadFile">
            <div class="col-md-12">
                <h2>1. Select your data file</h2>
                <g:if test="${!fn}">
                    <web-component url="${grailsApplication.config.sandbox.base.url}/datacheck/upload"
                                   content-selectors=".container form"
                                   exclude-selectors=".fileupload .cancel,#optionsAfterDownload,link[href*='bootstrap'],link[href*='jquery'],link[href*='ala-styles']"
                                   opus-id="${opusId}">
                    </web-component>
                </g:if>
                <g:else>
                    <web-component url="${grailsApplication.config.sandbox.base.url}/datacheck/upload/preview/${id}"
                                   content-selectors=".container form"
                                   exclude-selectors=".fileupload .cancel,#optionsAfterDownload,link[href*='bootstrap'],link[href*='jquery'],link[href*='ala-styles']"
                                   opus-id="${opusId}">
                    </web-component>
                </g:else>
            </div>
        </div>
        <div class="row embedded-sandbox" ng-hide="dataCtrl.uploadFile">
            <div class="col-md-12">
                <web-component url="${grailsApplication.config.sandbox.base.url}/datacheck/"
                               content-selectors="#initialPaste,#recognisedDataDiv,#processSample,#processedData,#jsonBlob"
                               exclude-selectors="#optionsAfterDownload,link[href*='bootstrap'],link[href*='jquery'],link[href*='ala-styles']"
                               opus-id="${opusId}">
                </web-component>
            </div>
        </div>
    </div>
</div>

</body>

</html>