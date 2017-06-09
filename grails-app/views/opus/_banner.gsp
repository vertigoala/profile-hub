<div class="banner" style="max-height: ${bannerItems.bannerHeight}px">
    <g:if test="${bannerItems.opusLogoUrl}">
        <div class="logo">
            <img class="img-responsive" src="${bannerItems.opusLogoUrl}"
                 style="max-height: ${bannerItems.bannerHeight}px" alt="Collection logo">
        </div>
    </g:if>
    <div class="banner-items">
        <g:each status="index" in="${bannerItems?.banners}" var="it">
            <g:if test="${doMainBanner && it.gradient && it.gradientWidth}">
                <div style="background-image: linear-gradient(to right, ${it.gradient}, rgba(0, 0, 0, 0) ${it.gradientWidth}%, rgba(0,0,0,0) 100%)"></div>
            </g:if>
            <g:elseif test="${doMainBanner && it.type == 'css'}">
                <div style="
                     background-image: url(${it.url});
                     animation-name: fader-${it.uuid};
                     animation-iteration-count: infinite;
                     animation-duration: ${it.totalDuration / 1000}s;
                     animation-delay: ${index * (it.fadeDuration + it.displayDuration) / 1000}s;"></div>
            </g:elseif>
            <g:elseif test="${!doMainBanner}">
                <div style="background-image: url(${it.imageUrl});"></div>
            </g:elseif>
        </g:each>
    </div>
</div>