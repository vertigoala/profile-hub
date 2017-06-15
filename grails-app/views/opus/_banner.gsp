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
    </style>
</g:if>
<div class="banner-wrapper">
    <div class="banner">
        <div class="banner-container" style="max-height: ${bannerItems.bannerHeight}px">
            <g:if test="${bannerItems.doMainBanner}"> %{-- && it.type == 'css' --}%
                <g:each status="index" in="${bannerItems?.banners}" var="it">
                    <div class="bg-item" style="
                    background-image: url(${it.imageUrl});
                    animation-name: fader-${bannerItems.uuid};
                    animation-iteration-count: infinite;
                    animation-duration: ${bannerItems.totalDuration / 1000}s;
                    animation-delay: ${index * (bannerItems?.fadeDuration + bannerItems?.displayDuration) / 1000}s;"></div>
                </g:each>
            </g:if>
            <g:elseif test="${!bannerItems.doMainBanner}">
                <div class="bg-item" style="background-image: url(${bannerItems?.banners?.first()?.imageUrl});"></div>
            </g:elseif>
            <div class="banner-gradient">
                <div class="banner-logo-container">
                    <div class="banner-logo-title">
                        ${ bannerItems.overlayText ? raw(bannerItems.overlayText) : (opus.title ?: opus.uuid) }
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>