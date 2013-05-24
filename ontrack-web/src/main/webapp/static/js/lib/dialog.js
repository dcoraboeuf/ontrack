define(['config', 'render', 'jquery'], function(config, render, $) {

    function show (dialog) {
        // Default configuration
        dialog = $.extend({
            width: 550
        }, dialog);
        // Checks parameters
        config.check(dialog, 'title');
        config.check(dialog, 'templateId');
        // Loads the template
        render.withTemplate(dialog.templateId, function (compiledTemplate) {
            // TODO Model for the template
            var data = {};
            // Gets the HTML to display
            var html = $(compiledTemplate(data));
            // TODO Error section
            // TODO Button section
            // TODO Initialization of the content
            // Displays the dialog
            $(html).dialog({
                title: dialog.title,
                width: dialog.width,
                modal: true
            });
        });
    }

    return {
        show: show
    }

});