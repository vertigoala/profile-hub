/**
 * Utility functions
 */
profileEditor.factory('util', function ($location, $q, config, $modal, $window) {

    var KEYWORDS = ["create", "update", "delete"];
    var UUID_REGEX_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
    var LAST = "last";
    var FIRST = "first";
    var RANK = {
        KINGDOM: "kingdom",
        PHYLUM: "phylum",
        CLASS: "class",
        SUBCLASS: "subclass",
        ORDER: "order",
        FAMILY: "family",
        GENUS: "genus",
        SPECIES: "species",
        SUBSPECIES: "subspecies",
        CULTIVAR: "cultivar",
        VARIETY: "variety"
    };

    /**
     * Retrieves a specific item from the path of the current URL. Items are defined as anything between two slashes.
     *
     * NOTE: The path INCLUDES the context root
     *
     * e.g. for the URL http://hostname.com/path/item1/item2/, getPathItem('first') will return 'path',
     * getPathItem(1) will return 'item1', getPathItem('last') will return 'item2'.
     *
     * @param index can be a numeric index or 'first' as an alias for 0 or 'last' as an alias for the last item in the url
     * @returns the specified item in the url
     */
    function getPathItem(index) {
        return getPathItemFromUrl(index, getPath());
    }

    /**
     * Retrieves a specific item from the path of the provided URL. Items are defined as anything between two slashes.
     *
     * NOTE: The path INCLUDES the context root
     *
     * e.g. for the URL http://hostname.com/path/item1/item2/, getPathItem('first') will return 'path',
     * getPathItem(1) will return 'item1', getPathItem('last') will return 'item2'.
     *
     * @param index can be a numeric index or 'first' as an alias for 0 or 'last' as an alias for the last item in the url
     * @param url The URL to parse
     * @returns the specified item in the url
     */
    function getPathItemFromUrl(index, url) {
        var path = url;

        // remove a leading slash so we don't get an empty item at the start of the array
        if (path.indexOf("/") == 0) {
            path = path.substring(1);
        }

        var items = path.split("/");
        var item;

        switch (index) {
            case "first":
                item = items[0];
                break;
            case "last":
                item = items[items.length - 1];
                item = stripQueryString(item);
                break;
            default:
                item = items[index];
                if (index == items.length - 1) {
                    item = stripQueryString(item);
                }
        }

        return item;
    }

    function stripQueryString(item) {
        if (item && item.indexOf("?") > 0) {
            item = item.substring(0, item.indexOf("?"));
        }

        if (item && item.indexOf("#") > 0) {
            item = item.substring(0, item.indexOf("#"));
        }

        return item;
    }

    /**
     * Retrieve the 'path' portion of the current URL. This is everything from the context root (inclusive) up to but not
     * including any request parameters.
     *
     * e.g. for a URL of http://hostname.com/foo/bar/bla?a=b, this method will return '/foo/bar/bla'
     * @returns {string} The path portion of the current URL
     */
    function getPath() {
        var path = $location.path();

        if (!path) {
            var url = $location.absUrl();
            var offset = $location.protocol().length + 3; // ignore the length of the protocol plus ://
            var startIndex = url.indexOf("/", offset);
            var endIndex = url.indexOf("?") == -1 ? url.length : url.indexOf("?");

            path = url.substring(startIndex, endIndex)
        }

        return path;
    }

    /**
     * Retrieves the value of the specified parameter from the query string of the url
     *
     * @param param The query parameter to return
     * @returns {*} The value of the specified param, or null if it does not exist
     */
    function getQueryParameter(param) {
        var val = $location.search()[param];
        if (!val) {
            val = null;
        }

        return val
    }

    /**
     * Get the specified entity id from the URL.
     *
     * Assumes all urls where the opus and/or profile id are required have the form http://.../opusShortName/profileScientificName/...
     */
    function getEntityId(entity) {
        var entityId = null;

        var path = getPath();

        if (path.indexOf(config.contextPath) == 0) {
            path = path.substring(config.contextPath.length);
        }

        if (path.indexOf("/") == 0) {
            path = path.substring(1)
        }

        if (path.charAt(path.length - 1) == "/") {
            path = path.substring(0, path.length - 1)
        }

        path = path.split("/");

        if (path) {
            if (entity == "opus") {
                entityId = path[1];
            } else if (entity == "profile" && path.length > 1) {
                entityId = path[3];
            }
        }

        if (entityId && entityId.indexOf(";") > -1) {
            entityId = entityId.substring(0, entityId.indexOf(";"))
        }

        if (KEYWORDS.indexOf(entityId) > -1) {
            entityId = null;
        }
        return entityId;
    }

    /**
     * Take the current URL and work out the context path.
     * $location.path only works when HTML 5 mode is enabled. This approach works in both HTML 5 and HashBang modes.
     *
     * e.g. for a URL of http://hostname.com/contextPath/bla/bla, this method will return '/contextPath'
     * e.g. for a URL of http://hostname.com/, this method will return '/'
     *
     * @returns String the context path of the current URL, with a leading slash but no trailing slash
     */
    function contextRoot() {
        return config.contextPath;
    }

    /**
     * Retrieve the current user's name
     * @returns {*}
     */
    function currentUser() {
        return config.currentUser;
    }

    /**
     * The $http service returns an extended promise object which has success and error functions.
     * This introduces inconsistency with other code that deals with promises, and complicates the unit tests.
     * Therefore, we will create a new standard promise (which just uses then()) and return it instead.
     * http://weblog.west-wind.com/posts/2014/Oct/24/AngularJs-and-Promises-with-the-http-Service has a good explanation.
     *
     * @param httpPromise $http extended promise
     * @return standard promise
     */
    function toStandardPromise(httpPromise) {
        var defer = $q.defer();

        httpPromise.success(function (data) {
            defer.resolve(data);
        });
        httpPromise.error(function (data, status, context, request) {
            var msg = "Failed to invoke URL " + request.url + ": Response code " + status;
            console.log(msg);
            defer.reject(msg);
            if (status == 403) {
                console.log("not authorised");
                redirect(contextRoot() + "/notAuthorised");
            }
        });

        return defer.promise;
    }

    /**
     * Checks if the provided identifier matches the regex pattern for a UUID.
     * @see UUID_REGEX_PATTERN
     *
     * @param id the id to check
     * @returns {Array|{index: number, input: string}|*}
     */
    function isUuid(id) {
        return id && id.match(UUID_REGEX_PATTERN)
    }

    /**
     * Displays a confirmation popup
     *
     * @param message The message text to display
     * @returns {Promise}
     */
    function confirm(message, ok, cancel) {
        if (ok === undefined) {
            ok = "OK"
        }
        if (cancel === undefined) {
            cancel = "Cancel"
        }

        var html = '<div class="modal-header confirm">' +
            '<h4 class="modal-title">Confirmation</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '{{ confirmCtrl.message | default:"Are you sure you wish to continue with this operation?"}}' +
            '</div>' +

            '<div class="modal-footer">' +
            '<button class="btn btn-primary" ng-click="confirmCtrl.ok()">' + ok + '</button>' +
            '<button class="btn btn-default" ng-click="confirmCtrl.cancel()">' + cancel + '</button>' +
            '</div>';

        var popup = $modal.open({
            template: html,
            controller: "ConfirmationController",
            controllerAs: "confirmCtrl",
            size: "sm",
            resolve: {
                message: function() {
                    return message
                }
            }
        });

        return popup.result;
    }

    function redirect(url) {
        $window.location = url;
    }

    /**
     * Converts the human-readable label to a value suitable for use as a key by removing all spaces and punctuation, and converting to lowercase.
     *
     * @param label the label to convert
     */
    function toKey(label) {
        if (label) {
            return label.replace(/\W/g, '_').toLowerCase();
        }
    }

    /**
     * Formats a profile name: italicises the scientific name, leaves connecting terms and author names as normal text.
     *
     * @param scientificName The name without any author information. Only used if 'fullName' is not provided.
     * @param nameAuthor The author information without the scientific name.
     * @param fullName The combined scientific name and author information.
     * @returns {*} The formatted name
     */
    function formatScientificName(scientificName, nameAuthor, fullName) {
        if (!scientificName && !nameAuthor && !fullName) {
            return null;
        }
        var connectingTerms = ["subsp.", "var.", "f.", "ser.", "subg.", "sect.", "subsect."];

        if (nameAuthor) {
            connectingTerms.push(nameAuthor);
        }

        var name = null;
        if (fullName) {
            name = fullName;
        } else if (scientificName && nameAuthor) {
            name = scientificName + " " + nameAuthor;
        } else {
            name = scientificName;
        }

        angular.forEach(connectingTerms, function(connectingTerm) {
            var index = name.indexOf(connectingTerm);
            if (index > -1) {
                var part1 = name.substring(0, index);
                var part2 = "<span class='normal-text'>" + connectingTerm + "</span>";
                var part3 = name.substring(index + connectingTerm.length, name.length);
                name = part1 + part2 + part3;
            }
        });

        return "<span class='scientific-name'>" + name + "</span>";
    }

    /**
     * Public API
     */
    return {
        contextRoot: contextRoot,
        getPathItem: getPathItem,
        getPathItemFromUrl: getPathItemFromUrl,
        toStandardPromise: toStandardPromise,
        isUuid: isUuid,
        confirm: confirm,
        redirect: redirect,
        getEntityId: getEntityId,
        getQueryParameter: getQueryParameter,
        currentUser: currentUser,
        toKey: toKey,
        formatScientificName: formatScientificName,

        LAST: LAST,
        FIRST: FIRST,
        RANK: RANK,
        UUID_REGEX_PATTERN: UUID_REGEX_PATTERN
    };

});


/**
 * Confirmation modal dialog controller
 */
profileEditor.controller('ConfirmationController', function ($modalInstance, message) {
    var self = this;

    self.message = message;

    self.ok = function() {
        $modalInstance.close({continue: true});
    };

    self.cancel = function() {
        $modalInstance.dismiss("cancel");
    }
});