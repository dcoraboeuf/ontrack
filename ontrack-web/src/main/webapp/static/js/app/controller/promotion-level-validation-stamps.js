define(['render'], function (render) {

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/promotion_level/{2}/validation_stamps'.format(
                config.project, config.branch, config.promotionLevel);
        },
        render: render.asSimpleTemplate('promotion-level-validation-stamps')
    }

})