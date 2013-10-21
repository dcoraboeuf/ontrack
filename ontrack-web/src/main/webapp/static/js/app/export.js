define(['jquery', 'ajax', 'app/component/export'], function ($, ajax, exp) {

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
            errorFn: ajax.simpleAjaxErrorFn(ajax.elementErrorMessageFn($('#export-error'))),
            successFn: function (response) {
                $('#export-projects').hide();
                exp.check({
                    container: $('#export-progress'),
                    uid: response.uid
                })
            }
        })
    })

});