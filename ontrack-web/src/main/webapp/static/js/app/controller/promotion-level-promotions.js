define(['render'], function (render) {

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/promotion_level/{2}/promotions?u=1'.format(
                config.project, config.branch, config.promotion_level);
        },
        render: render.asTableTemplate('promotion-level-promotions')
    }

});