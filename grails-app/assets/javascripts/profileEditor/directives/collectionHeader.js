(function() {

  function CollectionHeaderCtrl() {
    var self = this;
  }

  profileEditor.directive('collectionHeader', function() {
    return  {
      controller: CollectionHeaderCtrl,
      controllerAs: '$ctrl',
      bindToController: true,
      restrict: 'AE',
      scope: {
        'opus': '='
      },
      templateUrl: '/profileEditor/collectionHeader.htm'
    };
  });

  function FaderCtrl(util) {
    var self = this;
    self.uuid = util.generateUuid();
    if (angular.isUndefined(self.type) || self.type === null || self.type === '') {
      self.type = 'webanimations';
    }
    if (angular.isUndefined(self.displayDuration) || !isFinite(self.displayDuration)) {
      self.displayDuration = 2000;
    }
    if (angular.isUndefined(self.fadeDuration) || !isFinite(self.fadeDuration)) {
      self.fadeDuration = 1000;
    }

    self.totalDuration = function() {
      if (angular.isArray(self.images)) {
        return (self.displayDuration + self.fadeDuration) * self.images.length;
      } else {
        return 0;
      }
    }
  }

  profileEditor.directive('fader', function($document) {
    return {
      controller: FaderCtrl,
      controllerAs: '$ctrl',
      bindToController: true,
      restrict: 'AE',
      scope: {
        'type': '@?',
        'images': '=',
        'displayDuration': '=?',
        'fadeDuration': '=?',
        'gradient': '=?',
        'gradientWidth': '=?'
      },
      templateUrl: '/profileEditor/fader.htm',
      link: function(scope, element, attrs, ctrl) {
        var images = ctrl.images || [];
        var anims = [];

        function generateKeyframes(images, displayDuration, fadeDuration) {
          var totalTime = (displayDuration + fadeDuration) * images.length;

          // these key frames make the element visible (opacity: 1) from time 0 to displayDuration
          // then fade the element out (opacity: 1 -> 0) from time displayDuration to displayDuration + fadeDuration
          // the element remains hidden (opacity: 0) from displayDuration + fadeDuration until totalTime - fadeDuration
          // then fade the element back in (opacity: 0 -> 1) from totalTime - fadeDuration to totalTime
          // offset is then expressed as a percentage
          // by applying these keyframes to all elements but staggering each animation such that each starts x / n
          // of the way through the animation (where x is it's index and n is the total elements)
          // then we achieve a cross fade effect.

          return [
            {opacity: 1.0, offset: 0.0, easing: 'linear'},
            {opacity: 1.0, offset: displayDuration / totalTime, easing: 'linear'},
            {opacity: 0, offset: 1 / images.length, easing: 'ease-in-out'},
            {opacity: 0, offset: 1 - fadeDuration / totalTime, easing: 'linear'},
            {opacity: 1.0, offset: 1.0, easing: 'ease-in-out'}
          ];
        }

        // generate a fader using web animations as it exists in the browser and polyfill today
        function generateWebAnimationsFader(images, displayDuration, fadeDuration) {
          var totalTime = (displayDuration + fadeDuration) * images.length;

          var keyframes = generateKeyframes(images, displayDuration, fadeDuration);
          var children = element.children();

          for (var i = 0; i < images.length; ++i) {
            var iterationStart = 1 - (i / images.length);
            var child = children[i];
            var anim = child.animate(keyframes, {
              iterations: Infinity,
              iterationStart: iterationStart,
              duration: totalTime,
              fill: 'forwards'
            });
            anims.push(anim);
          }
        }

        // generate a fader using the next draft of web animations and -next polyfill
        function generateWebAnimationsNextFader(images, displayDuration, fadeDuration) {
          var totalTime = (displayDuration + fadeDuration) * images.length;

          var keyframes = generateKeyframes(images, displayDuration, fadeDuration);
          var children = element.children();
          var anims = [];

          for (var i = 0; i < images.length; ++i) {
            var iterationStart = 1 - (i / images.length);
            var child = children[i];
            var effect = new KeyframeEffect(
              child,
              keyframes,
              {
                duration: totalTime,
                iterations: Infinity,
                iterationStart: iterationStart,
                fill: 'forwards'
              }
            );
            var anim = new Animation(effect, $document[0].timeline);
            anims.push(anim);
          }
          for (i = 0; i < anims.length; ++i) {
            anims[i].play();
          }

        }

        // generate a fader by dropping a style sheet onto the page and
        // having the browser calculate the animations for us.
        function generateCssFader(images, displayDuration, fadeDuration) {

          var uuid = ctrl.uuid;
          var keyframes = generateKeyframes(images, displayDuration, fadeDuration);
          var styleId = 'fader-style-' + uuid;

          var existing = $document[0].getElementById(styleId);
          if (existing !== null) {
            angular.element(existing).remove();
          }

          var style = '@keyframes fader-'+uuid+' {\n';
          for (var i = 0; i < keyframes.length; ++i) {
            var kf = keyframes[i];
            var offset;
            if (angular.isNumber(kf.offset)) {
              offset = kf.offset;
            } else {
              offset = i / (keyframes.length - 1);
            }
            style += (offset * 100.0) + '% {\n';
            style += 'opacity: ' + kf.opacity +';\n';
            style += '}\n';
          }
          style += '}';

          $document.find('head').append('<style id="' + styleId + '">\n' + style + '\n</style>');
        }

        function cancelAnimations() {
          while (anims.length) {
            var anim = anims.pop();
            if (angular.isDefined(anim) && anim !== null) {
              anim.cancel();
            }
          }
        }

        function generateFader() {
          cancelAnimations();
          if (!ctrl.images || ctrl.images.length < 2) {
            return;
          }
          if (ctrl.type === 'css') {
            generateCssFader(ctrl.images, ctrl.displayDuration, ctrl.fadeDuration);
          } else if (ctrl.type === 'webanimations-next') {
            generateWebAnimationsNextFader(ctrl.images, ctrl.displayDuration, ctrl.fadeDuration);
          } else {
            generateWebAnimationsFader(ctrl.images, ctrl.displayDuration, ctrl.fadeDuration);
          }
        }

        generateFader(); // TODO do I need this here?

        scope.$watchGroup( ['$ctrl.images', '$ctrl.displayDuration', '$ctrl.fadeDuration'], function(newVals, oldVals, scope) {
          generateFader();
        });
      }
    }
  });

})();