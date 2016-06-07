<%@ page import="grails.converters.JSON" %>

<div class="row-fluid row-eq-height" id="resourceList">

        <div class="span4">

                <div class="btn-toolbar text-right">
                        <div class="input-prepend input-append text-left">
                                <span class="add-on"><i class="fa fa-filter"></i></span>
                                <input type="text" data-bind="textInput: documentFilter">
                                <div class="btn-group">
                                        <button type="button" class="btn dropdown-toggle" data-toggle="dropdown">
                                                <span data-bind="text: documentFilterField().label"></span>
                                                <span class="caret"></span>
                                        </button>
                                        <ul class="dropdown-menu" data-bind="foreach: documentFilterFieldOptions">
                                                <li><a data-bind="{ text: $data.label, click: $parent.documentFilterField }"></a></li>
                                        </ul>
                                </div>
                        </div>
                </div>
                <p/>
                <div class="well well-small fc-docs-list-well">
                        <!-- ko if: filteredDocuments().length == 0 -->
                        <h4 class="text-center">No documents</h4>
                        <!-- /ko -->
                        <ul class="list-group nav nav-list fc-docs-list" data-bind="foreach: { data: filteredDocuments }">
                                <li class="list-group-item pointer " data-bind="{ click: $parent.selectDocument, css: { active: $parent.selectedDocument() == $data } }">
                                        <div class="clearfix space-after media" data-bind="template:$parent.documentTemplate($data)"></div>
                                </li>
                        </ul>
                </div>
                <p/>
                <g:if test="${admin}"><button class="btn btn-default" data-bind="click:attachDocument">New Resource</button></g:if>
        </div>
        <div class="span8">
                <div class="fc-resource-preview-container" data-bind="{ template: { name: previewTemplate } }"/>
        </div>
</div>

<script id="iframeViewer" type="text/html">
<div class="well fc-resource-preview-well">
        <iframe class="fc-resource-preview" data-bind="attr: {src: selectedDocumentFrameUrl}">
                <p>Your browser does not support iframes <i class="fa fa-frown-o"></i>.</p>
        </iframe>
</div>
</script>

<script id="xssViewer" type="text/html">
<div class="well fc-resource-preview-well" data-bind="html: selectedDocument().embeddedVideo"></div>
</script>

<script id="noPreviewViewer" type="text/html">
<div class="well fc-resource-preview-well">
        <p>There is no preview available for this file.</p>
</div>
</script>

<script id="noViewer" type="text/html">
<div class="well fc-resource-preview-well">
        <p>Select a document to preview it here.</p>
</div>
</script>

<g:render template="/resource/documentTemplate"></g:render>


<r:script>
    console.log ('Loading listDocuments config ');
    var initialised;
    $(window).load(function () {


        var options = {
            imageLocation:"${resource(dir:'/images', plugin: 'document-preview-plugin')}",
            pdfgenUrl: "${createLink(controller: 'preview', action: 'pdfUrl')}",
            pdfViewer: "${createLink(controller: 'preview', action: 'viewer')}",
            imgViewer: "${createLink(controller: 'preview', action: 'imageviewer')}",
            audioViewer: "${createLink(controller: 'preview', action: 'audioviewer')}",
            videoViewer: "${createLink(controller: 'preview', action: 'videoviewer')}",
            errorViewer: "${createLink(controller: 'preview', action: 'error')}",
            %{--documentUpdateUrl: "${createLink(controller: '${updateController}', action:'${updateAction}')}",--}%
            %{--documentDeleteUrl: "${g.createLink(controller:'${deleteController}', action:'${deleteAction}')}",--}%
            documentUpdateUrl: '<g:createLink controller="${updateController}" action="${updateAction}"  />',
            documentDeleteUrl: '<g:createLink controller="${deleteController}" action="${deleteAction}"  />',
            parentId: '${parentId}',

            admin: ${admin || false}
}

    console.log('Before parsing');
    var documents = JSON.parse('${documents.toString()}');
    //var documents = JSON.parse('\u005b\u007b\u0022filepath\u0022:\u00222016-04\u0022\u002c\u0022status\u0022:\u0022active\u0022\u002c\u0022labels\u0022:\u005b\u005d\u002c\u0022lastUpdated\u0022:\u00222016-04-14T04:41:36Z\u0022\u002c\u0022contentType\u0022:\u0022image\u002fpng\u0022\u002c\u0022type\u0022:\u0022image\u0022\u002c\u0022isPrimaryProjectImage\u0022:false\u002c\u0022url\u0022:\u0022https:\u002f\u002fecodata-test.ala.org.au\u002fuploads\u002f2016-04\u002fScreen%20Shot%202016-02-04%20at%2010.34.23%20PM.png\u0022\u002c\u0022thirdPartyConsentDeclarationMade\u0022:false\u002c\u0022id\u0022:\u0022570f1f80e4b0475d032c5bc2\u0022\u002c\u0022thirdPartyConsentDeclarationText\u0022:\u0022null\u0022\u002c\u0022filesize\u0022:83033\u002c\u0022readOnly\u0022:false\u002c\u0022thumbnailUrl\u0022:\u0022https:\u002f\u002fecodata-test.ala.org.au\u002fuploads\u002f2016-04\u002fthumb_Screen%20Shot%202016-02-04%20at%2010.34.23%20PM.png\u0022\u002c\u0022name\u0022:\u0022Test\u0022\u002c\u0022dateCreated\u0022:\u00222016-04-14T04:41:36Z\u0022\u002c\u0022filename\u0022:\u0022Screen Shot 2016-02-04 at 10.34.23 PM.png\u0022\u002c\u0022role\u0022:\u0022mdba\u0022\u002c\u0022systemId\u0022:\u0022mdba\u0022\u002c\u0022documentId\u0022:\u00225a9f0657-1fd8-410e-8726-de482d080d60\u0022\u002c\u0022isSciStarter\u0022:false\u007d\u002c\u007b\u0022filepath\u0022:\u00222016-04\u0022\u002c\u0022status\u0022:\u0022active\u0022\u002c\u0022labels\u0022:\u005b\u005d\u002c\u0022lastUpdated\u0022:\u00222016-04-14T05:34:03Z\u0022\u002c\u0022contentType\u0022:\u0022application\u002fmsword\u0022\u002c\u0022type\u0022:\u0022application\u0022\u002c\u0022isPrimaryProjectImage\u0022:false\u002c\u0022url\u0022:\u0022https:\u002f\u002fecodata-test.ala.org.au\u002fuploads\u002f2016-04\u002f0_Testing%20with%20a%20space.doc\u0022\u002c\u0022thirdPartyConsentDeclarationMade\u0022:false\u002c\u0022id\u0022:\u0022570f2bcbe4b0475d032c5bd4\u0022\u002c\u0022thirdPartyConsentDeclarationText\u0022:\u0022null\u0022\u002c\u0022filesize\u0022:21842\u002c\u0022readOnly\u0022:false\u002c\u0022name\u0022:\u0022Testing with a space\u0022\u002c\u0022dateCreated\u0022:\u00222016-04-14T05:34:03Z\u0022\u002c\u0022filename\u0022:\u00220_Testing with a space.doc\u0022\u002c\u0022role\u0022:\u0022mdba\u0022\u002c\u0022systemId\u0022:\u0022mdba\u0022\u002c\u0022documentId\u0022:\u00222a8e40ac-b523-4ce1-96a0-886eb89f8449\u0022\u002c\u0022isSciStarter\u0022:false\u007d\u002c\u007b\u0022filepath\u0022:\u00222016-04\u0022\u002c\u0022status\u0022:\u0022active\u0022\u002c\u0022labels\u0022:\u005b\u005d\u002c\u0022lastUpdated\u0022:\u00222016-04-14T07:16:09Z\u0022\u002c\u0022contentType\u0022:\u0022image\u002fjpeg\u0022\u002c\u0022type\u0022:\u0022image\u0022\u002c\u0022isPrimaryProjectImage\u0022:false\u002c\u0022url\u0022:\u0022https:\u002f\u002fecodata-test.ala.org.au\u002fuploads\u002f2016-04\u002fMDBA_AG_crest_RGB_inline_small.jpg\u0022\u002c\u0022thirdPartyConsentDeclarationMade\u0022:false\u002c\u0022id\u0022:\u0022570f43b8e4b0475d032c5bdc\u0022\u002c\u0022thirdPartyConsentDeclarationText\u0022:\u0022null\u0022\u002c\u0022filesize\u0022:37490\u002c\u0022readOnly\u0022:false\u002c\u0022thumbnailUrl\u0022:\u0022https:\u002f\u002fecodata-test.ala.org.au\u002fuploads\u002f2016-04\u002fthumb_MDBA_AG_crest_RGB_inline_small.jpg\u0022\u002c\u0022name\u0022:\u0022Test doc 2\u0022\u002c\u0022dateCreated\u0022:\u00222016-04-14T07:16:09Z\u0022\u002c\u0022filename\u0022:\u0022MDBA_AG_crest_RGB_inline_small.jpg\u0022\u002c\u0022role\u0022:\u0022mdba\u0022\u002c\u0022systemId\u0022:\u0022mdba\u0022\u002c\u0022documentId\u0022:\u002291e513a6-e8e3-4cde-b3d0-8e3ee0312d7c\u0022\u002c\u0022isSciStarter\u0022:false\u007d\u002c\u007b\u0022status\u0022:\u0022active\u0022\u002c\u0022labels\u0022:\u005b\u005d\u002c\u0022embeddedVideo\u0022:\u0022\u003ciframe width=\u005c\u0022560\u005c\u0022 height=\u005c\u0022315\u005c\u0022 src=\u005c\u0022https:\u002f\u002fwww.youtube.com\u002fembed\u002fSi4f4waKW-M\u005c\u0022 frameborder=\u005c\u00220\u005c\u0022 allowfullscreen\u003e\u003c\u005cu002fiframe\u003e\u0022\u002c\u0022lastUpdated\u0022:\u00222016-04-22T02:36:58Z\u0022\u002c\u0022isPrimaryProjectImage\u0022:false\u002c\u0022url\u0022:\u0022\u0022\u002c\u0022thirdPartyConsentDeclarationMade\u0022:false\u002c\u0022id\u0022:\u002257198e4ae4b0c7a05c99cc4a\u0022\u002c\u0022thirdPartyConsentDeclarationText\u0022:\u0022\u007b\u007d\u0022\u002c\u0022readOnly\u0022:false\u002c\u0022name\u0022:\u0022Basin Champions video 1\u0022\u002c\u0022dateCreated\u0022:\u00222016-04-22T02:36:58Z\u0022\u002c\u0022role\u0022:\u0022mdba\u0022\u002c\u0022systemId\u0022:\u0022mdba\u0022\u002c\u0022documentId\u0022:\u0022f2fc30aa-7cb6-45a0-ad73-cc6eff4750b8\u0022\u002c\u0022isSciStarter\u0022:false\u007d\u005d');

    console.log(documents)
    var docListViewModel = new DocListViewModel(documents || [], options);

    console.log('-- Applying bindings');
    ko.applyBindings(docListViewModel, document.getElementById('resourceList'));

    console.log('-- Finished applying bindings');

});

</r:script>

