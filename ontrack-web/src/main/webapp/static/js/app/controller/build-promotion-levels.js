define(['jquery','render'], function($, render) {

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/build/{2}/promotionLevels'.format(
                config.project,
                config.branch,
                config.build
            )
        },
        render: render.asSimpleTemplate(
            'build-promotion-levels',
            function (promotionLevels, config) {
                return {
                    project: config.project,
                    branch: config.branch,
                    promotionLevels: promotionLevels
                }
            })
    }

});