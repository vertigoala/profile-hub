/**
 * Custom CKEditor control to allow images to be uploaded to the profile and/or selected to display inside the attribute
 */
CKEDITOR.plugins.add('ngImage', {
    init: function (editor) {
        editor.addCommand('insertImage', {
            exec: function (editor) {

                var callback = function(imageUrl, imageCaption) {
                    var html = "<img src='" + imageUrl + "' alt='" + imageCaption + "' class='thumbnail inline-attribute-image'>";
console.log(html)
                    var element = CKEDITOR.dom.element.createFromHtml(html);
                    editor.insertElement(element);
                };

                angular.element($('#attributeContent')).scope().attrCtrl.insertImage(callback);
            }
        });

        editor.ui.addButton( 'ngImage', {
            label: 'Insert image',
            command: 'insertImage',
            toolbar: 'ngImage'
        });
    }
});