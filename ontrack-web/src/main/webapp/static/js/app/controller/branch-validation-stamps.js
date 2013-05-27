define(['render'], function (render) {

    return {
        url: function (config) {
            return 'ui/manage/project/{0}/branch/{1}/validation_stamp'.format(config.project, config.branch);
        },
        preProcessingFn: function (config, stamps, append) {
            var count = stamps.length;
            $.each(stamps, function (index, stamp) {
                stamp.admin = (config.admin == 'true');
                if (index == 0) {
                    stamp.first = true;
                }
                if (index == count - 1) {
                    stamp.last = true;
                }
            });
            return stamps;
        },
        render: render.asTableTemplate('branch-validation-stamp')
    }

});