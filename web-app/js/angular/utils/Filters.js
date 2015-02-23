profileEditor.filter("capitalize", function () {
    return function (input) {
        var result = input;
        if (input) {
            result = input[0].toUpperCase() + input.slice(1);
        }
        return result
    }
});

profileEditor.filter("default", function() {
    return function (input, defaultValue) {
        var result = input;
        if (input) {
            result = defaultValue
        }
        return result
    }
});