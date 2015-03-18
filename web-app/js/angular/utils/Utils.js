/**
 * Utility functions
 */
profileEditor.factory('util', function ($location, $q, contextPath) {

    var UUID_REGEX_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i;
    var LAST = "last";
    var FIRST = "first";

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
            var startIndex = path.indexOf("/", offset);
            var endIndex = path.indexOf("?") == -1 ? path.length : path.indexOf("?");

            path = url.substring(startIndex, endIndex)
        }

        return path;
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
        return contextPath;
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
        return id.match(UUID_REGEX_PATTERN)
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

        LAST: LAST,
        FIRST: FIRST,
        UUID_REGEX_PATTERN: UUID_REGEX_PATTERN
    };

});
