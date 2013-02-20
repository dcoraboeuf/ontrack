var ValidationRun = function () {

    function onSendStatus () {
        // Run ID stored as a hidden field
        var runId = $('#validationRunId').val();
        // Sends the form
        Application.submit({
            id: 'status-form',
            url: 'ui/manage/validation_run/{0}/comment'.format(runId),
            successFn: function (data) {
                // Closes the dialog
                $('#status-form').hide();
                // Reloads the activities
                Template.reload('audit');
            },
            errorMessageFn: function (message) {
                Application.error('status-error', message);
            }
        });
        // Does not submit
        return false;
    }

    function init () {
        $('#status-form').submit(onSendStatus);
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
        init: init,
        updateStatus: updateStatus,
        cancelUpdateStatus: cancelUpdateStatus

    };

} ();

$(document).ready(ValidationRun.init);