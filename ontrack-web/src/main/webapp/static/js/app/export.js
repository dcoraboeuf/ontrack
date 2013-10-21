define(['jquery', 'ajax'], function ($, ajax) {

    $('#export-next').click(function () {
        var projects = $('.export-project-chk').find('input').filter(':checked').map(function (i, chk) {
            return $(chk).attr('data-project')
        }).toArray();
        ajax.post({
            url: 'ui/manage/export/project',
            data: {
                names: projects
            },
            loading: {
                el: $('#export-next')
            },
            errorFn: ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn($('#export-error')))
        })
    })

});