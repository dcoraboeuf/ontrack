define(['jquery', 'ajax'], function ($, ajax) {

    function check(uid) {
        ajax.get({
            url: 'ui/manage/export/{0}/check'.format(uid),
            successFn: function (ack) {
                if (ack.success) {
                    $('#project-export-loading').hide();
                    // Download ready
                    $('#project-export-ready').show();
                    $('#project-export-link').attr(
                        'href',
                        'gui/project/export/{0}'.format(uid)
                    )
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
                window.setTimeout(
                    function () {
                        check(response.uid)
                    },
                    5000
                )
            }
        })
    })

});