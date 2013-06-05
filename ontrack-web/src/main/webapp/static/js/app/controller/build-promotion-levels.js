define(['jquery', 'render', 'ajax', 'dynamic'], function ($, render, ajax, dynamic) {

    function promotionLevelRemove(project, branch, build, promotionLevel) {
        ajax.del({
            url: 'ui/manage/project/{0}/branch/{1}/build/{2}/promotion_level/{3}'.format(
                project,
                branch,
                build,
                promotionLevel
            ),
            successFn: function () {
                dynamic.reloadSection('build-promotion-levels');
            }
        });
    }

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
                    build: config.build,
                    admin: (config.admin == 'true'),
                    promotionLevels: promotionLevels
                }
            },
            function (config) {
                $('.promotion-level-remove').each(function (index, action) {
                    var promotionLevel = $(action).attr('data-promotionlevel');
                    $(action).unbind('click');
                    $(action).click(function () {
                        promotionLevelRemove(config.project, config.branch, config.build, promotionLevel);
                    });
                });
            })
    }

});