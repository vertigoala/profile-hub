/**
 * Custom CKEditor control to allow images to be uploaded to the profile and/or selected to display inside the attribute
 */
CKEDITOR.plugins.add('ngImage', {
    init: function (editor) {
        editor.addCommand('insertImage', {
            exec: function (editor) {
                var callback = function(imageElementHtml) {
                    var element = CKEDITOR.dom.element.createFromHtml(imageElementHtml);
                    editor.insertElement(element);
                };
                // angular.element($('#attributeContent')).scope().attrCtrl.insertImage(callback);
                editor.fire('insertNgImage', { callback: callback })
            }
        });

        editor.ui.addButton( 'ngImage', {
            label: 'Insert image',
            command: 'insertImage',
            toolbar: 'ngImage'
        });
    }
});