var profileEditor = angular.module('profileEditor', ['app.config', 'ui.bootstrap', 'leaflet-directive', 'colorpicker.module', 'angular-loading-bar', 'textAngular', 'duScroll', 'ngFileUpload', 'checklist-model']);

profileEditor.config(function () {

});

profileEditor.config(['cfpLoadingBarProvider', function (cfpLoadingBarProvider) {
    cfpLoadingBarProvider.includeSpinner = false;
}]);

profileEditor.run(function ($rootScope, config) {
    $rootScope.config = config;

    $rootScope.richTextToolbarFull = "[['h1','h2','h3','p'],['bold','italics','underline'],['ul','ol'],['indent', 'outdent'],['insertLink'],['male', 'female', 'plusMinus', 'endash'],['html'],['undo', 'redo']]";
    $rootScope.richTextToolbarSimple = "[['bold','italics','underline']]";
});

profileEditor.config(function($provide){
    $provide.decorator('taOptions', ['taRegisterTool', '$delegate', function(taRegisterTool, taOptions){
        taRegisterTool('male', {
            iconclass: "fa fa-mars",
            tooltiptext: "Insert male symbol",
            action: function(){
                insertTextAtCursor("\u2642");
                return moveCaret(1);
            }
        });

        taRegisterTool('female', {
            iconclass: "fa fa-venus",
            tooltiptext: "Insert female symbol",
            action: function(){
                insertTextAtCursor("\u2640");
                return moveCaret(1);
            }
        });

        taRegisterTool('plusMinus', {
            iconclass: "toolbar-plus-minus",
            tooltiptext: "Insert plus / minus symbol",
            action: function(){
                insertTextAtCursor("\u00B1");
                return moveCaret(1);
            }
        });

        taRegisterTool('endash', {
            iconclass: "toolbar-endash",
            tooltiptext: "Insert endash symbol",
            action: function(){
                insertTextAtCursor("\u2013");
                return moveCaret(1);
            }
        });

        return taOptions;
    }]);

    function insertTextAtCursor(text) {
        var sel, range;
        if (window.getSelection) {
            sel = window.getSelection();
            if (sel.getRangeAt && sel.rangeCount) {
                range = sel.getRangeAt(0);
                range.deleteContents();
                range.insertNode(document.createTextNode(text));
            }
        } else if (document.selection && document.selection.createRange) {
            document.selection.createRange().text = text;
        }
    }

    function moveCaret(charCount) {
        var sel, range;
        if (window.getSelection) {
            sel = window.getSelection();
            if (sel.rangeCount > 0) {
                var textNode = sel.focusNode;
                sel.collapse(textNode.nextSibling, charCount);
            }
        } else if ((sel = window.document.selection)) {
            if (sel.type != "Control") {
                range = sel.createRange();
                range.move("character", charCount);
                range.select();
            }
        }
    }

});

/**
 *  ALA Admin controller
 */
profileEditor.controller('ALAAdminController', function ($http, util) {
    var self = this;

    self.message = null;
    self.timestamp = null;

    var future = $http.get(util.contextRoot() + "/admin/message");
    future.then(function (response) {

        self.message = response.data.message;
        self.timestamp = response.data.timestamp;
    });

    self.postMessage = function () {
        $http.post(util.contextRoot() + "/admin/message", {message: self.message})
    };

    self.reloadConfig = function () {
        $http.post(util.contextRoot() + "/admin/reloadConfig")
    };
});