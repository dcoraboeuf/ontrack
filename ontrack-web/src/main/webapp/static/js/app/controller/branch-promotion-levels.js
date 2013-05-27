define(['render'], function (render) {

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/promotion_level'.format(config.project, config.branch);
        },
        render: render.asTableTemplate('branch-promotion-level')
    }

});