/**
 * Utility functions
 */
profileEditor.factory('util', function ($location) {
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
        var path = getPath();

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
            var endIndex = path.indexOf("?") == -1 ? path.length() : path.indexOf("?");

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
     * @returns the context path of the current URL, with a leading slash but no trailing slash
     */
    function contextRoot() {
        return "/" + getPathItem(FIRST);
    }

    /**
     * Public API
     */
    return {
        contextRoot: contextRoot,
        getPathItem: getPathItem,

        LAST: LAST,
        FIRST: FIRST
    };

});
