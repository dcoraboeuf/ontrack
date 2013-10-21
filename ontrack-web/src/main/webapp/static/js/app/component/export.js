define(['render', 'ajax'], function (render, ajax) {

    function doCheck(config) {

        var callback = function () {
            doCheck(config)
        }

        ajax.get({
            url: 'ui/manage/export/{0}/check'.format(config.uid),
            successFn: function (ack) {
                if (ack.success) {
                    $('#project-export-loading').hide();
                    // Download ready
                    $('#project-export-ready').show();
                    $('#project-export-link').attr(
                        'href',
                        'gui/project/export/{0}'.format(config.uid)
                    )
                } else {
                    // Going on...
                    window.setTimeout(callback, 5000);
                }
            },
            errorFn: ajax.simpleAjaxErrorFn(function (message) {
                $('#project-export-loading').hide();
                ajax.elementErrorMessageFn($('#project-export-error'))(message);
            })
        })
    }

    function check(config) {
        render.renderInto(
            config.container,
            'export-progress',
            {
                uid: config.uid
            },
            function () {
                doCheck(config)
            }
        )
    }

    return {
        check: check
    }

})