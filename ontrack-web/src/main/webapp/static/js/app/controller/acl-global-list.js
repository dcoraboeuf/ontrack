define(['jquery', 'render', 'ajax', 'dynamic'], function ($, render, ajax, dynamic) {

    return {
        url: 'ui/admin/acl/global',
        render: render.asTableTemplate(
            'acl-global-list',
            function () {
                $('#acl-global-list').find('.acl-global-delete').each(function (i, e) {
                    var account = $(e).attr('data-account');
                    var fn = $(e).attr('data-fn');
                    $(e).click(function () {
                        ajax.del({
                            url: 'ui/admin/acl/global/{0}/{1}'.format(account, fn),
                            loading: {
                                el: $(e)
                            },
                            successFn: function () {
                                dynamic.reloadSection('acl-global-list')
                            }
                        })
                    })
                })
            }
        )
    }

});