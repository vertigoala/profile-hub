/**
 * Capitalizes the first word of the provided string
 *
 * Usage: {{ someTextAttribute | capitalize }}
 *
 * @param the text value to capitalise
 */
profileEditor.filter("capitalize", function () {
    return function (input) {
        var result = input;
        if (input) {
            var words = [];
            angular.forEach(input.split(" "), function(word) {
                words.push(word[0].toUpperCase() + word.slice(1).toLocaleLowerCase());
            });
            result = words.join(" ");
        }
        return result
    }
});

/**
 * Provides a default value for text element.
 *
 * Usage: {{ someTextAttribute | default:"hello world" }}
 *
 * @param input the original text value
 * @param defaultValue returned if 'input' is empty, whitespace or null
 */
profileEditor.filter("default", function() {
    return function (input, defaultValue) {
        var result = input;
        if (!input || input.trim().length == 0) {
            result = defaultValue
        }
        return result
    }
});

profileEditor.filter("groupAttributes", function() {
    return function(attributes, title) {
        return attributes.filter(function(attribute) {
            return attribute.title == title;
        });
    }
});

profileEditor.filter("sanitizeHtml", function($sce) {
    return function(htmlCode){
        if (angular.isArray(htmlCode)) {
            htmlCode = htmlCode.join(", ");
        }
        return $sce.trustAsHtml(htmlCode);
    }
});

/**
 * Turns HTML into plain text
 *
 * Usage: {{ someHtmlAttribute | plainText }}
 *
 */
profileEditor.filter('plainText', function() {
    return function(text) {
        return text ? String(text).replace(/<[^>]+>/gm, '') : '';
    }
});

profileEditor.filter("formatProfileName", function(util) {
    return function(name) {
        return util.formatScientificName(name.scientificName, name.nameAuthor, name.fullName);
    }
});

/**
 * replace underscore(s) with white space
 *
 * Usage: {{ someTextAttribute | formatText }}
 *
 * @param the text value to remove underscore
 */
profileEditor.filter("formatText", function () {
    return function (input) {
        var result = input;
        if (input) {
            result = input.toString().replace(/_+/g," ");
        }
        return result
    }
});

/**
 * URL encodes the provided text
 */
profileEditor.filter('enc', function() {
    return window.encodeURIComponent;
});