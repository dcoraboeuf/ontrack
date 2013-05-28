define(['render'], function (render) {

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/validation_stamp/{2}/validation_run?u=1'.format(config.project, config.branch, config.validation_stamp)
        },
        render: render.asTableTemplate('validation-stamp-history')
    }

});