define(['render'], function (render) {

    return {
        url: function (config) {
            return 'ui/dashboard/project/{0}/branch/{1}'.format(config.project, config.branch)
        },
        render: render.asSimpleTemplate('dashboard-branch')
    }

})