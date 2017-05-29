<%@ page import="grails.util.Environment" %>
<footer class="main-footer">
    <div class="container margin-bottom-2">
        <div class="main-footer-border"></div>

        <div class="row">
            <div class="col-xs-12 col-sm-6 col-md-4 margin-bottom-1 site-logo" id="site-logo">
                <g:each in="${logos?:[[logoUrl:asset.assetPath(src: "ala-logo-2016-inline.png")]]}" var="logo">
                    <div class="row margin-bottom-1">
                        <div class="col-xs-12">
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

            <div class="col-xs-12 col-sm-6 col-md-4 margin-bottom-1">
                <div class="margin-bottom-1">
                    <a href="https://www.gov.au">
                        <img src="${asset.assetPath(src: "au-gov-logo.png")}"
                             width="138" class="img-responsive" alt="ALA logo">
                    </a>
                </div>

                <g:if test="${footerText}">
                    <p class="lead">${raw(footerText)}</p>
                </g:if>

                <g:if test="${opus?.brandingConfig?.shortLicense}">
                    ${raw(opus.brandingConfig.shortLicense)}
                </g:if>

                <g:if test="${opus?.brandingConfig?.issn}">
                    <g:render template="../opus/issn" model="${[issn: opus.brandingConfig.issn]}"></g:render>
                </g:if>
            </div>

            <div class="col-xs-12 col-sm-6 col-md-4">
                <g:if test="${contact?.facebook || contact?.twitter || contact?.email}">
                    <ul class="social list-inline margin-bottom-1">
                        <g:if test="${contact.facebook}">
                            <li><a class="soc-facebook" href="${contact.facebook}"
                                   title="${opus ? 'Contact this collection via Facebook' : 'Contact the Atlas via Facebook'}"
                                   target="_blank"><i class="fa fa-facebook"></i></a></li>
                        </g:if>
                        <g:if test="${contact.twitter}">
                            <li><a class="soc-twitter" href="${contact.twitter}"
                                   title="${opus ? 'Contact this collection via Twitter' : 'Contact the Atlas via Twitter'}"
                                   target="_blank"><i class="fa fa-twitter"></i></a></li>
                        </g:if>
                        <g:if test="${contact.email}">
                            <li><a class="soc-envelope" href="${contact.email.contains('@') ? 'mailto:' + contact.email : contact.email}"
                                   title="${opus ? 'Email this collection' : 'Email the Atlas'}"
                                   target="_blank"><i class="fa fa-envelope"></i></a></li>
                        </g:if>
                    </ul>
                </g:if>

                <ul class="link-list">
                    <li class="heading">Site navigation</li>
                    <li><a href="${request.contextPath}/">Collections</a></li>
                    <g:if test="${contact.email}">
                        <li><a href="${contact.email.contains('@') ? 'mailto:' + contact.email : contact.email}">Contact Us</a></li>
                    </g:if>
                </ul>
            </div>
        </div>
    </div>
</footer>

<asset:script type="text/javascript">
    // show warning if using IE6
    if ($.browser && $.browser.msie && $.browser.version.slice(0, 1) < 11) {
        $('#header').prepend($('<div style="text-align:center;color:red;">WARNING: This page is not compatible with IE10 or below.' +
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
        defaultOpusLogo: '${assetPath(src: "no-logo.svg")}',
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
