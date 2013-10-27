define(['jquery', 'ajax', 'render'], function ($, ajax, render) {

    var uid = $('#uid').val();

    function check() {
        ajax.get({
            url: 'ui/manage/import/{0}/check'.format(uid),
            successFn: function (result) {
                if (result.finished.success) {
                    $('#import-progress').hide();
                    // Import done
                    render.renderInto(
                        $('#import-result'),
                        'import-result',
                        result
                    )
                } else {
                    // Going on...
                    window.setTimeout(check, 2000);
                }
            },
            errorFn: ajax.simpleAjaxErrorFn(function (message) {
                $('#import-progress').hide();
                ajax.elementErrorMessageFn($('#import-error'))(message);
            })
        })
    }

    window.setTimeout(check, 2000);

});