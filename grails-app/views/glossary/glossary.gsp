<!DOCTYPE html>
<html>
<head>
    <r:require module="profiles"/>
    <meta name="layout" content="${grailsApplication.config.layout}"/>
    <title>Profile collections</title>
</head>

<body>

<div ng-app="profileEditor" ng-controller="GlossaryController as glossaryCtrl" ng-init="glossaryCtrl.loadGlossary()">
    <div class="row-fluid" ng-cloak>
        <div class="span6">

        </div>
        <g:render template="../layouts/login"/>
    </div>

    <div class="row-fluid">
        <div class="span12">
            <div class="btn-group center" style="display: flex">
                <label ng-repeat="prefix in ['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z']"
                       class="btn btn-info" ng-model="glossaryCtrl.page" btn-radio="prefix"
                       ng-click="glossaryCtrl.loadGlossary(prefix)">{{ prefix }}</label>
            </div>
        </div>
    </div>
    <g:if test="${params.isOpusAdmin}">
        <div class="row-fluid">
            <div class="pull-right">
                <button class="btn btn-success" ng-click="glossaryCtrl.addGlossaryItem($index)"><i
                        class="icon icon-plus icon-white"></i> Add item</button>
            </div>
        </div>
    </g:if>

    <p/>

    <div class="row-fluid {{$even ? 'even' : 'odd'}}" ng-repeat="item in glossaryCtrl.glossary.items" ng-cloak>
        <g:if test="${params.isOpusAdmin}">
            <div class="span1">
                <button class="btn-link fa fa-edit" ng-click="glossaryCtrl.editGlossaryItem($index)"
                        title="Edit glossary item"></button>
                <button class="btn-link fa fa-trash-o" ng-click="glossaryCtrl.deleteGlossaryItem($index)"
                        title="Delete glossary item"></button>
            </div>
        </g:if>
        <div class="span2">
            <a id="{{item.term}}"></a>
            {{ item.term }}
        </div>

        <div class="span9">
            <span ng-bind-html="item.description | sanitizeHtml"/>
        </div>
    </div>


    <script type="text/ng-template" id="editItemPopup.html">
    <div class="modal-header">
        <h3 class="modal-title">Glossary Item</h3>
    </div>

    <div class="modal-body">
        <alert class="alert-danger" ng-show="glossaryModalCtrl.error">{{ glossaryModalCtrl.error }}</alert>

        <div class="row-fluid">
            <div class="span3">
                <label for="term" class="inline-label">Term:</label>
            </div>

            <div class="span9">
                <input id="term" type="text" ng-model="glossaryModalCtrl.item.term" class="input-xlarge"
                       ng-enter="glossaryModalCtrl.ok()" ng-disabled="glossaryModalCtrl.item.uuid"/>
            </div>
        </div>

        <div class="row-fluid">
            <div class="span3">
                <label for="description" class="inline-label">Description:</label>
            </div>

            <div class="span9">
                <textarea id="description" type="text" ng-model="glossaryModalCtrl.item.description" class="input-xlarge"
                       ng-enter="glossaryModalCtrl.ok()" rows="6"></textarea>
            </div>
        </div>
    </div>

    <div class="modal-footer">
        <button class="btn btn-primary" ng-click="glossaryModalCtrl.ok()">OK</button>
        <button class="btn btn-warning" ng-click="glossaryModalCtrl.cancel()">Cancel</button>
    </div>
    </script>
</div>

</body>
</html>

