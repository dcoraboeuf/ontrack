define(['config', 'render', 'jquery'], function(config, render, $) {

    function createButton(dialog, button) {
        var item = $('<button/>');
        item.addClass('btn');
        item.text(button.text);
        if (button.action == 'submit') {
            item.addClass('btn-primary').attr('type', 'submit');
            dialog.form.unbind('submit');
            dialog.form.submit(function () {
                if (dialog.submitFn) {
                    dialog.submitFn(dialog);
                }
                return false;
            });
        } else if (button.action == 'cancel') {
            item.addClass('btn-link').attr('type', 'button');
            item.unbind('click');
            item.click(dialog.closeFn);
        } else {
            item.attr('type', 'button');
        }
        // Any ID?
        if (button.id) {
            item.attr('id', button.id);
        }
        // OK
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
            // Model for the template
            var data = {};
            if (dialog.data) {
                data = dialog.data;
            }
            // Gets the HTML to display
            var html = $(compiledTemplate(data));
            // ID
            var dialogId = 'dialog-{0}'.format(dialog.templateId.replace('/', '_'));
            dialog.id = dialogId;
            $(html).attr('id', dialogId);
            // Close function
            dialog.closeFn = function () {
                $('#' + dialog.id).dialog('close');
                $('#' + dialog.id).dialog('destroy');
                $('#' + dialog.id).remove();
            };
            // TODO Default error function
            dialog.errorFn = $.noop;
            // Form
            var form;
            if ($(html)[0].tagName.toLowerCase() == 'form') {
                form = html;
            } else {
                form = $(html).find('form');
            }
            // Customization
            if (form) {
                dialog.form = form;
                // Error section
                var errorEl = $('<div></div>')
                    .addClass('error').addClass('hidden').addClass('alert').addClass('alert-error')
                    .appendTo(form);
                // Error function
                dialog.errorFn = function (message) {
                    if (message == null) {
                        errorEl.hide();
                    } else {
                        errorEl.text(message);
                        errorEl.show();
                    }
                };
                // Button section
                if (dialog.buttons && dialog.buttons.length > 0) {
                    dialog.controls = {};
                    var controls = $('<div></div>').addClass('controls');
                    $.each(dialog.buttons, function (index, button) {
                        var item = createButton(dialog, button);
                        if (button.action) {
                            dialog.controls[button.action] = item;
                        }
                        controls.append(item).append("&nbsp;");
                    });
                    $('<div></div>').addClass('control-group').append(controls).appendTo(form);
                }
            }
            // Initialization of the content
            if (dialog.initFn) {
                dialog.initFn(dialog);
            }
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