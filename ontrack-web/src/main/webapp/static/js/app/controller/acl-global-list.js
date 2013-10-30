define(['render'], function (render) {

    return {
        url: 'ui/admin/acl/global',
        render: render.asTableTemplate(
            'acl-global-list'
        )
    }

});