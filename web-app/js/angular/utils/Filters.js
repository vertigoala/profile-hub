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
        return $sce.trustAsHtml(htmlCode);
    }
});