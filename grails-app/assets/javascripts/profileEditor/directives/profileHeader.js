(function () {

	function ProfileHeaderCtrl(config, $timeout) {
		var self = this;

		self.logoUrl = function() {
			if (angular.isUndefined(self.opus) || self.opus === null) {
				return '';
			} else {
				var ret;
				if (self.opus.opusLayoutConfig) {
					if (self.opus.opusLayoutConfig.opusLogoUrl) {
						ret = self.opus.opusLayoutConfig.opusLogoUrl;
					} else if (!angular.isUndefined(self.opus.opusLayoutConfig.images) && self.opus.opusLayoutConfig.images.length > 0) {
						ret = config.defaultOpusLogo;
					} else {
						ret = '';
					}
				} else {
					ret = '';
				}
				return ret;
			}
		};

		self.bannerUrl = function () {
			if (angular.isUndefined(self.opus) || self.opus === null) {
				return '';
			} else {
				var ret;
				var hasOpusBanner = self.hasOpusBanner();
				var hasProfileBanner = self.hasProfileBanner();
				var isProfile = angular.isDefined(self.isProfile) && self.isProfile;

				if (isProfile) {
					if (hasProfileBanner) {
						ret = self.opus.brandingConfig.profileBannerUrl;
					} else if (hasOpusBanner) {
						ret = self.opus.brandingConfig.opusBannerUrl;
					} else {
						ret = config.defaultProfileBannerUrl;
					}
				} else {
					if (hasOpusBanner) {
						ret = self.opus.brandingConfig.opusBannerUrl;
					} else if (hasProfileBanner) {
						ret = self.opus.brandingConfig.profileBannerUrl;
					} else {
						ret = config.defaultOpusBannerUrl;
					}
				}

				return ret;
			}
		};

		self.bannerHeight = function () {
			if (angular.isUndefined(self.opus) || self.opus === null) {
				return '0';
			}
			else {
				console.log("self.opus is not null");
				var isProfile = angular.isDefined(self.isProfile) && self.isProfile;
				if (isProfile) {
					return self.opus.brandingConfig.profileBannerHeight;
				} else {
					return self.opus.brandingConfig.opusBannerHeight;
				}
			}
		};

		self.hasBanner = function () {
			$timeout(function () {
				var exists = self.opus !== null;
				console.log("Checking whether a banner might exist: " + exists.toString());
				return exists;
			});
		};

		self.hasOpusBanner = function () {
			if (self.opus.brandingConfig) {
				return angular.isDefined(self.opus.brandingConfig.opusBannerUrl);
			} else {
				return false;
			}
		};

		self.hasProfileBanner = function () {
			if (self.opus.brandingConfig) {
				return angular.isDefined(self.opus.brandingConfig.profileBannerUrl);
			} else {
				return false;
			}
		};
	}

	profileEditor.directive('profileHeader', function () {
		return {
			controller: ProfileHeaderCtrl,
			controllerAs: '$ctrl',
			bindToController: true,
			restrict: 'AE',
			scope: {
				'opus': '=',
				'isProfile': '='
			},
			templateUrl: '/profileEditor/profileHeader.htm'
		};
	});

})();