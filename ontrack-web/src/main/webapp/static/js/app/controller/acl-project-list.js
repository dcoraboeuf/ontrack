define(['jquery', 'render', 'ajax', 'dynamic'], function ($, render, ajax, dynamic) {

    return {
        url: function (config) {
            return 'ui/admin/acl/project/{0}'.format(config.project)
        },
        render: render.asTableTemplate(
            'acl-project-list',
            function (config) {
                $('#acl-project-list').find('.acl-project-delete').each(function (i, e) {
                    var account = $(e).attr('data-account');
                    $(e).click(function () {
                        ajax.del({
                            url: 'ui/admin/acl/project/{0}/{1}'.format(config.project, account),
                            loading: {
                                el: $(e)
                            },
                            successFn: function () {
                                dynamic.reloadSection('acl-project-list')
                            }
                        })
                    })
                })
            }
        )
    }

});