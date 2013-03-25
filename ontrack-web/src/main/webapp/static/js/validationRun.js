var ValidationRun = function () {

    function statusUpdateDataTemplate (validationRunId) {
        return Template.config({
            url: 'ui/manage/validation_run/{0}/statusUpdateData'.format(validationRunId),
            render: Template.asSimpleTemplate('statusUpdateDataTemplate')
        });
    }

    function historyTemplate (validationRunId) {
        return Template.config({
            url: 'ui/manage/validation_run/{0}/history?u=1'.format(validationRunId),
            more: true,
            render: Template.asTableTemplate('historyItemTemplate')
        });
    }

    function sendStatus (status) {
        // Checks the description
        var description = $('#description').val();
        if (description.trim() == '') {
            $('#description').focus();
            $('#description-line').addClass('error');
            return false;
        }
        // Run ID stored as a hidden field
        var runId = $('#validationRunId').val();
        // Collects the properties and their values
        var properties = [];
        $('.update-status-property').each (function (index, cell) {
            var extension = $(cell).attr('extension');
            var name = $(cell).attr('property');
            var inputId = 'extension-{0}-{1}'.format(extension, name);
            var value = $('#' + inputId).val();
            if (value != '') {
                properties.push({
                    extension: extension,
                    name: name,
                    value: value
                });
            }
        });
        // Sends the form
        Application.ajax (
            'POST',
            'ui/manage/validation_run/{0}/comment'.format(runId),
            {
                status: status,
                description: description,
                properties: properties
            },
            function (data) {
                // Closes the dialog
                $('#statusUpdate-form').hide();
                // Reloads the page
                location.reload();
            },
            function (message) {
                Application.error('statusUpdate-error', message);
            }
        );
        // Does not submit
        return false;
    }

    function updateStatus () {
        $('#statusUpdate-error').hide();
        $('#statusUpdate-form').show();
        if ($('#statusUpdate-form').is(':visible')) {
            $('#description').focus();
        }
    }

    function cancelUpdateStatus () {
        $('#statusUpdate-form').hide();
    }

    return {
        sendStatus: sendStatus,
        updateStatus: updateStatus,
        cancelUpdateStatus: cancelUpdateStatus,
        historyTemplate: historyTemplate,
        statusUpdateDataTemplate: statusUpdateDataTemplate
    };

} ();