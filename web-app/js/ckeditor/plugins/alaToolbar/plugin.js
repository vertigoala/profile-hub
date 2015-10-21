/**
 * Custom CKEditor toolbar to show commonly used symbols like male/female, plus/minus etc
 */
CKEDITOR.plugins.add('alaToolbar', {
    init: function (editor) {
        editor.addCommand('insertMaleSymbol', {
            exec: function (editor) {
                editor.insertHtml('\u2642');
            }
        });

        editor.ui.addButton( 'Male', {
            label: 'Insert male symbol',
            command: 'insertMaleSymbol',
            toolbar: 'male'
        });

        editor.addCommand('insertFemaleSymbol', {
            exec: function (editor) {
                editor.insertHtml('\u2640');
            }
        });

        editor.ui.addButton( 'Female', {
            label: 'Insert female symbol',
            command: 'insertFemaleSymbol',
            toolbar: 'female'
        });

        editor.addCommand('insertPlusMinusSymbol', {
            exec: function (editor) {
                editor.insertHtml('\u00B1');
            }
        });

        editor.ui.addButton( 'PlusMinus', {
            label: 'Insert plus/minus symbol',
            command: 'insertPlusMinusSymbol',
            toolbar: 'plusminus'
        });

        editor.addCommand('insertEndashSymbol', {
            exec: function (editor) {
                editor.insertHtml('\u2013');
            }
        });

        editor.ui.addButton( 'Endash', {
            label: 'Insert endash symbol',
            command: 'insertEndashSymbol',
            toolbar: 'endash'
        });
    }
});