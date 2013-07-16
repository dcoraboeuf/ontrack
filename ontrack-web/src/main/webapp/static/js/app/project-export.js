define(['jquery', 'ajax'], function ($, ajax) {

    var project = $('#project').val();

    function check() {
        ajax.get({
            url: 'ui/io/project/{0}'.format(project),
            successFn: function () {
                $('#project-export-loading').hide();
                console.log("Success");
            },
            errorFn: function (jqXHR, textStatus, errorThrown) {
                if (jqXHR.status == 204) {
                    // No content
                    // Going on...
                    window.setTimeout(check, 5000);
                } else {
                    $('#project-export-loading').hide();
                    ajax.elementErrorMessageFn($('#project-export-error'))(
                        ajax.getAjaxError(jqXHR, textStatus, errorThrown)
                    );
                }
            }
        });
    }

    window.setTimeout(check, 5000)

})