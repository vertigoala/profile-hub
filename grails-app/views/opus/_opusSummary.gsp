<div class="row padding-bottom-2">
    <div class="col-lg-8 col-md-6 col-sm-6" ng-cloak>
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

    <div class="col-lg-4 col-md-6 col-sm-6 col-xs-12">
        <div class="main-stats" ng-cloak ng-controller="StatisticsController as statsCtrl">
            <div class="main-stats__stat col-lg-4 col-md-4 col-sm-4 col-xs-6"
                ng-repeat="stat in statsCtrl.statistics">
                <h4 class="stat__title heading-underlined">{{stat.name}}
                <span class="small fa fa-info-circle color--medium-blue" title="{{stat.caveat}}" ng-show="stat.caveat"></span>
                </h4>
                <div class="stat__number" title="{{stat.tooltip}}">{{stat.value}}</div>
            </div>
        </div>
    </div>
</div>


<div class="row">
    <div class="col-md-12">

    </div>
</div>
