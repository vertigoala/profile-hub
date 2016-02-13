/**
 * Angular service for recording navigation items across disparate controllers
 */
profileEditor.factory('navService', function ($rootScope, $filter) {

    $rootScope.nav = [];

    var orderBy = $filter("orderBy");

    function add(label, key, category) {
        var exists = false;
        angular.forEach($rootScope.nav, function(item) {
            if (item.key === key) {
                exists = true;
            }
        });

        var item = {label: label, key: key, category: category};
        if (!exists) {
            $rootScope.nav.push(item);
            $rootScope.nav = orderBy($rootScope.nav, 'label');
        }
    }

    function remove(key) {
        var index = -1;
        angular.forEach($rootScope.nav, function(item, idx) {
            if (item.key === key) {
                index = idx;
            }
        });

        if (index > -1) {
            $rootScope.nav.splice(index, 1);
        }
    }

    /**
     * Public API
     */
    return {
        add: add,
        remove: remove
    }
});