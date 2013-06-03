define(['render','jquery','ajax'], function (render, $, ajax) {

    Handlebars.registerHelper('statusLabel', function(options) {
        return ('status.' + options.fn(this)).loc();
    });

    function updateStatus() {
        $('#validation-run-status-error').hide();
        $('#validation-run-status-form').show();
        if ($('#validation-run-status-form').is(':visible')) {
            $('#validation-run-status-description').focus();
        }
    }

    function updateStatusCancel() {
        $('#validation-run-status-form').hide();
    }

    function sendStatus(config, status) {
        // Description element
        var descriptionLine = $(config.section).find('#validation-run-status-description');
        var descriptionEl = descriptionLine.find('textarea');
        // Checks the description (required for Comment only)
        var description = descriptionEl.val();
        if (description.trim() == '' && status == '') {
            $(descriptionEl).focus();
            $(descriptionLine).addClass('error');
            return false;
        }
        // Run ID stored in the configuration
        var runId = config.run;
        // Collects the properties and their values
        var properties = [];
        $(config.section).find('.update-status-property').each (function (index, cell) {
            var extension = $(cell).attr('extension');
            var name = $(cell).attr('property');
            var inputId = 'extension-{0}-{1}'.format(extension, name);
            var value = $(config.section).find('#' + inputId).val();
            if (value != '') {
                properties.push({
                    extension: extension,
                    name: name,
                    value: value
                });
            }
        });
        // Sends the form
        ajax.post({
            url: 'ui/manage/validation_run/{0}/comment'.format(runId),
            data: {
                status: status,
                description: description,
                properties: properties
            },
            successFn: function (data) {
                // Closes the dialog
                $(config.section).find('form').hide();
                // Reloads the page
                location.reload();
            },
            errorFn: ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn($(config.section).find('#validation-run-status-error')))
        });
        // Does not submit
        return false;
    }

    function init (config) {
        $('#validation-run-status-button').click(updateStatus);
        $('#validation-run-status-cancel').click(updateStatusCancel);
        $('.validation-run-status-change').each(function (index, button) {
            $(button).click(function () {
                sendStatus(config, $(button).attr('status'));
            });
        });
    }

    return {
        url: function (config) {
            return 'ui/manage/validation_run/{0}/statusUpdateData'.format(config.run)
        },
        render: render.asSimpleTemplate(
            'validation-run-status',
            render.sameDataFn,
            init
        )
    }

})