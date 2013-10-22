define(['ajax'], function (ajax) {

    var uid = $('#uid').val();

    function check() {
        ajax.get({
            url: 'ui/manage/import/{0}/check'.format(uid),
            successFn: function (ack) {
                if (ack.success) {
                    $('#import-progress').hide();
                    // Import done
                    // FIXME Renders the results
                } else {
                    // Going on...
                    window.setTimeout(check, 5000);
                }
            },
            errorFn: ajax.simpleAjaxErrorFn(function (message) {
                $('#import-progress').hide();
                ajax.elementErrorMessageFn($('#import-error'))(message);
            })
        })
    }

    window.setTimeout(check, 5000);

});