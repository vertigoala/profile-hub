<g:if test="${bannerItems.doMainBanner  && bannerItems?.banners?.size() > 1 && bannerItems?.banners?.keyframes}">
    %{-- TODO replace with web animations api --}%
    <style id="fader-${bannerItems.uuid}">
        @keyframes fader-${bannerItems.uuid} {
        <g:each in="${bannerItems.keyframes}" var="kf" status="i">
            ${(kf.offset ?: i / (bannerItems.keyframes.size() - 1)) * 100.0}% {
                opacity: ${kf.opacity};
            }
        </g:each>
        }
        .fader-${bannerItems.uuid} {
            animation-name: fader-${bannerItems.uuid};
            animation-iteration-count: infinite;
            animation-duration: ${bannerItems.totalDuration}ms;
        }
    </style>
</g:if>
<div class="banner">
    <div class="banner-container">
        <g:if test="${bannerItems.doMainBanner}"> %{-- && it.type == 'css' --}%
            <g:each status="index" in="${bannerItems?.banners?.reverse()}" var="it">
                <div class="bg-item fader-${bannerItems.uuid}" style="
                background-image: url(${it.imageUrl});
                animation-delay: ${(bannerItems?.banners.size() - index - 1) * (bannerItems?.fadeDuration + bannerItems?.displayDuration)}ms;"></div>
            </g:each>
        </g:if>
        <g:elseif test="${!bannerItems.doMainBanner}">
            <div class="bg-item" style="background-image: url(${bannerItems?.banners?.first()?.imageUrl});"></div>
        </g:elseif>
        <div class="banner-gradient">
            <div class="banner-logo-container" style="${bannerItems?.minHeight ? "min-height: ${bannerItems.minHeight};" : ''}">
                <div class="banner-logo-title">
                    ${ bannerItems.overlayText ? raw(bannerItems.overlayText) : (opus.title ?: opus.uuid) }
                </div>
            </div>
        </div>
    </div>
</div>