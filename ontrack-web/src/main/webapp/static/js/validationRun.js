var ValidationRun = function () {

    function updateStatus () {
        $('#status-form').toggle();
    }

    function cancelUpdateStatus () {
        $('#status-form').toggle();
    }

    return {

        updateStatus: updateStatus,
        cancelUpdateStatus: cancelUpdateStatus

    };

} ();