/**
 * Custom CKEditor control to allow images to be uploaded to the profile and/or selected to display inside the attribute
 */
CKEDITOR.plugins.add('ngImage', {
    init: function (editor) {
        editor.addCommand('insertImage', {
            exec: function (editor) {

                var callback = function(imageElement, imageCaption) {
console.log(imageElement)

                    //CREATE THE ANGULAR MODAL, CONSTRUCT AND COMPILE THE IMG TAG THERE THEN RETURN...SEE IF THAT FIXES THE UNTRUSTED RESOURCE PROBLEM

console.log("here7") // TODO remove me!!
                    //var element = CKEDITOR.dom.element.createFromHtml(imageElement);
                    var element = new CKEDITOR.dom.element(imageElement);
                    console.log(element) // TODO remove me!!
                    editor.insertElement(element);
                    //editor.insertHtml(imageElement);
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