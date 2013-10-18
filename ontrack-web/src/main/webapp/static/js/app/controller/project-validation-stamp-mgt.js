define(['render'], function (render) {

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch'.format(config.project)
        },
        render: render.asSimpleTemplate(
            'project-validation-stamp-mgt',
            function (branches, config) {
                return {
                    branchType: config.branchType,
                    branches: branches
                }
            }
        )
    }

});