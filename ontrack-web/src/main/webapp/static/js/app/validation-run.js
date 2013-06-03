define(['jquery'], function ($) {

    /**
     * Updates the status
     */
    function updateStatus() {
        $('#statusUpdate-error').hide();
        $('#statusUpdate-form').show();
        if ($('#statusUpdate-form').is(':visible')) {
            $('#description').focus();
        }
    }

    // Actions

    $('#statusUpdate-button').click(updateStatus);

});