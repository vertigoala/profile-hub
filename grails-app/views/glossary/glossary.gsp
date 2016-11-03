<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>${pageTitle} Glossary</title>
</head>

<body>

<div ng-controller="GlossaryController as glossaryCtrl" ng-init="glossaryCtrl.loadGlossary()">
    <div class="margin-bottom-2"></div>
    <div class="row padding-bottom-2">
        <div class="col-md-10">
            <h2 class="heading-large inline">Glossary</h2>
        </div>

        <div class="col-md-2">
            <button class="btn btn-default pull-right margin-top-1" ng-click="glossaryCtrl.addGlossaryItem($index)"><i
                    class="fa fa-plus"></i> Add item</button>
        </div>
    </div>


    <div class="row" ng-cloak>

        <div class="col-md-2 margin-bottom-1">
            <ul class="nav nav-stacked" id="sidebar">
                <h4 class="font-xxsmall heading-underlined"><strong>Glossary index</strong></h4>

                <li ng-repeat="prefix in ['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z']">
                    <a href="" class="font-xxsmall" ng-model="glossaryCtrl.page" btn-radio="prefix"
                       ng-click="glossaryCtrl.loadGlossary(prefix)">{{ prefix }}</a></li>
            </ul>
        </div>

        <div class="col-md-10">
            <h4 class="font-xxsmall">Glossary Entry: {{glossaryCtrl.page | capitalize}}</h4>

            <div class="table-responsive">
                <table class="table table-striped table-hover">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Details</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr ng-repeat="item in glossaryCtrl.glossary.items">
                        <td>{{item.term}}</td>
                        <td><span ng-bind-html="item.description | sanitizeHtml"></span></td>
                        <td class="edits">
                            <button class="btn-link fa fa-edit" ng-click="glossaryCtrl.editGlossaryItem($index)"
                                    title="Edit glossary item"></button>
                            <button class="btn-link fa fa-trash-o color--red" ng-click="glossaryCtrl.deleteGlossaryItem($index)"
                                    title="Delete glossary item"></button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

</body>
</html>

