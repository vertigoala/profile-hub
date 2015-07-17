<div class="row padding-bottom-2">
    <div class="col-md-8" ng-cloak>
        <p class="lead">
            {{opusCtrl.opus.description}}
        </p>
        <g:if test="${aboutPageUrl}">
            <p class="margin-bottom-2">
                To find more information about the {{opusCtrl.opus.title}}, visit <a href="${aboutPageUrl}"
                                                                                     target="_blank">our About page</a>.
            </p>
        </g:if>
    </div>

    <div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">
        <div class="main-stats" ng-cloak>
            <div class="main-stats__stat col-lg-6 col-md-6 col-sm-3 col-xs-3">
                <h4 class="stat__title heading-underlined">Profiles</h4>

                <div class="stat__number">{{opusCtrl.opus.profileCount}}</div>
            </div>

            <div class="main-stats__stat col-lg-6 col-md-6 col-sm-3 col-xs-3">
                <h4 class="stat__title heading-underlined">Editors</h4>

                <div class="stat__number">{{opusCtrl.editors.length}}</div>
            </div>

            %{--<div class="main-stats__stat col-lg-6 col-md-6 col-sm-3 col-xs-3">--}%
                %{--<h4 class="stat__title heading-underlined">Another statistic</h4>--}%

                %{--<div class="stat__number">?</div>--}%
            %{--</div>--}%

            %{--<div class="main-stats__stat col-lg-6 col-md-6 col-sm-3 col-xs-3">--}%
                %{--<h4 class="stat__title heading-underlined">Another statistic</h4>--}%

                %{--<div class="stat__number">?</div>--}%
            %{--</div>--}%
        </div>
    </div>
</div>


<div class="row">
    <div class="col-md-12">

    </div>
</div>