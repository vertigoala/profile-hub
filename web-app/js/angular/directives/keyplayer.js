profileEditor.directive('keyPlayer', function ($browser) {
    return {
        restrict: 'A',
        require: ['?keyId', '?profileUrl', '?keybaseUrl'],
        scope: {
            keyId: '=',
            profileUrl: '@',
            keybaseUrl: '@'
        },
        templateUrl: $browser.baseHref() + 'static/templates/keyplayer.html',
        controller: ['$scope', '$http', '$window', function ($scope, $http, $window) {
            angular.element(document).find('head').prepend('<style type="text/css">@charset "UTF-8";.keyplayer-panel{overflow: auto; max-height: 300px; min-height: 300px}</style>');

            $scope.visitedKeys = [];

            $scope.loadKey = function (key) {
                $http.jsonp($scope.keybaseUrl + key)
                    .success(function (data) {
                        $scope.visitedKeys.push({id: key, name: data.key_name});

                        $scope.json = data;
                        $scope.rootNodeId = data.first_step.root_node_id;
                        var nextId = $scope.rootNodeId;

                        nestedSets($scope.rootNodeId);
                        nextCouplet(nextId, $scope.rootNodeId);

                        $scope.projectName = data.project.project_name;
                    }
                ).error(function () {
                        console.log("something died")
                    }
                );
            };

            $scope.viewItem = function (name) {
                $scope.error = null;

                $http.get($scope.profileUrl + "/" + name).success(function () {
                    $window.location = $scope.profileUrl + "/" + name;
                }).error(function (responseData, statusCode) {
                    if (statusCode == 404) {
                        $scope.error = " A profile page does not exist for " + name;
                    }
                });

            };

            $scope.loadPreviousKey = function () {
                $scope.visitedKeys.splice($scope.visitedKeys.length - 1, 1);

                $scope.loadKey($scope.visitedKeys.splice($scope.visitedKeys.length - 1, 1)[0].id);
            };

            $scope.loadItem = function (selectedKey) {
                nextCouplet(selectedKey, $scope.rootNodeId);
            };

            $scope.loadParent = function (selectedKey) {
                var newKey = getParent(selectedKey);
                nextCouplet(newKey, $scope.rootNodeId);
            };

            function nestedSets(rootNodeId) {
                $scope.nested_sets = [];

                getNode(rootNodeId, 1);

                $scope.nested_sets.sort(function (a, b) {
                    return a.left - b.left;
                });

                $scope.json.first_step.left = 1;
                $scope.json.first_step.right = Math.max.apply(Math, JSPath.apply('.right', $scope.nested_sets));
            }

            function getNode(parentID, i) {
                var items = JSPath.apply('.leads{.parent_id==' + parentID + '}', $scope.json);
                $.each(items, function (index, item) {
                    i++;
                    item.left = i;
                    if (!item.item) {
                        i = getNode(item.lead_id, i);
                    }
                    item.right = i;
                    $scope.nested_sets.push(item);
                });
                return i;
            }

            function nextCouplet(nextId, rootNodeId) {
                var left = 1;
                var right = 1;
                if (nextId == rootNodeId) {
                    left = $scope.json.first_step.left;
                    right = $scope.json.first_step.right;
                }
                else {
                    var curnode = JSPath.apply('.leads{.lead_id === "' + nextId + '"}', $scope.json);
                    if (curnode && curnode[0]) {
                        left = curnode[0].left;
                        right = curnode[0].right;
                    } else {
                        return;
                    }
                }

                // Current node
                var current_node = currentNode(nextId);

                if (current_node.length > 0) {
                    $scope.currentHtml = [];
                    $.each(current_node, function (index, item) {
                        $scope.currentHtml.push({id: item.lead_id, text: item.lead_text});
                    });
                }
                else {
                    $scope.currentHtml = [{id: 0, text: "Result: " + getResult(nextId)[0].item_name}];
                }
                $scope.previousId = nextId;
                $scope.firstId = rootNodeId;

                // Path
                $scope.path = [];

                $.each(getPath(left, right), function (index, item) {
                    $scope.path.push({id: item.lead_id, text: item.lead_text});
                });

                // remaining and discarded nodes
                $scope.remainingItems = [];
                $scope.discardedItems = [];

                var aux_remaining = JSPath.apply('.leads{.item && .left >= ' + left + ' && .right <= ' + right + '}.item', $scope.json);

                $.each($scope.json.items, function (index, item) {
                    if (aux_remaining.indexOf(item.item_id) > -1) {
                        $scope.remainingItems.push(item);
                    }
                    else {
                        $scope.discardedItems.push(item);
                    }
                });

            }

            function getParent(leadId) {
                return JSPath.apply('.leads{.lead_id === "' + leadId + '"}.parent_id[0]', $scope.json);
            }

            function currentNode(parentId) {
                return JSPath.apply('.leads{.parent_id === "' + parentId + '"}', $scope.json);
            }

            function getResult(nextId) {
                var itemId = JSPath.apply('.leads{.lead_id == "' + nextId + '"}.item', $scope.json)[0];
                return JSPath.apply('.items{.item_id == "' + itemId + '"}', $scope.json);
            }

            function getPath(left, right) {
                return JSPath.apply('.leads{.left <= ' + left + ' && .right >= ' + right + '}', $scope.json);
            }

        }],
        link: function (scope, element, attrs, ctrl) {
            scope.$watch("keyId", function (keyId) {
                if (keyId) {
                    scope.loadKey(keyId);
                }
            });
        }
    }
});