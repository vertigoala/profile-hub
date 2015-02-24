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
            result = input[0].toUpperCase() + input.slice(1);
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