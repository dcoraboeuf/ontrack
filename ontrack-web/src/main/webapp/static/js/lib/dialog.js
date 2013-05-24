define(['config', 'render', 'jquery'], function(config, render, $) {

    function createButton(dialog, button) {
        var item = $('<button/>');
        item.addClass('btn');
        item.text(button.text);
        if (button.action == 'submit') {
            item.addClass('btn-primary').attr('type', 'submit');
        } else if (button.action == 'cancel') {
            item.addClass('btn-link').attr('type', 'button');
            item.unbind('click');
            item.click(dialog.closeFn);
        } else {
            item.attr('type', 'button');
        }
        return item;
    }

    function show (dialog) {
        // Default configuration
        dialog = $.extend({
            width: 550,
            buttons:[{
                text: 'general.submit'.loc(),
                action: 'submit'
            }, {
                text: 'general.cancel'.loc(),
                action: 'cancel'
            }]
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
            // ID
            var dialogId = 'dialog-{0}'.format(dialog.templateId);
            dialog.id = dialogId;
            $(html).attr('id', dialogId);
            // Close function
            dialog.closeFn = function () {
                $('#' + dialog.id).dialog('close');
                $('#' + dialog.id).dialog('destroy');
                $('#' + dialog.id).remove();
            };
            // Form
            var form;
            if ($(html)[0].tagName.toLowerCase() == 'form') {
                form = html;
            } else {
                form = $(html).find('form');
            }
            // Customization
            if (form) {
                // Error section
                $('<div></div>')
                    .addClass('error').addClass('hidden').addClass('alert').addClass('alert-error')
                    .appendTo(form);
                // Button section
                if (dialog.buttons && dialog.buttons.length > 0) {
                    var controls = $('<div></div>').addClass('controls');
                    $.each(dialog.buttons, function (index, button) {
                        var item = createButton(dialog, button);
                        controls.append(item);
                    });
                    $('<div></div>').addClass('control-group').append(controls).appendTo(form);
                }
            }
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