'use strict';

(function(angular, factory) {
  if (typeof define === 'function' && define.amd) {
    define(['angular', 'ckeditor'], function(angular) {
      return factory(angular);
    });
  } else {
    return factory(angular);
  }
}(angular || null, function(angular) {
  var app = angular.module('ngCkeditor', []);
  var $defer, loaded = false;

  app.run(['$q', '$timeout', function($q, $timeout) {
    $defer = $q.defer();

    if (angular.isUndefined(CKEDITOR)) {
      throw new Error('CKEDITOR not found');
    }
    CKEDITOR.disableAutoInline = true;

    function checkLoaded() {
      if (CKEDITOR.status === 'loaded') {
        loaded = true;
        $defer.resolve();
      } else {
        checkLoaded();
      }
    }

    CKEDITOR.on('loaded', checkLoaded);
    $timeout(checkLoaded, 100);
  }]);

  app.directive('ckeditor', ['$timeout', '$q', '$log', function($timeout, $q, $log) {

    return {
      restrict: 'AC',
      require: ['ngModel', '^?form'],
      scope: false,
      link: function(scope, element, attrs, ctrls) {
        var ngModel = ctrls[0];
        var form = ctrls[1] || null;
        var EMPTY_HTML = '<p></p>',
          isTextarea = element[0].tagName.toLowerCase() === 'textarea',
          data = [],
          isReady = false;

        if (!isTextarea) {
          element.attr('contenteditable', true);
        }

        var onLoad = function() {
          var options = {
            toolbar: 'full',
            toolbar_full: [ //jshint ignore:line
              {
                name: 'basicstyles',
                items: ['Bold', 'Italic', 'Strike', 'Underline']
              }, {
                name: 'paragraph',
                items: ['BulletedList', 'NumberedList', 'Blockquote']
              }, {
                name: 'editing',
                items: ['JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock']
              }, {
                name: 'links',
                items: ['Link', 'Unlink', 'Anchor']
              }, {
                name: 'tools',
                items: ['SpellChecker', 'Maximize']
              }, '/', {
                name: 'styles',
                items: ['Format', 'FontSize', 'TextColor', 'PasteText', 'PasteFromWord', 'RemoveFormat']
              }, {
                name: 'insert',
                items: ['Image', 'Table', 'SpecialChar']
              }, {
                name: 'forms',
                items: ['Outdent', 'Indent']
              }, {
                name: 'clipboard',
                items: ['Undo', 'Redo']
              }, {
                name: 'document',
                items: ['PageBreak', 'Source']
              }],
            disableNativeSpellChecker: false,
            uiColor: '#FAFAFA',
            height: '400px',
            width: '100%'
          };
          options = angular.extend(options, scope[attrs.ckeditor]);

          var instance = (isTextarea) ? CKEDITOR.replace(element[0], options) : CKEDITOR.inline(element[0], options),
            configLoaderDef = $q.defer();

          element.bind('$destroy', function() {
            // instance is the reference to the CHKEditor instance that is to be deleted.
            if (instance) {
              instance.destroy();
            }
          });
          var setModelData = function(setPristine) {
            var data = instance.getData();
            if (data === '') {
              data = null;
            }
            $timeout(function() { // for key up event
              if (setPristine !== true || data !== ngModel.$viewValue) {
                ngModel.$setViewValue(data);
              }

              if (setPristine === true && form) {
                form.$setPristine();
              }
            }, 0);
          }, onUpdateModelData = function(setPristine) {
            if (!data.length) {
              return;
            }

            var item = data.pop() || EMPTY_HTML;
            isReady = false;
            instance.setData(item, function() {
              setModelData(setPristine);
              isReady = true;
            });
          };

          instance.on('pasteState', setModelData);
          instance.on('change', setModelData);
          instance.on('blur', setModelData);
          //instance.on('key',          setModelData); // for source view

          instance.on('instanceReady', function() {
            scope.$broadcast('ckeditor.ready');
            scope.$apply(function() {
              onUpdateModelData(true);
            });

            // This fixes a bug in ng-ckeditor that caused #627.
            // Previously listener was incorrectly configured. Function setModelData was called on a keystroke
            // anywhere on the page.
            instance.on('key', setModelData);
          });
          instance.on('customConfigLoaded', function() {
            configLoaderDef.resolve();
          });

          // XXX Hack work around ngImage plugin using angular.element.scope which is not available outside debug mode.
          instance.on('insertNgImage', function(event) {
            if (scope.attrCtrl) {
              scope.attrCtrl.insertImage(event.data.callback);
            } else {
              $log.debug("insertNgImage event but not in a scope with attrCtrl available.")
            }
          });

          // XXX Hack to disallow enter for single line text boxes
          if (attrs.ckeditor == 'richTextSingleLine') {
            instance.on('key', function(event) {
                if (event.data.keyCode == 13) {
                    event.cancel();
                }
            });
          }

          ngModel.$render = function() {
            data.push(ngModel.$viewValue);
            if (isReady) {
              onUpdateModelData();
            }
          };
        };

        if (CKEDITOR.status === 'loaded') {
          loaded = true;
        }
        if (loaded) {
          onLoad();
        } else {
          $defer.promise.then(onLoad);
        }
      }
    };
  }]);

  return app;
}));