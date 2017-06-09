(function() {

  function CollectionHeaderCtrl(config, $sce) {
    var self = this;

    self.opusLogoUrl = function() {
      if (angular.isUndefined(self.opus) || self.opus === null) {
        return '';
      } else {
        var logoUrl;
        if (self.opus.opusLayoutConfig) {
          if (self.opus.opusLayoutConfig.opusLogoUrl) {
            logoUrl = self.opus.opusLayoutConfig.opusLogoUrl;
          } else if (!angular.isUndefined(self.opus.opusLayoutConfig.images) && self.opus.opusLayoutConfig.images.length > 0) {
            logoUrl = config.defaultOpusLogo;
          } else {
            logoUrl = '';
          }
        } else {
          logoUrl = '';
        }
        return logoUrl;
      }
    };

    self.trustAsHtml = function (text) {
       return $sce.trustAsHtml(text);
    };
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

})();