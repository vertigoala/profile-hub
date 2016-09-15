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

    <script type="text/ng-template" id="editItemPopup.html">
    <div class="modal-header">
        <h4 class="modal-title">Glossary Item</h4>
        <close-modal close="glossaryModalCtrl.cancel()"></close-modal>
    </div>

    <div class="modal-body">
        <alert class="alert-danger" ng-show="glossaryModalCtrl.error">{{ glossaryModalCtrl.error }}</alert>

        <div class="form-horizontal">
            <div class="form-group">
                <label for="term" class="col-sm-3 control-label">Term</label>

                <div class="col-sm-8">
                    <input id="term" type="text" ng-model="glossaryModalCtrl.item.term" class="form-control"
                           ng-enter="glossaryModalCtrl.ok()" ng-disabled="glossaryModalCtrl.item.uuid"/>
                </div>
            </div>
        </div>

        <div class="form-horizontal">
            <div class="form-group">
                <label for="description" class="col-sm-3 control-label">Description</label>

                <div class="col-sm-8">
                    <textarea id="description" type="text" ng-model="glossaryModalCtrl.item.description"
                              class="form-control"
                              ng-enter="glossaryModalCtrl.ok()" rows="6"></textarea>
                </div>
            </div>
        </div>
    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" ng-click="glossaryModalCtrl.ok()">OK</button>
        <button class="btn btn-default" ng-click="glossaryModalCtrl.cancel()">Cancel</button>
    </div>
    </script>
</div>

</body>
</html>

