var ValidationRun = function () {

    function sendStatus (status) {
        // Run ID stored as a hidden field
        var runId = $('#validationRunId').val();
        // Sends the form
        Application.ajax (
            'POST',
            'ui/manage/validation_run/{0}/comment'.format(runId),
            {
                status: status,
                description: $('#description').val()
            },
            function (data) {
                // Closes the dialog
                $('#status-form').hide();
                // Reloads the page
                location.reload();
            },
            function (message) {
                Application.error('status-error', message);
            }
        );
        // Does not submit
        return false;
    }

    function updateStatus () {
        $('#status-error').hide();
        $('#status-form').toggle();
        if ($('#status-form').is(':visible')) {
            $('#description').focus();
        }
    }

    function cancelUpdateStatus () {
        $('#status-form').toggle();
    }

    return {
        sendStatus: sendStatus,
        updateStatus: updateStatus,
        cancelUpdateStatus: cancelUpdateStatus

    };

} ();