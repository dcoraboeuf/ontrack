define(['jquery', 'ajax'], function ($, ajax) {

    var project = $('#project').val();
    var projectExportUID = $('#projectExportUID').val();

    function check() {
        ajax.get({
            url: 'ui/manage/export/{0}/check'.format(projectExportUID),
            successFn: function (ack) {
                if (ack.success) {
                    $('#project-export-loading').hide();
                    alert("Success");
                } else {
                    // Going on...
                    window.setTimeout(check, 5000);
                }
            },
            errorFn: ajax.simpleAjaxErrorFn(function (message) {
                $('#project-export-loading').hide();
                ajax.elementErrorMessageFn($('#project-export-error'))(message);
            })
        })
    }

    window.setTimeout(check, 5000)

})